/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
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
package spoon.support.compiler.jdt;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.compiler.ModelBuildingException;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.compiler.builder.AdvancedOptions;
import spoon.compiler.builder.AnnotationProcessingOptions;
import spoon.compiler.builder.ClasspathOptions;
import spoon.compiler.builder.ComplianceOptions;
import spoon.compiler.builder.JDTBuilder;
import spoon.compiler.builder.JDTBuilderImpl;
import spoon.compiler.builder.SourceOptions;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.AstParentConsistencyChecker;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.Query;
import spoon.support.sniper.SniperJavaPrettyPrinter;
import spoon.support.QueueProcessingManager;
import spoon.support.comparator.FixedOrderBasedOnFileNameCompilationUnitComparator;
import spoon.support.compiler.SpoonProgress;
import spoon.support.compiler.VirtualFolder;
import spoon.support.modelobs.SourceFragmentCreator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main class of Spoon to build the model.
 * Highly depends on {@link JDTBatchCompiler} for performing the job.
 */
public class JDTBasedSpoonCompiler implements spoon.SpoonModelBuilder {
	protected INameEnvironment environment = null;
	protected final List<CategorizedProblem> probs = new ArrayList<>();
	protected final TreeBuilderRequestor requestor = new TreeBuilderRequestor(this);
	protected Factory factory;
	protected int javaCompliance = 7;
	//list of java files or folders with java files which represents source of the CtModel
	protected SpoonFolder sources = new VirtualFolder();
	//list of java files or folders with java files which represents templates. Templates are added to CtModel too.
	protected SpoonFolder templates = new VirtualFolder();
	//The classpath used to build templates
	protected String[] templateClasspath = new String[0];
	protected List<CompilationUnitFilter> compilationUnitFilters = new ArrayList<>();
	private boolean sortList;

	/**
	 * Default constructor
	 */
	public JDTBasedSpoonCompiler(Factory factory) {
		this.factory = factory;
		this.initializeCUCOmparator();
	}

	private void initializeCUCOmparator() {
		this.sortList = System.getenv("SPOON_SEED_CU_COMPARATOR") == null;
	}

	@Override
	public boolean build() {
		return build(null);
	}

	@Override
	public boolean build(JDTBuilder builder) {
		if (factory == null) {
			throw new SpoonException("Factory not initialized");
		}
		if (factory.getModel() != null && factory.getModel().isBuildModelFinished()) {
			throw new SpoonException("Model already built");
		}

		boolean srcSuccess;
		boolean templateSuccess;
		factory.getEnvironment().debugMessage("building sources: " + sources.getAllJavaFiles());
		long t = System.currentTimeMillis();
		javaCompliance = factory.getEnvironment().getComplianceLevel();
		srcSuccess = buildSources(builder);

		reportProblems(factory.getEnvironment());

		factory.getEnvironment().debugMessage("built in " + (System.currentTimeMillis() - t) + " ms");
		factory.getEnvironment().debugMessage("building templates: " + templates.getAllJavaFiles());
		t = System.currentTimeMillis();
		templateSuccess = buildTemplates(builder);
		factory.getEnvironment().debugMessage("built in " + (System.currentTimeMillis() - t) + " ms");
		checkModel();
		factory.getModel().setBuildModelIsFinished(true);

		if (factory.getEnvironment().createPrettyPrinter() instanceof SniperJavaPrettyPrinter) {
			//setup a model change collector
			new SourceFragmentCreator().attachTo(factory.getEnvironment());
		}

		return srcSuccess && templateSuccess;
	}

	private void checkModel() {
		if (!factory.getEnvironment().checksAreSkipped()) {
			factory.getModel().getUnnamedModule().accept(new AstParentConsistencyChecker());
		}
	}

	@Override
	public boolean compile(InputType... types) {
		factory.getEnvironment().debugMessage("compiling sources: " + factory.CompilationUnit().getMap().keySet());
		long t = System.currentTimeMillis();
		javaCompliance = factory.getEnvironment().getComplianceLevel();

		JDTBatchCompiler batchCompiler = createBatchCompiler(types);


		final String[] args = new JDTBuilderImpl() //
				.classpathOptions(new ClasspathOptions().encoding(this.getEnvironment().getEncoding().displayName()).classpath(getSourceClasspath()).binaries(getBinaryOutputDirectory())) //
				.complianceOptions(new ComplianceOptions().compliance(javaCompliance)) //
				.annotationProcessingOptions(new AnnotationProcessingOptions().compileProcessors()) //
				.advancedOptions(new AdvancedOptions().preserveUnusedVars().continueExecution().enableJavadoc()) //
				.sources(new SourceOptions().sources(sources.getAllJavaFiles())) // no sources, handled by the JDTBatchCompiler
				.build();

		getFactory().getEnvironment().debugMessage("compile args: " + Arrays.toString(args));
		System.setProperty("jdt.compiler.useSingleThread", "true");
		batchCompiler.compile(args);

		reportProblems(factory.getEnvironment());
		factory.getEnvironment().debugMessage("compiled in " + (System.currentTimeMillis() - t) + " ms");
		return probs.isEmpty();
	}

	@Override
	public void instantiateAndProcess(List<String> processors) {
		// processing (consume all the processors)
		ProcessingManager processing = new QueueProcessingManager(factory);
		for (String processorName : processors) {
			processing.addProcessor(processorName);
			factory.getEnvironment().debugMessage("Loaded processor " + processorName + ".");
		}

		processing.process(factory.Package().getRootPackage());
	}

	@Override
	public void process(Collection<Processor<? extends CtElement>> processors) {
		// processing (consume all the processors)
		ProcessingManager processing = new QueueProcessingManager(factory);
		for (Processor<? extends CtElement> processorName : processors) {
			processing.addProcessor(processorName);
			factory.getEnvironment().debugMessage("Loaded processor " + processorName + ".");
		}

		processing.process(factory.Package().getRootPackage());
	}

	@Override
	public void generateProcessedSourceFiles(OutputType outputType) {
		generateProcessedSourceFiles(outputType, null);
	}

	@Override
	public void generateProcessedSourceFiles(OutputType outputType, Filter<CtType<?>> typeFilter) {
		if (getEnvironment().getSpoonProgress() != null) {
			getEnvironment().getSpoonProgress().start(SpoonProgress.Process.PRINT);
		}
		switch (outputType) {
		case CLASSES:
			generateProcessedSourceFilesUsingTypes(typeFilter);
			break;
		case COMPILATION_UNITS:
			generateProcessedSourceFilesUsingCUs();
			break;
		case NO_OUTPUT:
		}
		if (getEnvironment().getSpoonProgress() != null) {
			getEnvironment().getSpoonProgress().end(SpoonProgress.Process.PRINT);
		}
	}

	@Override
	public void addInputSource(File source) {
		try {
			if (SpoonResourceHelper.isFile(source)) {
				this.sources.addFile(SpoonResourceHelper.createFile(source));
			} else {
				this.sources.addFolder(SpoonResourceHelper.createFolder(source));
			}
		} catch (Exception e) {
			throw new SpoonException(e);
		}
	}

	@Override
	public void addInputSource(SpoonResource source) {
		if (source.isFile()) {
			this.sources.addFile((SpoonFile) source);
		} else {
			this.sources.addFolder((SpoonFolder) source);
		}
	}

	@Override
	public void addInputSources(List<SpoonResource> resources) {
		for (SpoonResource r : resources) {
			addInputSource(r);
		}
	}

	@Override
	public Set<File> getInputSources() {
		Set<File> files = new HashSet<>();
		for (SpoonFolder file : getSource().getSubFolders()) {
			files.add(new File(file.getPath()));
		}
		return files;
	}

	@Override
	public void addTemplateSource(SpoonResource source) {
		if (source.isFile()) {
			this.templates.addFile((SpoonFile) source);
		} else {
			this.templates.addFolder((SpoonFolder) source);
		}
	}

	@Override
	public void addTemplateSource(File source) {
		try {
			if (SpoonResourceHelper.isFile(source)) {
				this.templates.addFile(SpoonResourceHelper.createFile(source));
			} else {
				this.templates.addFolder(SpoonResourceHelper.createFolder(source));
			}
		} catch (Exception e) {
			throw new SpoonException(e);
		}
	}

	@Override
	public void addTemplateSources(List<SpoonResource> resources) {
		for (SpoonResource r : resources) {
			addTemplateSource(r);
		}
	}

	@Override
	public Set<File> getTemplateSources() {
		Set<File> files = new HashSet<>();
		for (SpoonFolder file : getTemplates().getSubFolders()) {
			files.add(new File(file.getPath()));
		}
		return files;
	}

	@Override
	public File getSourceOutputDirectory() {
		return this.factory.getEnvironment().getSourceOutputDirectory();
	}

	@Override
	public void setBinaryOutputDirectory(File binaryOutputDirectory) {
		this.getEnvironment().setBinaryOutputDirectory(binaryOutputDirectory.getAbsolutePath());
	}

	@Override
	public File getBinaryOutputDirectory() {
		return new File(getEnvironment().getBinaryOutputDirectory());
	}

	@Override
	public String[] getSourceClasspath() {
		return getEnvironment().getSourceClasspath();
	}

	@Override
	public void setSourceClasspath(String... classpath) {
		getEnvironment().setSourceClasspath(classpath);
	}

	@Override
	public String[] getTemplateClasspath() {
		return templateClasspath;
	}

	@Override
	public void setTemplateClasspath(String... classpath) {
		this.templateClasspath = classpath;
	}

	@Override
	public Factory getFactory() {
		return factory;
	}

	protected boolean buildSources(JDTBuilder jdtBuilder) {
		return buildUnitsAndModel(jdtBuilder, sources, getSourceClasspath(), "");
	}

	protected JDTBatchCompiler createBatchCompiler() {
		return new JDTBatchCompiler(this);
	}

	protected JDTBatchCompiler createBatchCompiler(InputType... types) {
		JDTBatchCompiler batchCompiler = createBatchCompiler();
		// backward compatible
		if (types.length == 0) {
			types = new InputType[]{InputType.CTTYPES};
		}
		for (InputType inputType : types) {
			inputType.initializeCompiler(batchCompiler);
		}
		return batchCompiler;
	}

	protected boolean buildTemplates(JDTBuilder jdtBuilder) {
		return buildUnitsAndModel(jdtBuilder, templates, getTemplateClasspath(), "template ");
	}

	/**
	 * Get the units from the given source folder and build the Spoon Model.
	 * @param jdtBuilder The instance of JDTBuilder to use to prepare the right arguments
	 * @param sourcesFolder The source folder
	 * @param classpath The complete classpath
	 * @param debugMessagePrefix Useful to help debugging
	 * @param buildOnlyOutdatedFiles This parameter is NEVER used
	 * @return true if the model has been built without errors
	 *
	 * @deprecated (since Spoon 7.0.0) The parameter buildOnlyOutdatedFiles is not used anymore.
	 */
	@Deprecated
	protected boolean buildUnitsAndModel(JDTBuilder jdtBuilder, SpoonFolder sourcesFolder, String[] classpath, String debugMessagePrefix, boolean buildOnlyOutdatedFiles) {
		return buildUnitsAndModel(jdtBuilder, sourcesFolder, classpath, debugMessagePrefix);
	}

	/**
	 * Get the units from the given source folder and build the Spoon Model.
	 * @param jdtBuilder The instance of JDTBuilder to prepare the right JDT arguments
	 * @param sourcesFolder The source folder
	 * @param classpath The complete classpath
	 * @param debugMessagePrefix Useful to help debugging
	 * @return true if the model has been built without errors
	 */
	protected boolean buildUnitsAndModel(JDTBuilder jdtBuilder, SpoonFolder sourcesFolder, String[] classpath, String debugMessagePrefix) {
		CompilationUnitDeclaration[] units = buildUnits(jdtBuilder, sourcesFolder, classpath, debugMessagePrefix);

		// here we build the model in the template factory
		buildModel(units);

		return probs.isEmpty();
	}

	private static final CompilationUnitDeclaration[] EMPTY_RESULT = new CompilationUnitDeclaration[0];

	/**
	 * Build the CompilationUnit found in the source folder
	 * @param jdtBuilder The instance of JDTBuilder to prepare the right JDT arguments
	 * @param sourcesFolder The source folder
	 * @param classpath The complete classpath
	 * @param debugMessagePrefix Useful to help debugging
	 * @param buildOnlyOutdatedFiles This parameter is NEVER used
	 * @return All compilationUnitDeclaration from JDT found in source folder
	 * @deprecated (since Spoon 7.0.0) The parameter buildOnlyOutdatedFiles is not used anymore.
	 */
	@Deprecated
	protected CompilationUnitDeclaration[] buildUnits(JDTBuilder jdtBuilder, SpoonFolder sourcesFolder, String[] classpath, String debugMessagePrefix, boolean buildOnlyOutdatedFiles) {
		return this.buildUnits(jdtBuilder, sourcesFolder, classpath, debugMessagePrefix);
	}

	/**
	 * Build the CompilationUnit found in the source folder
	 * @param jdtBuilder The instance of JDTBuilder to prepare the right JDT arguments
	 * @param sourcesFolder The source folder
	 * @param classpath The complete classpath
	 * @param debugMessagePrefix Useful to help debugging
	 * @return All compilationUnitDeclaration from JDT found in source folder
	 */
	protected CompilationUnitDeclaration[] buildUnits(JDTBuilder jdtBuilder, SpoonFolder sourcesFolder, String[] classpath, String debugMessagePrefix) {
		List<SpoonFile> sourceFiles = Collections.unmodifiableList(sourcesFolder.getAllJavaFiles());
		if (sourceFiles.isEmpty()) {
			return EMPTY_RESULT;
		}

		JDTBatchCompiler batchCompiler = createBatchCompiler(new FileCompilerConfig(sourceFiles));

		String[] args;
		if (jdtBuilder == null) {
			args = new JDTBuilderImpl() //
					.classpathOptions(new ClasspathOptions().encoding(this.getEnvironment().getEncoding().displayName()).classpath(classpath)) //
					.complianceOptions(new ComplianceOptions().compliance(javaCompliance)) //
					.advancedOptions(new AdvancedOptions().preserveUnusedVars().continueExecution().enableJavadoc()) //
					.sources(new SourceOptions().sources(sourceFiles)) // no sources, handled by the JDTBatchCompiler
					.build();
		} else {
			args = jdtBuilder.build();
		}

		getFactory().getEnvironment().debugMessage(debugMessagePrefix + "build args: " + Arrays.toString(args));
		batchCompiler.configure(args);

		return batchCompiler.getUnits();
	}

	protected List<CompilationUnitDeclaration> sortCompilationUnits(CompilationUnitDeclaration[] units) {
		List<CompilationUnitDeclaration> unitList = new ArrayList<>(Arrays.asList(units));
		if (this.sortList) {
			unitList.sort(new FixedOrderBasedOnFileNameCompilationUnitComparator());
		} else {
			Collections.shuffle(unitList);
		}

		return unitList;
	}

	protected void buildModel(CompilationUnitDeclaration[] units) {
		if (getEnvironment().getSpoonProgress() != null) {
			getEnvironment().getSpoonProgress().start(SpoonProgress.Process.MODEL);
		}
		JDTTreeBuilder builder = new JDTTreeBuilder(factory);
		List<CompilationUnitDeclaration> unitList = this.sortCompilationUnits(units);

		int i = 0;
		unitLoop:
		for (CompilationUnitDeclaration unit : unitList) {
			if (unit.isModuleInfo() || !unit.isEmpty()) {
				final String unitPath = new String(unit.getFileName());
				for (final CompilationUnitFilter cuf : compilationUnitFilters) {
					if (cuf.exclude(unitPath)) {
						// do not traverse this unit
						continue unitLoop;
					}
				}
				unit.traverse(builder, unit.scope);
				if (getFactory().getEnvironment().isCommentsEnabled()) {
					new JDTCommentBuilder(unit, factory).build();
				}
				if (getEnvironment().getSpoonProgress() != null) {
					getEnvironment().getSpoonProgress().step(SpoonProgress.Process.MODEL, new String(unit.getFileName()), ++i, unitList.size());
				}
			}
		}
		if (getEnvironment().getSpoonProgress() != null) {
			getEnvironment().getSpoonProgress().end(SpoonProgress.Process.MODEL);
		}

		// we need first to go through the whole model before getting the right reference for imports
		if (getFactory().getEnvironment().isAutoImports()) {
			if (getEnvironment().getSpoonProgress() != null) {
				getEnvironment().getSpoonProgress().start(SpoonProgress.Process.IMPORT);
			}

			i = 0;
			for (CompilationUnitDeclaration unit : units) {
				new JDTImportBuilder(unit, factory).build();
				if (getEnvironment().getSpoonProgress() != null) {
					getEnvironment().getSpoonProgress().step(SpoonProgress.Process.IMPORT, new String(unit.getFileName()), ++i, units.length);
				}
			}
			if (getEnvironment().getSpoonProgress() != null) {
				getEnvironment().getSpoonProgress().end(SpoonProgress.Process.IMPORT);
			}
		}
	}

	protected void generateProcessedSourceFilesUsingTypes(Filter<CtType<?>> typeFilter) {
		if (factory.getEnvironment().getDefaultFileGenerator() != null) {
			factory.getEnvironment().debugMessage("Generating source using types...");
			ProcessingManager processing = new QueueProcessingManager(factory);
			processing.addProcessor(factory.getEnvironment().getDefaultFileGenerator());
			if (typeFilter != null) {
				processing.process(Query.getElements(factory.getModel().getUnnamedModule(), typeFilter));
			} else {
				processing.process(factory.getModel().getAllModules());
			}
		}
	}

	protected void generateProcessedSourceFilesUsingCUs() {

		File outputDirectory = getSourceOutputDirectory();

		factory.getEnvironment().debugMessage("Generating source using compilation units...");
		// Check output directory
		if (outputDirectory == null) {
			throw new RuntimeException("You should set output directory before generating source files");
		}
		// Create spooned directory
		if (outputDirectory.isFile()) {
			throw new RuntimeException("Output must be a directory");
		}
		if (!outputDirectory.exists()) {
			if (!outputDirectory.mkdirs()) {
				throw new RuntimeException("Error creating output directory");
			}
		}

		try {
			outputDirectory = outputDirectory.getCanonicalFile();
		} catch (IOException e1) {
			throw new SpoonException(e1);
		}

		factory.getEnvironment().debugMessage("Generating source files to: " + outputDirectory);

		List<File> printedFiles = new ArrayList<>();
		for (spoon.reflect.cu.CompilationUnit cu : factory.CompilationUnit().getMap().values()) {

			if (cu.getDeclaredTypes().isEmpty()) { // case of package-info
				continue;
			}

			CtType<?> element = cu.getMainType();

			CtPackage pack = element.getPackage();

			// create package directory
			File packageDir;
			if (pack.isUnnamedPackage()) {
				packageDir = new File(outputDirectory.getAbsolutePath());
			} else {
				// Create current package directory
				packageDir = new File(outputDirectory.getAbsolutePath() + File.separatorChar + pack.getQualifiedName().replace('.', File.separatorChar));
			}
			if (!packageDir.exists()) {
				if (!packageDir.mkdirs()) {
					throw new RuntimeException("Error creating output directory");
				}
			}

			// print type
			try {
				File file = new File(packageDir.getAbsolutePath() + File.separatorChar + element.getSimpleName() + DefaultJavaPrettyPrinter.JAVA_FILE_EXTENSION);
				file.createNewFile();

				// the path must be given relatively to to the working directory
				try (InputStream is = getCompilationUnitInputStream(cu.getFile().getPath());
					FileOutputStream outFile = new FileOutputStream(file)) {

					IOUtils.copy(is, outFile);
				}

				if (!printedFiles.contains(file)) {
					printedFiles.add(file);
				}

			} catch (Exception e) {
				Launcher.LOGGER.error(e.getMessage(), e);
			}
		}
	}

	public void setEnvironment(INameEnvironment environment) {
		this.environment = environment;
	}

	/**
	 * report a compilation problem (callback for JDT)
	 */
	public void reportProblem(CategorizedProblem pb) {
		if (pb == null) {
			return;
		}

		// we can not accept this problem, even in noclasspath mode
		// otherwise a nasty null pointer exception occurs later
		if (pb.getID() == IProblem.DuplicateTypes) {
			throw new ModelBuildingException(pb.getMessage());
		}

		probs.add(pb);
	}

	public void reportProblems(Environment environment) {
		if (!getProblems().isEmpty()) {
			for (CategorizedProblem problem : getProblems()) {
				if (problem != null) {
					report(environment, problem);
				}
			}
		}
	}

	protected void report(Environment environment, CategorizedProblem problem) {
		if (problem == null) {
			throw new IllegalArgumentException("problem cannot be null");
		}

		File file = new File(new String(problem.getOriginatingFileName()));
		String filename = file.getAbsolutePath();

		String message = problem.getMessage() + " at " + filename + ":" + problem.getSourceLineNumber();

		if (problem.isError()) {
			if (!environment.getNoClasspath()) {
				// by default, compilation errors are notified as exception
				throw new ModelBuildingException(message);
			} else {
				// in noclasspath mode, errors are only reported
				// but undefined import, type, and name errors are irrelevant
				int problemId = problem.getID();
				if (problemId != IProblem.UndefinedType && problemId != IProblem.UndefinedName
						&& problemId != IProblem.ImportNotFound) {
					environment.report(null, Level.WARN, message);
				}
			}
		}

	}

	/**
	 * returns the list of current problems
	 */
	public List<CategorizedProblem> getProblems() {
		return Collections.unmodifiableList(this.probs);
	}

	public SpoonFolder getSource() {
		return sources;
	}

	public SpoonFolder getTemplates() {
		return templates;
	}

	protected InputStream getCompilationUnitInputStream(String path) {
		Environment env = factory.getEnvironment();
		spoon.reflect.cu.CompilationUnit cu = factory.CompilationUnit().getMap().get(path);
		List<CtType<?>> toBePrinted = cu.getDeclaredTypes();

		PrettyPrinter printer = env.createPrettyPrinter();
		printer.calculate(cu, toBePrinted);

		return new ByteArrayInputStream(printer.getResult().getBytes());
	}

	protected Environment getEnvironment() {
		return getFactory().getEnvironment();
	}

	@Override
	public void addCompilationUnitFilter(final CompilationUnitFilter filter) {
		compilationUnitFilters.add(filter);
	}

	@Override
	public void removeCompilationUnitFilter(CompilationUnitFilter filter) {
		compilationUnitFilters.remove(filter);
	}

	@Override
	public List<CompilationUnitFilter> getCompilationUnitFilter() {
		return new ArrayList<>(compilationUnitFilters);
	}
}
