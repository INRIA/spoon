/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
 * 
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify 
 * and/or redistribute the software under the terms of the CeCILL-C license as 
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *  
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */

package spoon.support.compiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.util.Util;

import spoon.compiler.Environment;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.processing.Severity;
import spoon.reflect.Factory;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.FragmentDrivenJavaPrettyPrinter;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

public class JDTCompiler implements SpoonCompiler {

	protected class JDTBatchCompiler extends Main {

		private boolean useFactory;

		public JDTBatchCompiler() {
			this(true);
		}

		public JDTBatchCompiler(boolean useFactory) {
			super(new PrintWriter(System.out), new PrintWriter(
			/* new NullOutputStream() */System.err), false, null, null);
			this.useFactory = useFactory;
		}

		public ICompilerRequestor getBatchRequestor() {
			final ICompilerRequestor r = super.getBatchRequestor();
			return new ICompilerRequestor() {
				public void acceptResult(CompilationResult compilationResult) {
					if (compilationResult.hasErrors()) {
						probs.add(compilationResult.problems);
					}
					r.acceptResult(compilationResult);
				}
			};
		}

		private Set<String> ignoredFiles = new HashSet<>();

		public void ignoreFile(String filePath) {
			ignoredFiles.add(filePath);
		}

		@Override
		public CompilationUnit[] getCompilationUnits() {
			CompilationUnit[] units = super.getCompilationUnits();
			if (!ignoredFiles.isEmpty()) {
				List<CompilationUnit> l = new ArrayList<>();
				for (CompilationUnit unit : units) {
					if (!ignoredFiles.contains(new String(unit.getFileName()))) {
						l.add(unit);
					}
				}
				units = l.toArray(new CompilationUnit[0]);
			}
			if (useFactory) {
				for (int i = 0; i < units.length; i++) {
					CompilationUnit unit = units[i];
					units[i] = new CompilationUnitWrapper(unit);
				}
			}
			return units;
		}

		public CompilationUnit[] getCompilationUnits(List<SpoonFile> files) {
			Set<String> fileNames = new HashSet<>();
			List<SpoonFile> virtualFiles = new ArrayList<>();
			for (SpoonFile f : files) {
				if (!f.isActualFile()) {
					virtualFiles.add(f);
				} else {
					fileNames.add(f.getPath());
				}
			}

			List<CompilationUnit> culist = new ArrayList<>();
			CompilationUnit[] units = getCompilationUnits();
			for (CompilationUnit unit : units) {
				if (fileNames.contains(new String(unit.getFileName()))) {
					culist.add(unit);
				}
			}
			for (SpoonFile f : virtualFiles) {
				try {
					culist.add(new CompilationUnit(IOUtils.toCharArray(f
							.getContent()), f.getName(), null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return culist.toArray(new CompilationUnit[0]);
		}

		public CompilationUnitDeclaration[] getUnits(List<SpoonFile> files)
				throws Exception {
			startTime = System.currentTimeMillis();
			INameEnvironment environment = JDTCompiler.this.environment;
			if (environment == null) {
				environment = getLibraryAccess();
			}
			CompilerOptions compilerOptions = new CompilerOptions(this.options);
			compilerOptions.parseLiteralExpressionsAsConstants = false;
			TreeBuilderCompiler treeBuilderCompiler = new TreeBuilderCompiler(
					environment, getHandlingPolicy(), compilerOptions,
					JDTCompiler.this.requestor, getProblemFactory(), this.out,
					null);
			CompilationUnitDeclaration[] units = treeBuilderCompiler
					.buildUnits(getCompilationUnits(files));
			return units;
		}

		private class TreeBuilderCompiler extends
				org.eclipse.jdt.internal.compiler.Compiler {

			public TreeBuilderCompiler(INameEnvironment environment,
					IErrorHandlingPolicy policy, CompilerOptions options,
					ICompilerRequestor requestor,
					IProblemFactory problemFactory, PrintWriter out,
					CompilationProgress progress) {
				super(environment, policy, options, requestor, problemFactory,
						out, progress);
			}

			public CompilationUnitDeclaration[] buildUnits(
					CompilationUnit[] sourceUnits) {

				// //////////////////////////////////////////////////////////////////////////
				// This code is largely inspired from JDT's
				// CompilationUnitResolver.resolve

				CompilationUnitDeclaration unit = null;
				int i = 0;
				// build and record parsed units
				beginToCompile(sourceUnits);

				// process all units (some more could be injected in the loop by
				// the lookup environment)
				for (; i < this.totalUnits; i++) {
					unit = unitsToProcess[i];
					// System.err.println(unit);
					this.parser.getMethodBodies(unit);

					// fault in fields & methods
					if (unit.scope != null)
						unit.scope.faultInTypes();

					// verify inherited methods
					if (unit.scope != null)
						unit.scope.verifyMethods(lookupEnvironment
								.methodVerifier());

					// type checking
					unit.resolve();
					// flow analysis
					unit.analyseCode();

					unit.ignoreFurtherInvestigation = false;
					requestor.acceptResult(unit.compilationResult);
				}

				return this.unitsToProcess;
			}
		}

	}

	// private Logger logger = Logger.getLogger(SpoonBuildingManager.class);

	public int javaCompliance = 7;

	String sourceClasspath = null;

	String templateClasspath = null;

	File outputDirectory;

	boolean buildOnlyOutdatedFiles = false;

	@Override
	public File getOutputDirectory() {
		return outputDirectory;
	}

	@Override
	public void setOutputDirectory(File outputDirectory) throws IOException {
		this.outputDirectory = outputDirectory;
	}

	File destinationDirectory;

	@Override
	public File getDestinationDirectory() {
		return destinationDirectory;
	}

	@Override
	public void setDestinationDirectory(File desinationDirectory)
			throws IOException {
		this.destinationDirectory = desinationDirectory;
	}

	public JDTCompiler(Factory factory, PrintWriter outWriter,
			PrintWriter errWriter) {
		// super(outWriter, errWriter, false, null, null);
		this.factory = factory;
	}

	public JDTCompiler(Factory factory) {
		// super(new PrintWriter(System.out), new PrintWriter(System.err),
		// false,
		// null, null);
		this.factory = factory;
	}

	// example usage (please do not use directly, use instead the spoon.Spoon
	// API to create the factory)
	public static void main(String[] args) {
		JDTCompiler comp = new JDTCompiler(new Factory(
				new DefaultCoreFactory(), new StandardEnvironment()));
		comp.createBatchCompiler().printUsage();
		SpoonFile file = new FileSystemFile(new File(
				"./src/main/java/spoon/support/compiler/JDTCompiler.java"));
		comp.addInputSource(file);
		try {
			comp.build();
			System.out.println(comp.getFactory().Package()
					.get("spoon.support.compiler").getTypes());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected JDTBatchCompiler createBatchCompiler(boolean useFactory) {
		return new JDTBatchCompiler(useFactory);
	}

	protected JDTBatchCompiler createBatchCompiler() {
		return createBatchCompiler(false);
	}

	protected void keepOutdatedFiles(List<SpoonFile> files,
			Collection<File> outputFiles) {
		// System.out.println("outputfiles: " + outputFiles);

		int offset = outputDirectory.getAbsolutePath().length() + 1;
		Collection<String> relativeOutputPaths = new ArrayList<>();
		for (File f : outputFiles) {
			relativeOutputPaths.add(f.getAbsolutePath().substring(offset));
		}
		for (SpoonFile sf : new ArrayList<SpoonFile>(files)) {
			File f = sf.toFile();
			for (String s : relativeOutputPaths) {
				if (f.getAbsolutePath().endsWith(s)) {
					if (f.lastModified() <= new File(outputDirectory, s)
							.lastModified()) {
						files.remove(sf);
					}
				}
			}
		}
		// System.out.println("filtered: " + files);
	}

	protected boolean buildSources() throws Exception {
		if (sources.getRootJavaPaths().isEmpty())
			return true;
		// long t=System.currentTimeMillis();
		// Build input
		JDTBatchCompiler batchCompiler = createBatchCompiler();
		List<String> args = new ArrayList<String>();
		args.add("-1." + javaCompliance);
		args.add("-preserveAllLocals");
		args.add("-enableJavadoc");
		args.add("-noExit");
		// args.add("-d");
		// args.add("none");

		if (destinationDirectory != null) {
			sourceClasspath += File.pathSeparator
					+ destinationDirectory.getAbsolutePath();
		}
		if (sourceClasspath != null) {
			args.add("-cp");
			args.add(sourceClasspath);
		} else {
			ClassLoader currentClassLoader = Thread.currentThread()
					.getContextClassLoader();// ClassLoader.getSystemClassLoader();
			if (currentClassLoader instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) currentClassLoader).getURLs();
				if (urls != null && urls.length > 0) {
					String classpath = ".";
					for (URL url : urls) {
						classpath += File.pathSeparator + url.getFile();
					}
					if (classpath != null) {
						args.add("-cp");
						args.add(classpath);
					}
				}
			}
		}
		// args.add("-nowarn");
		// Set<String> paths = new HashSet<String>();
		// for (SpoonFile file : files) {
		// // We can not use file.getPath() because of in-memory code or files
		// // within archives
		// paths.add(file.getFileSystemParent().getPath());
		// }
		args.addAll(sources.getRootJavaPaths());

		getFactory().getEnvironment().debugMessage("build args: " + args);

		try {
			batchCompiler.configure(args.toArray(new String[0]));
		} catch (Exception e) {
			System.err.println("build args: " + args);
			System.err.println("sources: " + sources.rootJavaPaths);
			throw e;
		}
		List<SpoonFile> filesToBuild = sources.getAllJavaFiles();
		if (buildOnlyOutdatedFiles) {
			if (outputDirectory.exists()) {
				@SuppressWarnings("unchecked")
				Collection<File> outputFiles = FileUtils.listFiles(
						outputDirectory, new String[] { "java" }, true);
				keepOutdatedFiles(filesToBuild, outputFiles);
			} else {
				keepOutdatedFiles(filesToBuild, new ArrayList<File>());
			}
		}
		CompilationUnitDeclaration[] units = batchCompiler
				.getUnits(filesToBuild);

		// here we build the model
		JDTTreeBuilder builder = new JDTTreeBuilder(factory);
		for (CompilationUnitDeclaration unit : units) {
			unit.traverse(builder, unit.scope);
		}

		return probs.size() == 0;
	}

	// this function is used to hack the JDT compiler...
	protected File createTmpJavaFile(File folder) {
		File f = new File(folder, "Tmp.java");
		if (f.exists()) {
			return f;
		}
		try {
			FileUtils.writeStringToFile(f, "class Tmp {}");
			f.deleteOnExit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}

	protected boolean buildTemplates() throws Exception {
		if (templates.getRootJavaPaths().isEmpty())
			return true;
		JDTBatchCompiler batchCompiler = createBatchCompiler();
		List<String> args = new ArrayList<String>();
		args.add("-1." + javaCompliance);
		args.add("-preserveAllLocals");
		args.add("-enableJavadoc");
		args.add("-noExit");
		// args.add("-verbose");
		// args.add("-d");
		// args.add("none");
		// args.add("-g");
		// args.add("-nowarn");

		if (templateClasspath != null) {
			String fullClasspath = templateClasspath + File.pathSeparator + ".";
			args.add("-cp");
			args.add(fullClasspath);
			// Set<String> paths = new HashSet<String>();
			// String sourcePaths = "";
			// for (SpoonFolder file : templates.getSubFolders()) {
			// if (file.isArchive()) {
			// sourcePaths += file.getPath() + File.pathSeparator;
			// }
			// }
			// for (SpoonFile file : files) {
			// if (!paths.contains(file.getFileSystemParent().getPath())) {
			// sourcePaths += file.getParent().getPath()
			// + File.pathSeparator;
			// }
			// paths.add(file.getPath());
			// }
			// args.add("-sourcepath");
			// args.add(sourcePaths.substring(0, sourcePaths.length() - 1));
			// args.addAll(paths);
			// args.add(".");
			for (SpoonFolder file : templates.getSubFolders()) {
				if (file.isArchive()) {
					// JDT bug HACK
					createTmpJavaFile(file.getFileSystemParent());
				}
			}
			args.addAll(templates.getRootJavaPaths());
		} else {
			// when no class path is defined, we are probably in test and we try
			// to get as much source as we can compiled
			args.add(".");
		}

		getFactory().getEnvironment().debugMessage(
				"template build args: " + args);
		// printUsage();
		// System.out.println("=>" + args);
		batchCompiler.configure(args.toArray(new String[0]));
		CompilationUnitDeclaration[] units = batchCompiler.getUnits(templates
				.getAllJavaFiles());

		// here we build the model in the template factory
		JDTTreeBuilder builder = new JDTTreeBuilder(factory.Template());
		for (CompilationUnitDeclaration unit : units) {
			unit.traverse(builder, unit.scope);
		}

		return probs.size() == 0;

	}

	PrintWriter out;

	/*
	 * Build the set of compilation source units
	 */
	public CompilationUnit[] getCompilationUnits(List<SpoonFile> streams,
			Factory factory) throws Exception {
		CompilationUnit[] units = new CompilationUnit[streams.size()];
		int i = 0;
		for (SpoonFile stream : streams) {
			// TODO: here substitute processed content!!!!
			// factory.CompilationUnit().
			InputStream in = stream.getContent();
			units[i] = new CompilationUnit(Util.getInputStreamAsCharArray(in,
					-1, null), stream.getPath(), null);
			in.close();
			i++;
		}
		return units;
	}

	INameEnvironment environment = null;

	public void setEnvironment(INameEnvironment environment) {
		this.environment = environment;
	}

	// public CompilationUnitDeclaration[] getUnits(JDTBatchCompiler compiler,
	// List<SpoonFile> streams) throws Exception {
	// compiler.startTime = System.currentTimeMillis();
	// INameEnvironment environment = this.environment;
	// if (environment == null)
	// environment = compiler.getLibraryAccess();
	// TreeBuilderCompiler batchCompiler = new TreeBuilderCompiler(
	// environment, compiler.getHandlingPolicy(), compiler.options,
	// this.requestor, compiler.getProblemFactory(), this.out, false);
	// CompilationUnitDeclaration[] units = batchCompiler
	// .buildUnits(getCompilationUnits(streams, factory));
	// return units;
	// }

	final List<CategorizedProblem[]> probs = new ArrayList<CategorizedProblem[]>();

	public final TreeBuilderRequestor requestor = new TreeBuilderRequestor();

	// this class can not be static because it uses the fiel probs
	public class TreeBuilderRequestor implements ICompilerRequestor {

		public void acceptResult(CompilationResult result) {
			if (result.hasErrors()) {
				probs.add(result.problems);
			}
		}

	}

	public List<CategorizedProblem[]> getProblems() {
		return this.probs;
	}

	private boolean build = false;

	VirtualFolder sources = new VirtualFolder();

	VirtualFolder templates = new VirtualFolder();

	@Override
	public void addInputSources(List<SpoonResource> resources) {
		for (SpoonResource r : resources) {
			addInputSource(r);
		}
	}

	@Override
	public void addTemplateSources(List<SpoonResource> resources) {
		for (SpoonResource r : resources) {
			addTemplateSource(r);
		}
	}

	public void addInputSource(SpoonResource source) {
		if (source.isFile())
			this.sources.addFile((SpoonFile) source);
		else
			this.sources.addFolder((SpoonFolder) source);
	}

	public void addInputSource(File source) throws IOException {
		if (SpoonResourceHelper.isFile(source))
			this.sources.addFile(SpoonResourceHelper.createFile(source));
		else
			this.sources.addFolder(SpoonResourceHelper.createFolder(source));
	}

	public void addTemplateSource(SpoonResource source) {
		if (source.isFile())
			this.templates.addFile((SpoonFile) source);
		else
			this.templates.addFolder((SpoonFolder) source);
	}

	public void addTemplateSource(File source) throws IOException {
		if (SpoonResourceHelper.isFile(source))
			this.templates.addFile(SpoonResourceHelper.createFile(source));
		else
			this.templates.addFolder(SpoonResourceHelper.createFolder(source));
	}

	public boolean build() throws Exception {
		if (factory == null) {
			throw new Exception("Factory not initialized");
		}
		if (build) {
			throw new Exception("Model already built");
		}
		build = true;

		boolean srcSuccess, templateSuccess;
		factory.getEnvironment().debugMessage(
				"building sources: " + sources.getAllJavaFiles());
		long t = System.currentTimeMillis();
		javaCompliance = factory.getEnvironment().getComplianceLevel();
		srcSuccess = buildSources();
		reportProblems(factory.getEnvironment());
		factory.getEnvironment().debugMessage(
				"built in " + (System.currentTimeMillis() - t) + " ms");
		factory.getEnvironment().debugMessage(
				"building templates: " + templates.getAllJavaFiles());
		t = System.currentTimeMillis();
		templateSuccess = buildTemplates();
		// factory.Template().parseTypes();
		factory.getEnvironment().debugMessage(
				"built in " + (System.currentTimeMillis() - t) + " ms");
		return srcSuccess && templateSuccess;
	}

	protected void report(Environment environment, CategorizedProblem problem) {
		if (problem == null) {
			System.out.println("cannot report null problem");
			return;
		}
		File file = new File(new String(problem.getOriginatingFileName()));
		String filename = file.getAbsolutePath();
		environment.report(
				null,
				problem.isError() ? Severity.ERROR
						: problem.isWarning() ? Severity.WARNING
								: Severity.MESSAGE,

				problem.getMessage() + " at " + filename + ":"
						+ problem.getSourceLineNumber());
	}

	public void reportProblems(Environment environment) {
		if (getProblems().size() > 0) {
			for (CategorizedProblem[] cps : getProblems()) {
				for (int i = 0; i < cps.length; i++) {
					CategorizedProblem problem = cps[i];
					if (problem != null) {
						report(environment, problem);
					}
				}
			}
		}
	}

	public Set<File> getInputSources() {
		Set<File> files = new HashSet<File>();
		for (SpoonFolder file : getSource().getSubFolders()) {
			files.add(new File(file.getPath()));
		}
		return files;
	}

	public VirtualFolder getSource() {
		return sources;
	}

	public VirtualFolder getTemplates() {
		return templates;
	}

	public Set<File> getTemplateSources() {
		Set<File> files = new HashSet<File>();
		for (SpoonFolder file : getTemplates().getSubFolders()) {
			files.add(new File(file.getPath()));
		}
		return files;
	}

	@Override
	public boolean compile() {
		factory.getEnvironment().debugMessage(
				"compiling sources: "
						+ factory.CompilationUnit().getMap().keySet());
		long t = System.currentTimeMillis();
		javaCompliance = factory.getEnvironment().getComplianceLevel();

		JDTBatchCompiler batchCompiler = createBatchCompiler(true);
		List<String> args = new ArrayList<String>();
		args.add("-1." + javaCompliance);
		args.add("-preserveAllLocals");
		args.add("-enableJavadoc");
		args.add("-noExit");
		// args.add("-verbose");
		args.add("-proc:none");
		if (getDestinationDirectory() != null) {
			args.add("-d");
			args.add(getDestinationDirectory().getAbsolutePath());
		} else {
			args.add("-d");
			args.add("none");
		}

		// args.add("-d");
		// args.add(getDestinationDirectory().toString());

		String finalClassPath = null;
		if (sourceClasspath != null) {
			finalClassPath = sourceClasspath;
		} else {
			ClassLoader currentClassLoader = Thread.currentThread()
					.getContextClassLoader();// ClassLoader.getSystemClassLoader();
			if (currentClassLoader instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) currentClassLoader).getURLs();
				if (urls != null && urls.length > 0) {
					String classpath = ".";
					for (URL url : urls) {
						classpath += File.pathSeparator + url.getFile();
					}
					if (classpath != null) {
						finalClassPath = classpath;
					}
				}
			}
		}

		args.add("-cp");
		args.add(finalClassPath);

		if (buildOnlyOutdatedFiles) {

			// ignore the files that are not outdated
			if (outputDirectory.exists()) {
				@SuppressWarnings("unchecked")
				Collection<File> outputFiles = FileUtils.listFiles(
						outputDirectory, new String[] { "java" }, true);
				int offset = outputDirectory.getAbsolutePath().length() + 1;
				Collection<String> relativeOutputPaths = new ArrayList<>();
				for (File f : outputFiles) {
					relativeOutputPaths.add(f.getAbsolutePath().substring(
							offset));
				}
				for (SpoonFile sf : sources.getAllJavaFiles()) {
					if (factory.CompilationUnit().getMap()
							.containsKey(sf.getPath())) {
						continue;
					}
					File source = sf.toFile();
					for (String out : relativeOutputPaths) {
						if (source.getAbsolutePath().endsWith(out)) {
							if (source.lastModified() <= new File(
									outputDirectory, out).lastModified()) {
								batchCompiler
										.ignoreFile(new File(outputDirectory,
												out).getAbsolutePath());
							}
						}
					}
				}
			}

			args.add(getOutputDirectory().getAbsolutePath());

		} else {
			args.addAll(sources.getRootJavaPaths());
		}

		getFactory().getEnvironment().debugMessage("compile args: " + args);

		// batchCompiler.batchCompiler.useSingleThread = true;

		System.setProperty("jdt.compiler.useSingleThread", "true");

		batchCompiler.compile(args.toArray(new String[0]));

		reportProblems(factory.getEnvironment());

		factory.getEnvironment().debugMessage(
				"compiled in " + (System.currentTimeMillis() - t) + " ms");
		return probs.size() == 0;

	}

	Factory factory;

	Map<String, char[]> loadedContent = new HashMap<>();

	class CompilationUnitWrapper extends CompilationUnit {
		public CompilationUnitWrapper(CompilationUnit wrappedUnit) {
			super(null, wrappedUnit.fileName != null ? new String(
					wrappedUnit.fileName) : null, null,
					wrappedUnit.destinationPath != null ? new String(
							wrappedUnit.destinationPath) : null, false);
		}

		@Override
		public char[] getContents() {
			String s = new String(getFileName());
			// System.out.println("--- getContent: " + s);
			// new Exception("getContent").printStackTrace();
			if (factory != null
					&& factory.CompilationUnit().getMap().containsKey(s)) {
				try {
					if (loadedContent.containsKey(s)) {
						// System.out.println(new String(loadedContent.get(s)));
						return loadedContent.get(s);
					} else {
						char[] content = IOUtils
								.toCharArray(getCompilationUnitInputStream(s));
						loadedContent.put(s, content);
						return content;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return super.getContents();
		}

	}

	boolean writePackageAnnotationFile = true;

	public void generateProcessedSourceFiles() throws Exception {
		// Check output directory
		if (outputDirectory == null)
			throw new RuntimeException(
					"You should set output directory before generating source files");
		// Create spooned dir
		if (outputDirectory.isFile())
			throw new RuntimeException("Output must be a directory");
		if (!outputDirectory.exists()) {
			if (!outputDirectory.mkdirs())
				throw new RuntimeException("Error creating output directory");
		}
		outputDirectory = outputDirectory.getCanonicalFile();

		factory.getEnvironment().debugMessage(
				"Generating source files to: " + outputDirectory);

		List<File> printedFiles = new ArrayList<File>();
		for (spoon.reflect.cu.CompilationUnit cu : factory.CompilationUnit()
				.getMap().values()) {

			factory.getEnvironment().debugMessage(
					"Generating source for compilation unit: " + cu.getFile());

			CtSimpleType<?> element = cu.getMainType();

			CtPackage pack = element.getPackage();

			// create package directory
			File packageDir;
			if (pack.getQualifiedName()
					.equals(CtPackage.TOP_LEVEL_PACKAGE_NAME)) {
				packageDir = new File(outputDirectory.getAbsolutePath());
			} else {
				// Create current package dir
				packageDir = new File(outputDirectory.getAbsolutePath()
						+ File.separatorChar
						+ pack.getQualifiedName().replace('.',
								File.separatorChar));
			}
			if (!packageDir.exists()) {
				if (!packageDir.mkdirs())
					throw new RuntimeException(
							"Error creating output directory");
			}

			// Create package annotation file
			// if (writePackageAnnotationFile
			// && element.getPackage().getAnnotations().size() > 0) {
			// File packageAnnot = new File(packageDir.getAbsolutePath()
			// + File.separatorChar
			// + DefaultJavaPrettyPrinter.JAVA_PACKAGE_DECLARATION);
			// if (!printedFiles.contains(packageAnnot))
			// printedFiles.add(packageAnnot);
			// try {
			// stream = new PrintStream(packageAnnot);
			// stream.println(printer.getPackageDeclaration());
			// stream.close();
			// } catch (FileNotFoundException e) {
			// e.printStackTrace();
			// } finally {
			// if (stream != null)
			// stream.close();
			// }
			// }

			// print type
			try {
				File file = new File(packageDir.getAbsolutePath()
						+ File.separatorChar + element.getSimpleName()
						+ DefaultJavaPrettyPrinter.JAVA_FILE_EXTENSION);
				file.createNewFile();

				// the path must be given relatively to to the working directory
				InputStream is = getCompilationUnitInputStream(cu.getFile()
						.getPath());

				IOUtils.copy(is, new FileOutputStream(file));

				if (!printedFiles.contains(file)) {
					printedFiles.add(file);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected InputStream getCompilationUnitInputStream(String path) {
		Environment env = factory.getEnvironment();
		spoon.reflect.cu.CompilationUnit cu = factory.CompilationUnit()
				.getMap().get(path);
		List<CtSimpleType<?>> toBePrinted = cu.getDeclaredTypes();

		PrettyPrinter printer = null;

		if (env.isUsingSourceCodeFragments()) {
			try {
				printer = new FragmentDrivenJavaPrettyPrinter(env, cu);
			} catch (Exception e) {
				e.printStackTrace();
				printer = null;
			}
		}
		if (printer == null) {
			printer = new DefaultJavaPrettyPrinter(env);
			printer.calculate(cu, toBePrinted);
		}

		return new ByteArrayInputStream(printer.getResult().toString()
				.getBytes());

	}

	@Override
	public Factory getFactory() {
		return factory;
	}

	@Override
	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	@Override
	public boolean compileInputSources() throws Exception {
		factory.getEnvironment().debugMessage(
				"compiling input sources: " + sources.getAllJavaFiles());
		long t = System.currentTimeMillis();
		javaCompliance = factory.getEnvironment().getComplianceLevel();

		Main batchCompiler = createBatchCompiler(false);
		List<String> args = new ArrayList<String>();
		args.add("-1." + javaCompliance);
		args.add("-preserveAllLocals");
		args.add("-enableJavadoc");
		args.add("-noExit");
		args.add("-proc:none");
		if (getDestinationDirectory() != null) {
			args.add("-d");
			args.add(getDestinationDirectory().getAbsolutePath());
		} else {
			args.add("-d");
			args.add("none");
		}

		String finalClassPath = null;
		if (sourceClasspath != null) {
			finalClassPath = sourceClasspath;
		} else {
			ClassLoader currentClassLoader = Thread.currentThread()
					.getContextClassLoader();// ClassLoader.getSystemClassLoader();
			if (currentClassLoader instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) currentClassLoader).getURLs();
				if (urls != null && urls.length > 0) {
					String classpath = ".";
					for (URL url : urls) {
						classpath += File.pathSeparator + url.getFile();
					}
					if (classpath != null) {
						finalClassPath = classpath;
					}
				}
			}
		}

		args.add("-cp");
		args.add(finalClassPath);

		// Set<String> paths = new HashSet<String>();
		// for (SpoonFile file : sources.getAllJavaFiles()) {
		// paths.add(file.getParent().getPath());
		// }
		// args.addAll(paths);

		args.addAll(sources.getRootJavaPaths());

		// configure(args.toArray(new String[0]));

		batchCompiler.compile(args.toArray(new String[0]));

		factory.getEnvironment().debugMessage(
				"compiled in " + (System.currentTimeMillis() - t) + " ms");
		return probs.size() == 0;

	}

	@Override
	public String getSourceClasspath() {
		return sourceClasspath;
	}

	@Override
	public String getTemplateClasspath() {
		return templateClasspath;
	}

	@Override
	public void setSourceClasspath(String classpath) {
		this.sourceClasspath = classpath;
	}

	@Override
	public void setTemplateClasspath(String classpath) {
		this.templateClasspath = classpath;
	}

	@Override
	public void setBuildOnlyOutdatedFiles(boolean buildOnlyOutdatedFiles) {
		this.buildOnlyOutdatedFiles = buildOnlyOutdatedFiles;
	}

}
