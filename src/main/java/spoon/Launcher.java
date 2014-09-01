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
package spoon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import spoon.compiler.Environment;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.processing.Severity;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.FragmentDrivenJavaPrettyPrinter;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.DefaultCoreFactory;
import spoon.support.JavaOutputProcessor;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.ZipFolder;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;
import spoon.support.gui.SpoonModelTree;
import spoon.support.processing.SpoonletXmlHandler;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

/**
 * This class implements an integrated command-line launcher for processing
 * programs at compile-time using the JDT-based builder (Eclipse). It takes
 * arguments that allow building, processing, printing, and compiling Java
 * programs. Launch with no arguments (see {@link #main(String[])}) for detailed
 * usage.
 * 
 * 
 * @see spoon.compiler.Environment
 * @see spoon.reflect.factory.Factory
 * @see spoon.compiler.SpoonCompiler
 * @see spoon.processing.ProcessingManager
 * @see spoon.processing.Processor
 */
public class Launcher {

	private Factory factory = createFactory();
	
	private String[] args = new String[0];

	private List<SpoonResource> inputResources = new ArrayList<SpoonResource>();

	/**
	 * Contains the arguments accepted by this launcher (available after
	 * construction and accessible by sub-classes).
	 */
	protected JSAP jsapArgs;

	private List<String> processors = new ArrayList<String>();

	private List<SpoonResource> templateResources = new ArrayList<SpoonResource>();

	// private Environment environment;

	// private Factory factory;

	// protected boolean nooutput;

	/**
	 * A default program entry point (instantiates a launcher with the given
	 * arguments and calls {@link #run()}).
	 */
	public static void main(String[] args) throws Exception {
		Launcher launcher = new Launcher();
		launcher.setArgs(args);
		if (args.length != 0) {
			launcher.run();
		} else {
			launcher.printUsage();
		}
	}

	public void setArgs(String[] args2) {
		this.args = args2;
	}

	/**
	 * Print the usage for this command-line launcher.
	 */
	public void printUsage() throws Exception {
		this.args = new String[] { "--help" };
		run();
	}

	/**
	 * Constructor with no arguments.
	 */
	public Launcher() throws JSAPException {
		jsapArgs = defineArgs();
	}

	/**
	 * Adds an input resource to be processed by Spoon.
	 */
	public void addInputResource(SpoonResource resource) {
		inputResources.add(resource);
	}

	/**
	 * Adds a processor.
	 */
	public void addProcessor(String name) {
		processors.add(name);
	}

	/**
	 * Adds a resource that contains a template (usually a source File).
	 */
	public void addTemplateResource(SpoonResource resource) {
		templateResources.add(resource);
	}

	/**
	 * Defines the common arguments for sub-launchers.
	 * 
	 * @return the JSAP arguments
	 * @throws JSAPException
	 *             when the creation fails
	 */
	protected JSAP defineArgs() throws JSAPException {
		// Verbose output
		JSAP jsap = new JSAP();

		// help
		Switch sw1 = new Switch("help");
		sw1.setShortFlag('h');
		sw1.setLongFlag("help");
		sw1.setDefault("false");
		jsap.registerParameter(sw1);

		// Verbose
		sw1 = new Switch("verbose");
		sw1.setShortFlag('v');
		sw1.setLongFlag("verbose");
		sw1.setDefault("false");
		sw1.setHelp("Output messages about what the compiler is doing.");
		jsap.registerParameter(sw1);

		// Tabs
		sw1 = new Switch("tabs");
		sw1.setLongFlag("tabs");
		sw1.setDefault("false");
		sw1.setHelp("Use tabulations instead of spaces in the generated code (use spaces by default).");
		jsap.registerParameter(sw1);

		// fragments
		sw1 = new Switch("fragments");
		sw1.setLongFlag("fragments");
		sw1.setShortFlag('f');
		sw1.setDefault("false");
		sw1.setHelp("Use source code fragments to generate source code (preserve formatting).");
		jsap.registerParameter(sw1);

		// Tab size
		FlaggedOption opt2 = new FlaggedOption("tabsize");
		opt2.setLongFlag("tabsize");
		opt2.setStringParser(JSAP.INTEGER_PARSER);
		opt2.setDefault("4");
		opt2.setHelp("Define tabulation size.");
		jsap.registerParameter(opt2);

		// Super Verbose
		sw1 = new Switch("debug");
		sw1.setLongFlag("vvv");
		sw1.setDefault("false");
		sw1.setHelp("Generate all debugging info.");
		jsap.registerParameter(sw1);

		// Auto-import
		sw1 = new Switch("imports");
		sw1.setLongFlag("with-imports");
		sw1.setDefault("false");
		sw1.setHelp("Enable imports in generated files.");
		jsap.registerParameter(sw1);

		// java compliance
		opt2 = new FlaggedOption("compliance");
		opt2.setLongFlag("compliance");
		opt2.setHelp("Java source code compliance level (1,2,3,4,5, 6 or 7).");
		opt2.setStringParser(JSAP.INTEGER_PARSER);
		opt2.setDefault("7");
		jsap.registerParameter(opt2);

		// compiler's encoding
		opt2 = new FlaggedOption("encoding");
		opt2.setLongFlag("encoding");
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setRequired(false);
		opt2.setHelp("Forces the compiler to use a specific encoding (UTF-8, UTF-16, ...).");
		jsap.registerParameter(opt2);

		// setting a spoonlet (packaged processors)
		opt2 = new FlaggedOption("spoonlet");
		opt2.setShortFlag('s');
		opt2.setLongFlag("spoonlet");
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setRequired(false);
		opt2.setHelp("List of spoonlet files to load.");
		jsap.registerParameter(opt2);

		// setting Input files & Directory
		opt2 = new FlaggedOption("input");
		opt2.setShortFlag('i');
		opt2.setLongFlag("input");
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setRequired(false);
		opt2.setHelp("List of path to sources files.");
		jsap.registerParameter(opt2);

		// Processor qualified name
		opt2 = new FlaggedOption("processors");
		opt2.setShortFlag('p');
		opt2.setLongFlag("processors");
		opt2.setHelp("List of processor's qualified name to be used.");
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setRequired(false);
		jsap.registerParameter(opt2);

		// setting input template
		opt2 = new FlaggedOption("template");
		opt2.setShortFlag('t');
		opt2.setLongFlag("template");
		opt2.setHelp("List of source templates.");
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setRequired(false);
		opt2.setHelp("List of path to templates java files.");
		jsap.registerParameter(opt2);

		// Spooned output directory
		opt2 = new FlaggedOption("output");
		opt2.setShortFlag('o');
		opt2.setLongFlag("output");
		opt2.setDefault("spooned");
		opt2.setHelp("Specify where to place generated java files.");
		opt2.setStringParser(FileStringParser.getParser());
		opt2.setRequired(false);
		jsap.registerParameter(opt2);

		// Location of properties files
		opt2 = new FlaggedOption("properties");
		opt2.setLongFlag("properties");
		opt2.setStringParser(FileStringParser.getParser());
		opt2.setRequired(false);
		opt2.setHelp("Directory to search for spoon properties files.");
		jsap.registerParameter(opt2);

		// Source classpath
		opt2 = new FlaggedOption("source-classpath");
		opt2.setLongFlag("source-classpath");
		opt2.setHelp("An optional classpath to be passed to the internal Java compiler when building or compiling the input sources.");
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setRequired(false);
		jsap.registerParameter(opt2);

		// Template classpath
		opt2 = new FlaggedOption("template-classpath");
		opt2.setLongFlag("template-classpath");
		opt2.setHelp("An optional classpath to be passed to the internal Java compiler when building the template sources.");
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setRequired(false);
		jsap.registerParameter(opt2);

		// Destination
		opt2 = new FlaggedOption("destination");
		opt2.setShortFlag('d');
		opt2.setLongFlag("destination");
		opt2.setDefault("spooned-classes");
		opt2.setHelp("An optional destination directory for the generated class files.");
		opt2.setStringParser(FileStringParser.getParser());
		opt2.setRequired(false);
		jsap.registerParameter(opt2);

		// Sets output type generation
		opt2 = new FlaggedOption("output-type");
		opt2.setLongFlag(opt2.getID());
		String msg = "States how to print the processed source code: ";
		int i = 0;
		for (OutputType v : OutputType.values()) {
			i++;
			msg += v.toString();
			if (i != OutputType.values().length) {
				msg += "|";
			}
		}
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setHelp(msg);
		opt2.setDefault("classes");
		jsap.registerParameter(opt2);

		// Enable compilation
		sw1 = new Switch("compile");
		sw1.setLongFlag(sw1.getUsageName());
		sw1.setHelp("Enable compilation and output class files.");
		sw1.setDefault("false");
		jsap.registerParameter(sw1);

		// Enable pre-compilation
		sw1 = new Switch("precompile");
		sw1.setLongFlag("precompile");
		sw1.setHelp("Enable pre-compilation of input source files before processing. Compiled classes will be added to the classpath so that they are accessible to the processing manager (typically, processors, annotations, and templates should be pre-compiled most of the time).");
		sw1.setDefault("false");
		jsap.registerParameter(sw1);

		// Enable building only outdated files
		sw1 = new Switch("buildOnlyOutdatedFiles");
		sw1.setLongFlag("buildOnlyOutdatedFiles");
		sw1.setHelp("Set Spoon to build only the source files that have been modified since the latest source code generation, for performance purpose. Note that this option requires to have the --ouput-type option not set to none."
				+ "This option is not appropriate to all kinds of processing. In particular processings that implement or rely on a global analysis should avoid this option because the processor will only have access to the outdated source code (the files modified since the latest processing).");
		sw1.setDefault("false");
		jsap.registerParameter(sw1);

		sw1 = new Switch("lines");
		sw1.setLongFlag("lines");
		sw1.setHelp("Set Spoon to try to preserve the original line numbers when generating the source code (may lead to human-unfriendly formatting).");
		sw1.setDefault("false");
		jsap.registerParameter(sw1);
		
		// show GUI
		sw1 = new Switch("gui");
		sw1.setShortFlag('g');
		sw1.setLongFlag("gui");
		sw1.setHelp("Show spoon model after processing");
		jsap.registerParameter(sw1);

		return jsap;
	}

	/**
	 * Returns the command-line given launching arguments in JSAP format.
	 */
	protected final JSAPResult getArguments() throws Exception {
		return parseArgs();
	}

	/**
	 * Processes the arguments.
	 */
	protected void processArguments(Factory factory) throws Exception {

		Environment env = factory.getEnvironment();

		if (getArguments().getString("input") != null) {
			for (String s : getArguments().getString("input").split(
					"[" + File.pathSeparatorChar + "]")) {
				try {
					inputResources.add(SpoonResourceHelper
							.createResource(new File(s)));
				} catch (FileNotFoundException e) {
					env.report(null, Severity.ERROR,
							"Unable to add source file : " + e.getMessage());
					if (env.isDebug()) {
						logger.debug(e.getMessage(), e);
					}
				}
			}
		}

		if (getArguments().getString("spoonlet") != null) {
			for (String s : getArguments().getString("spoonlet").split(
					"[" + File.pathSeparatorChar + "]")) {
				loadSpoonlet(factory, new File(s));
			}
		}

		// Adding template from command-line
		if (getArguments().getString("template") != null) {
			for (String s : getArguments().getString("template").split(
					"[" + File.pathSeparatorChar + "]")) {
				try {
					addTemplateResource(SpoonResourceHelper
							.createResource(new File(s)));
				} catch (FileNotFoundException e) {
					env.report(null, Severity.ERROR,
							"Unable to add template file: " + e.getMessage());
					if (env.isDebug()) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}

		if (getArguments().getString("processors") != null) {
			for (String processorName : getArguments().getString("processors")
					.split(File.pathSeparator)) {
				addProcessor(processorName);
			}
		}

	}

	/**
	 * Gets the list of input sources as files. This method can be overridden to
	 * customize this list.
	 */
	protected java.util.List<SpoonResource> getInputSources() {
		return inputResources;
	}

	/**
	 * Gets the list of processor types to be initially applied during the
	 * processing (-p option).
	 */
	protected java.util.List<String> getProcessorTypes() {
		return processors;
	}

	/**
	 * Gets the list of template sources as files.
	 */
	protected List<SpoonResource> getTemplateSources() {
		return templateResources;
	}

	/**
	 * Load content of spoonlet file (template and processor list).
	 */
	protected void loadSpoonlet(Factory factory, File spoonletFile) {
		Environment env = factory.getEnvironment();
		SpoonFolder folder;
		try {
			folder = new ZipFolder(spoonletFile);
		} catch (IOException e) {
			env.report(null, Severity.ERROR,
					"Unable to load spoonlet: " + e.getMessage());
			if (env.isDebug()) {
				logger.debug(e.getMessage(), e);
			}
			return;
		}
		List<SpoonResource> spoonletIndex = new ArrayList<SpoonResource>();
		SpoonFile configFile = null;
		for (SpoonFile file : folder.getAllFiles()) {
			if (file.isJava()) {
				spoonletIndex.add(file);
			} else if (file.getName().endsWith("spoon.xml")) {
				// Loading spoonlet properties
				configFile = file;
			}
		}

		if (configFile == null) {
			env.report(
					null,
					Severity.ERROR,
					"No configuration file in spoonlet "
							+ spoonletFile.getName());
		} else {
			try {
				XMLReader xr = XMLReaderFactory.createXMLReader();
				SpoonletXmlHandler loader = new SpoonletXmlHandler(factory,
						this, spoonletIndex);
				xr.setContentHandler(loader);
				InputStream stream = configFile.getContent();
				xr.parse(new InputSource(stream));
				stream.close();
			} catch (SAXException e) {
				logger.error(e.getMessage(), e);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Parses the arguments given by the command line.
	 *
	 * @return the JSAP-presented arguments
	 * @throws JSAPException
	 *             when an error occurs in the argument parsing
	 */
	protected JSAPResult parseArgs() throws JSAPException {
		if (args==null) {
			throw new IllegalStateException("no args, please call setArgs before");
		}
		JSAPResult arguments = jsapArgs.parse(args);
		if (!arguments.success()) {
			// print out specific error messages describing the problems
			for (java.util.Iterator<?> errs = arguments
					.getErrorMessageIterator(); errs.hasNext();) {
				System.err.println("Error: " + errs.next());
			}
		}
		if (!arguments.success() || arguments.getBoolean("help")) {
			System.err.println(getVersionMessage());
			System.err.println("Usage: java <launcher name> [option(s)]");
			System.err.println();
			System.err.println("Options : ");
			System.err.println();
			System.err.println(jsapArgs.getHelp());
			System.exit(-1);
		}

		return arguments;
	}

	/**
	 * A default logger to be used by Spoon.
	 */
	public static final Logger logger = Logger.getLogger(Launcher.class);

	/**
	 * Creates a new Spoon Java compiler in order to process and compile Java
	 * source code.
	 * 
	 * @param factory
	 *            the factory this compiler works on
	 */
	public SpoonCompiler createCompiler(Factory factory) {
		return new JDTBasedSpoonCompiler(factory);
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
	public SpoonCompiler createCompiler(Factory factory,
			List<SpoonResource> inputSources) {
		SpoonCompiler c = createCompiler(factory);
		c.addInputSources(inputSources);
		return c;
	}

	/**
	 * Creates a new Spoon Java compiler in order to process and compile Java
	 * source code.
	 */
	public SpoonCompiler createCompiler(Factory factory,
			List<SpoonResource> inputSources,
			List<SpoonResource> templateSources) {
		SpoonCompiler c = createCompiler(factory);
		c.addInputSources(inputSources);
		c.addTemplateSources(templateSources);
		return c;
	}

	/**
	 * Creates a new Spoon Java compiler with a default factory in order to
	 * process and compile Java source code. The compiler's factory can be
	 * accessed with the {@link SpoonCompiler#getFactory()}.
	 */
	public SpoonCompiler createCompiler() {
		return createCompiler(factory);
	}

	/**
	 * Creates a new Spoon Java compiler with a default factory and a list of
	 * input sources.
	 */
	public SpoonCompiler createCompiler(List<SpoonResource> inputSources) {
		SpoonCompiler c = createCompiler(factory);
		c.addInputSources(inputSources);
		return c;
	}

	/**
	 * Creates a new Spoon Java compiler with a default factory and a list of
	 * input and template sources.
	 */
	public SpoonCompiler createCompiler(
			List<SpoonResource> inputSources,
			List<SpoonResource> templateSources) {
		SpoonCompiler c = createCompiler(factory);
		c.addInputSources(inputSources);
		c.addTemplateSources(templateSources);
		return c;
	}

	/**
	 * Creates a default Spoon factory, which holds the Java model (AST)
	 * compiled from the source files and which can be processed by Spoon
	 * processors.
	 */
	public Factory createFactory() {
		return createFactory(new StandardEnvironment());
	}

	/**
	 * Creates a default factory with the given environment.
	 * 
	 * @param environment
	 *            the factory's environment
	 * @return the created factory
	 */
	public Factory createFactory(Environment environment) {
		return new FactoryImpl(new DefaultCoreFactory(), environment);
	}

	/**
	 * Creates a new default environment.
	 */
	public Environment createEnvironment() {
		return new StandardEnvironment();
	}

	/**
	 * Initializes an environment with the given parameters.
	 * 
	 * @param environment
	 *            the environment to be initialized
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
	 */
	public void initEnvironment(Environment environment,
			int complianceLevel, boolean verbose, boolean debug,
			File properties, boolean autoImports, int tabulationSize,
			boolean useTabulations, boolean useSourceCodeFragments,
			boolean preserveLineNumbers, File sourceOutputDir) {

		// environment initialization
		environment.setComplianceLevel(complianceLevel);
		environment.setVerbose(true);
		environment.setXmlRootFolder(properties);

		JavaOutputProcessor printer = createOutputWriter(sourceOutputDir);
		environment.setDefaultFileGenerator(printer);

		environment.setVerbose(verbose || debug);
		environment.setDebug(debug);
		environment.setAutoImports(autoImports);
		environment.setPreserveLineNumbers(preserveLineNumbers);

		environment.setTabulationSize(tabulationSize);
		environment.useTabulations(useTabulations);
		environment.useSourceCodeFragments(useSourceCodeFragments);
	}

	public JavaOutputProcessor createOutputWriter(File sourceOutputDir) {
		return new JavaOutputProcessor(sourceOutputDir, createPrettyPrinter());
	}

	public PrettyPrinter createPrettyPrinter() {
		if (factory.getEnvironment().isUsingSourceCodeFragments()) {
			return new FragmentDrivenJavaPrettyPrinter(factory.getEnvironment());
		} else {
			return new DefaultJavaPrettyPrinter(factory.getEnvironment());
		}
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
	 * </ol>
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
	 *            last build/compilation (requires {@code !nooutput} and
	 *            {@code compile} with a correctly set
	 *            {@code destinationDirectory})
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
	public void run(SpoonCompiler compiler, String encoding,
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
		
		// backward compatibility
		// we don't have to set the source classpath
		if (sourceClasspath != null) {
		  compiler.setSourceClasspath(sourceClasspath);
		}
		
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
				logger.debug(e.getMessage(), e);
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

	/**
	 * Starts the Spoon processing.
	 */
	public void run() throws Exception {

		JSAPResult args = getArguments();
		initEnvironment(factory.getEnvironment(), args.getInt("compliance"),
				args.getBoolean("verbose"), args.getBoolean("debug"),
				args.getFile("properties"), args.getBoolean("imports"),
				args.getInt("tabsize"), args.getBoolean("tabs"),
				args.getBoolean("fragments"), args.getBoolean("lines"),
				args.getFile("output"));

		factory.getEnvironment().reportProgressMessage(getVersionMessage());

		factory.getEnvironment().debugMessage("loading command-line arguments: "
				+ Arrays.asList(this.args));

		processArguments(factory);

		OutputType outputType = OutputType.fromString(args
				.getString("output-type"));
		if (outputType == null) {
			factory.getEnvironment().report(null, Severity.ERROR,
					"unsupported output type: " + args.getString("output-type"));
			printUsage();
			throw new Exception("unsupported output type: "
					+ args.getString("output-type"));
		}

		SpoonCompiler compiler = createCompiler(factory);
		run(compiler, args.getString("encoding"),
				args.getBoolean("precompile"), outputType,
				args.getFile("output"), getProcessorTypes(),
				args.getBoolean("compile"), args.getFile("destination"),
				args.getBoolean("buildOnlyOutdatedFiles"),
				args.getString("source-classpath"),
				args.getString("template-classpath"), getInputSources(),
				getTemplateSources());

		// display GUI
		if (getArguments().getBoolean("gui")) {
			new SpoonModelTree(compiler.getFactory());
		}

	}

	private String getVersionMessage() {
		return "Spoon version "+ResourceBundle.getBundle("spoon").getString("application.version");
	}

}
