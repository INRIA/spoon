package spoon;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import spoon.compiler.Environment;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResource;
import spoon.processing.Severity;
import spoon.reflect.Factory;
import spoon.support.DefaultCoreFactory;
import spoon.support.JavaOutputProcessor;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.JDTCompiler;

/**
 * This helper class defines the Spoon API.
 * 
 * <p>
 * For example:
 * </p>
 * 
 * <pre>
 * SpoonCompiler compiler = Spoon.createCompiler();
 * Factory factory = Spoon.createFactory();
 * List&lt;SpoonFile&gt; files = SpoonResourceHelper.files(&quot;myFile.java&quot;);
 * compiler.build(factory, files);
 * ... process and compile
 * </pre>
 */
public abstract class Spoon {

	/**
	 * A default logger to be used by Spoon.
	 */
	public static final Logger logger = Logger.getLogger(Spoon.class);

	private Spoon() {
	}

	/**
	 * Creates a new Spoon Java compiler in order to process and compile Java
	 * source code.
	 * 
	 * @param factory
	 *            the factory this compiler works on
	 */
	public static SpoonCompiler createCompiler(Factory factory) {
		return new JDTCompiler(factory);
	}

	/**
	 * Creates a new Spoon Java compiler in order to process and compile Java
	 * source code.
	 * 
	 * @param factory
	 *            the factory this compiler works on
	 * @param inputSources
	 *            the sources to be processed and/or compiled
	 */
	public static SpoonCompiler createCompiler(Factory factory,
			List<SpoonResource> inputSources) {
		SpoonCompiler c = new JDTCompiler(factory);
		c.addInputSources(inputSources);
		return c;
	}

	/**
	 * Creates a new Spoon Java compiler in order to process and compile Java
	 * source code.
	 */
	public static SpoonCompiler createCompiler(Factory factory,
			List<SpoonResource> inputSources,
			List<SpoonResource> templateSources) {
		SpoonCompiler c = new JDTCompiler(factory);
		c.addInputSources(inputSources);
		c.addTemplateSources(templateSources);
		return c;
	}

	/**
	 * Creates a new Spoon Java compiler with a default factory in order to
	 * process and compile Java source code. The compiler's factory can be
	 * accessed with the {@link SpoonCompiler#getFactory()}.
	 */
	public static SpoonCompiler createCompiler() {
		return new JDTCompiler(createFactory());
	}

	/**
	 * Creates a new Spoon Java compiler with a default factory and a list of
	 * input sources.
	 */
	public static SpoonCompiler createCompiler(List<SpoonResource> inputSources) {
		SpoonCompiler c = new JDTCompiler(createFactory());
		c.addInputSources(inputSources);
		return c;
	}

	/**
	 * Creates a new Spoon Java compiler with a default factory and a list of
	 * input and template sources.
	 */
	public static SpoonCompiler createCompiler(
			List<SpoonResource> inputSources,
			List<SpoonResource> templateSources) {
		SpoonCompiler c = new JDTCompiler(createFactory());
		c.addInputSources(inputSources);
		c.addTemplateSources(templateSources);
		return c;
	}

	/**
	 * Creates a default Spoon factory, which holds the Java model (AST)
	 * compiled from the source files and which can be processed by Spoon
	 * processors.
	 */
	public static Factory createFactory() {
		return new Factory(new DefaultCoreFactory(), new StandardEnvironment());
	}

	/**
	 * Creates a default factory with the given environment.
	 * 
	 * @param environment
	 *            the factory's environment
	 * @return the created factory
	 */
	public static Factory createFactory(Environment environment) {
		return new Factory(new DefaultCoreFactory(), environment);
	}

	/**
	 * Creates a new default environment.
	 */
	public static Environment createEnvironment() {
		return new StandardEnvironment();
	}

	/**
	 * Initializes an environment with the given parameters.
	 * 
	 * @param environment
	 *            the environment to be intialized
	 * @param complianceLevel
	 *            the Java source code compliance level (... 4, 5, 6, 7)
	 * @param verbose
	 *            tells Spoon to print out the basic traces
	 * @param debug
	 *            tells Spoon to print out the detailed traces
	 * @param properties
	 * @param autoImports
	 *            tells Spoon to automatically generate the imports when
	 *            printing out the source code
	 * @param tabulationSize
	 *            the size of the tabulations in the printed source code
	 * @param useTabulations
	 *            tells if Spoon uses tabulations (vs spaces)
	 * @param useSourceCodeFragments
	 *            tells if Spoon should be in source code fragments mode
	 * @param preserveLineNumbers
	 *            tells if Spoon should try to preserve the original line
	 *            numbers when generating the source code (may lead to
	 *            human-unfriendly formatting)
	 * @param sourceOutputDir
	 *            sets the Spoon output directory where to generate the printed
	 *            source code
	 * @return a properly initialized environment
	 */
	public static void initEnvironment(Environment environment,
			int complianceLevel, boolean verbose, boolean debug,
			File properties, boolean autoImports, int tabulationSize,
			boolean useTabulations, boolean useSourceCodeFragments,
			boolean preserveLineNumbers, File sourceOutputDir) {

		// environment initialization
		environment.setComplianceLevel(complianceLevel);
		environment.setVerbose(true);
		environment.setXmlRootFolder(properties);

		JavaOutputProcessor printer = new JavaOutputProcessor(sourceOutputDir);
		environment.setDefaultFileGenerator(printer);

		environment.setVerbose(verbose || debug);
		environment.setDebug(debug);
		environment.setAutoImports(autoImports);
		environment.setPreserveLineNumbers(preserveLineNumbers);

		environment.setTabulationSize(tabulationSize);
		environment.useTabulations(useTabulations);
		environment.useSourceCodeFragments(useSourceCodeFragments);
	}

	/**
	 * Runs Spoon using the given compiler, with the given run options. A Spoon
	 * run will perform the following tasks:
	 * 
	 * <ol>
	 * <li>Pre-compilation (optional):
	 * {@link SpoonCompiler#compileInputSources()}.</li>
	 * <li>Source model building in the given compiler:
	 * {@link SpoonCompiler#build()}.</li>
	 * <li>Template model building in the given factory (if any template source
	 * is given): {@link SpoonCompiler#build()}.</li>
	 * <li>Model processing with the list of given processors if any:
	 * {@link SpoonCompiler#process(List)}.</li>
	 * <li>Processed Source code printing and generation (can be disabled with
	 * {@link OutputType#NO_OUTPUT}):
	 * {@link SpoonCompiler#generateProcessedSourceFiles(OutputType)}.</li>
	 * <li>Processed source code compilation (optional):
	 * {@link SpoonCompiler#compile()}.</li>
	 * <ol>
	 * 
	 * @param compiler
	 *            the compiler to be used, with a properly initialized factory
	 *            and environment
	 * @param encoding
	 *            the encoding to be used (null to use the default system
	 *            encoding)
	 * @param precompile
	 *            precompile the source code before processing to make sure that
	 *            the input source classes will be available in the classpath
	 * @param outputType
	 *            sets type of source code output
	 * @param outputDirectory
	 *            the output directory of the generated source files
	 * @param processorTypes
	 *            the list of processors to be applied to the built model
	 * @param compile
	 *            compile the source code to bytecode once generated
	 * @param destinationDirectory
	 *            the destination directory of the compiled bytecode
	 * @param buildOnlyOutdatedFiles
	 *            build and compile the files that has been modified since the
	 *            last build/compilation (requires <code>!nooutput</code> and
	 *            <code>compile</code> with a correctly set
	 *            <code>detinatioDirectory</code>)
	 * @param sourceClasspath
	 *            the classpath to build and compile the input sources, given as
	 *            a string
	 * @param templateClasspath
	 *            the classpath to build the template sources, given as a string
	 * @param inputSources
	 *            a list of resources containing the input sources
	 * @param templateSources
	 *            a list of resources containing the template sources (can
	 *            contain zip or jar files)
	 * @throws Exception
	 *             in case something bad happens
	 */
	public static void run(SpoonCompiler compiler, String encoding,
			boolean precompile, OutputType outputType, File outputDirectory,
			List<String> processorTypes, boolean compile,
			File destinationDirectory, boolean buildOnlyOutdatedFiles,
			String sourceClasspath, String templateClasspath,
			List<SpoonResource> inputSources,
			List<SpoonResource> templateSources) throws Exception {
		Environment env = compiler.getFactory().getEnvironment();
		env.reportProgressMessage("running Spoon...");

		if (env.isUsingSourceCodeFragments()) {
			env.reportProgressMessage("running in 'fragments' mode: AST changes will be ignored");
		}
		env.reportProgressMessage("start processing...");

		long t = System.currentTimeMillis();
		long tstart = t;

		// building
		compiler.setBuildOnlyOutdatedFiles(outputType != OutputType.NO_OUTPUT
				&& buildOnlyOutdatedFiles);
		compiler.setDestinationDirectory(destinationDirectory);
		compiler.setOutputDirectory(outputDirectory);
		compiler.setSourceClasspath(sourceClasspath);
		compiler.setTemplateClasspath(templateClasspath);

		env.debugMessage("output: " + compiler.getOutputDirectory());
		env.debugMessage("destination: " + compiler.getDestinationDirectory());
		env.debugMessage("source classpath: " + compiler.getSourceClasspath());
		env.debugMessage("template classpath: "
				+ compiler.getTemplateClasspath());

		try {
			for (SpoonResource f : inputSources) {
				env.debugMessage("add input source: " + f);
				compiler.addInputSource(f);
			}
			for (SpoonResource f : templateSources) {
				env.debugMessage("add template source: " + f);
				compiler.addTemplateSource(f);
			}
		} catch (Exception e) {
			env.report(null, Severity.ERROR, "Error while loading resource : "
					+ e.getMessage());
			if (env.isDebug()) {
				Spoon.logger.debug(e.getMessage(), e);
			}
		}

		if (precompile) {
			t = System.currentTimeMillis();
			compiler.compileInputSources();
			env.debugMessage("pre-compiled input sources in "
					+ (System.currentTimeMillis() - t) + " ms");
		}

		t = System.currentTimeMillis();
		compiler.build();
		env.debugMessage("model built in " + (System.currentTimeMillis() - t)
				+ " ms");

		// System.out.println("============> " + factory.Type().getAll());
		//
		// System.out.println("============> "
		// + factory.CompilationUnit().getMap());
		//
		// for (CompilationUnit cu :
		// factory.CompilationUnit().getMap().values()) {
		// System.out.println("## " + cu.getFile());
		// for (CtSimpleType<?> type : cu.getDeclaredTypes()) {
		// System.out.println("- " + type.getQualifiedName());
		// }
		// // getEnvironment().getDefaultFileGenerator().
		// }

		t = System.currentTimeMillis();
		compiler.process(processorTypes);
		env.debugMessage("model processed in "
				+ (System.currentTimeMillis() - t) + " ms");

		t = System.currentTimeMillis();
		compiler.generateProcessedSourceFiles(outputType);
		env.debugMessage("source generated in "
				+ (System.currentTimeMillis() - t) + " ms");

		t = System.currentTimeMillis();
		if (compile) {
			compiler.compile();
			env.debugMessage("generated bytecode in "
					+ (System.currentTimeMillis() - t) + " ms");
		}

		// FileGenerator<?> fg = getEnvironment().getDefaultFileGenerator();
		// if (fg != null) {
		// // if (arguments.getBoolean("compile")) {
		// // getFactory().getEnvironment().debugMessage(
		// // "generated bytecode in "
		// // + (System.currentTimeMillis() - t) + " ms");
		// // } else
		// {
		// getEnvironment().debugMessage(
		// "generated source in "
		// + (System.currentTimeMillis() - t) + " ms");
		// }
		// getEnvironment().debugMessage(
		// "output directory: " + fg.getOutputDirectory());
		// }
		t = System.currentTimeMillis();

		env.debugMessage("program spooning done in " + (t - tstart) + " ms");
		env.reportEnd();

	}

}
