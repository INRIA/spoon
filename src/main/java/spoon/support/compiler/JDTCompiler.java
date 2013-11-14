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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
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

	private class JDTBatchCompiler extends Main {
		public JDTBatchCompiler(PrintWriter outWriter, PrintWriter errWriter) {
			super(outWriter, errWriter, false, null, null);
		}

		public JDTBatchCompiler() {
			super(new PrintWriter(System.out), new PrintWriter(System.err),
					false, null, null);
		}

		@Override
		public CompilationUnit[] getCompilationUnits() {
			CompilationUnit[] units = super.getCompilationUnits();
			for (int i = 0; i < units.length; i++) {
				CompilationUnit unit = units[i];
				units[i] = new CompilationUnitWrapper(unit);
			}
			return units;
		}
		
	}

	// private Logger logger = Logger.getLogger(SpoonBuildingManager.class);

	public int javaCompliance = 7;

	String sourceClasspath = null;

	String templateClasspath = null;

	File outputDirectory;

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
		List<SpoonFile> files = new ArrayList<SpoonFile>();
		SpoonFile file = new FileSystemFile(new File(
				"./src/main/java/spoon/support/builder/SpoonCompiler.java"));
		files.add(file);
		System.out.println(file.getPath());
		try {
			comp.build(files);
			System.out.println(comp.getFactory().Package()
					.get("spoon.support.builder").getTypes());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private JDTBatchCompiler createBatchCompiler() {
		return new JDTBatchCompiler();
	}

	public boolean build(List<SpoonFile> files) throws Exception {
		if (files.isEmpty())
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
		Set<String> paths = new HashSet<String>();
		for (SpoonFile file : files) {
			// We can not use file.getPath() because of in-memory code or files
			// within archives
			paths.add(file.getFileSystemParent().getPath());
		}
		args.addAll(paths);

		getFactory().getEnvironment().debugMessage("build args: " + args);

		batchCompiler.configure(args.toArray(new String[0]));
		CompilationUnitDeclaration[] units = getUnits(batchCompiler, files);

		// here we build the model
		JDTTreeBuilder builder = new JDTTreeBuilder(factory);
		for (CompilationUnitDeclaration unit : units) {
			unit.traverse(builder, unit.scope);
		}

		return probs.size() == 0;
	}

	public boolean buildTemplates(List<SpoonFile> files) throws Exception {
		if (files.isEmpty())
			return true;
		JDTBatchCompiler batchCompiler = createBatchCompiler();
		List<String> args = new ArrayList<String>();
		args.add("-1." + javaCompliance);
		args.add("-preserveAllLocals");
		args.add("-enableJavadoc");
		args.add("-noExit");
		args.add("-verbose");
		// args.add("-d");
		// args.add("none");
		// args.add("-g");
		// args.add("-nowarn");

		if (templateClasspath != null) {
			String fullClasspath = templateClasspath + File.pathSeparator + ".";
			args.add("-cp");
			args.add(fullClasspath);
			Set<String> paths = new HashSet<String>();
			String sourcePaths = "";
			for (SpoonFile file : files) {
				if (!paths.contains(file.getFileSystemParent().getPath())) {
					sourcePaths += file.getParent().getPath()
							+ File.pathSeparator;
				}
				paths.add(file.getPath());
			}
			args.add("-sourcepath");
			args.add(sourcePaths.substring(0, sourcePaths.length() - 1));
			args.addAll(paths);
			//args.add(".");
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
		CompilationUnitDeclaration[] units = getUnits(batchCompiler, files);

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

	public CompilationUnitDeclaration[] getUnits(JDTBatchCompiler compiler,
			List<SpoonFile> streams) throws Exception {
		compiler.startTime = System.currentTimeMillis();
		INameEnvironment environment = this.environment;
		if (environment == null)
			environment = compiler.getLibraryAccess();
		TreeBuilderCompiler batchCompiler = new TreeBuilderCompiler(
				environment, compiler.getHandlingPolicy(), compiler.options,
				this.requestor, compiler.getProblemFactory(), this.out, false);
		CompilationUnitDeclaration[] units = batchCompiler
				.buildUnits(getCompilationUnits(streams, factory));
		return units;
	}

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

	public void addInputSource(SpoonResource source) throws IOException {
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

	public void addTemplateSource(SpoonResource source) throws IOException {
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
		srcSuccess = build(sources.getAllJavaFiles());
		reportProblems(factory.getEnvironment());
		factory.getEnvironment().debugMessage(
				"built in " + (System.currentTimeMillis() - t) + " ms");
		factory.getEnvironment().debugMessage(
				"building templates: " + templates.getAllJavaFiles());
		t = System.currentTimeMillis();
		templateSuccess = buildTemplates(templates.getAllJavaFiles());
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
		// TODO Auto-generated method stub
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean compile() {
		factory.getEnvironment().debugMessage(
				"compiling sources: "
						+ factory.CompilationUnit().getMap().keySet());
		long t = System.currentTimeMillis();
		javaCompliance = factory.getEnvironment().getComplianceLevel();

		Main batchCompiler = createBatchCompiler();
		List<String> args = new ArrayList<String>();
		args.add("-1." + javaCompliance);
		args.add("-preserveAllLocals");
		args.add("-enableJavadoc");
		args.add("-noExit");
		args.add("-d");
		args.add(getDestinationDirectory().getAbsolutePath());

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

		// args.addAll(factory.CompilationUnit().getMap().keySet());

		Set<String> paths = new HashSet<String>();
		String sourcePaths = "";
		for (SpoonFile file : sources.getAllJavaFiles()) {
			// We can not use file.getPath() because when using in-memory code
			// (e.g. snippets)
			// there is no real file on the disk
			// In this case, the virtual parent of the virtual file is "." (by
			// convention)
			// and we are sure it exists
			// However, if . contains a lot of subfolders and Java files, it
			// will take a lot of time
			if (!paths.contains(file.getFileSystemParent().getPath())) {
				sourcePaths += file.getParent().getPath() + File.pathSeparator;
			}
			paths.add(file.getFileSystemParent().getPath());
		}
		args.add("-sourcepath");
		args.add(sourcePaths.substring(0, sourcePaths.length() - 1));
		args.addAll(paths);

		getFactory().getEnvironment().debugMessage("compile args: " + args);

		batchCompiler.compile(args.toArray(new String[0]));

		factory.getEnvironment().debugMessage(
				"compiled in " + (System.currentTimeMillis() - t) + " ms");
		return probs.size() == 0;

	}

	Factory factory;

	class CompilationUnitWrapper extends CompilationUnit {
		public CompilationUnitWrapper(CompilationUnit wrappedUnit) {
			super(null, wrappedUnit.fileName != null ? new String(
					wrappedUnit.fileName) : null, null,
					wrappedUnit.destinationPath != null ? new String(
							wrappedUnit.destinationPath) : null, false);
		}

		@Override
		public char[] getContents() {
			if (factory != null
					&& factory.CompilationUnit().getMap()
							.containsKey(new String(getFileName()))) {
				try {
					return IOUtils
							.toCharArray(getCompilationUnitInputStream(new String(
									getFileName())));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return super.getContents();
		}

	}

	boolean writePackageAnnotationFile = true;

	public void generateProcessedSourceFiles() {
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

		List<File> printedFiles = new ArrayList<File>();
		for (spoon.reflect.cu.CompilationUnit cu : factory.CompilationUnit()
				.getMap().values()) {

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

		Main batchCompiler = createBatchCompiler();
		List<String> args = new ArrayList<String>();
		args.add("-1." + javaCompliance);
		args.add("-preserveAllLocals");
		args.add("-enableJavadoc");
		args.add("-noExit");
		args.add("-d");
		args.add(getDestinationDirectory().getAbsolutePath());

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

		Set<String> paths = new HashSet<String>();
		for (SpoonFile file : sources.getAllJavaFiles()) {
			paths.add(file.getParent().getPath());
		}
		args.addAll(paths);

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

}
