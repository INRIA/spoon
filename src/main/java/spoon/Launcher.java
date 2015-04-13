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
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
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
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.DefaultCoreFactory;
import spoon.support.JavaOutputProcessor;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FileSystemFolder;
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
 */
public class Launcher implements SpoonAPI {
	
	public static final String OUTPUTDIR = "spooned";

	private Factory factory = createFactory();

	private SpoonCompiler modelBuilder;
	
	private String[] commandLineArgs = new String[0];

	/**
	 * Contains the arguments accepted by this launcher (available after
	 * construction and accessible by sub-classes).
	 */
	private static JSAP jsapSpec;	
	protected JSAPResult jsapActualArgs;

	private List<String> processors = new ArrayList<String>();

	/**
	 * A default program entry point (instantiates a launcher with the given
	 * arguments and calls {@link #run()}).
	 */
	public static void main(String[] args) throws Exception {
		new Launcher().run(args);
	}
	
	@Override
	public void run(String[] args) {
		this.setArgs(args);
		if (args.length != 0) {
			this.run();
			// display GUI
			if (this.jsapActualArgs.getBoolean("gui")) {
				new SpoonModelTree(getFactory());
			}
		} else {
			this.printUsage();
		}
	}

	public void setArgs(String[] args2) {
		this.commandLineArgs = args2;
		processArguments();
	}

	public void printUsage() {
		this.commandLineArgs = new String[] { "--help" };
		run();
	}

	static {
		jsapSpec = defineArgs();
	}
	
	/**
	 * Constructor with no arguments.
	 */
	public Launcher() {
		processArguments();
	}

	@Override
	public void addInputResource(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			addInputResource(new FileSystemFolder(file));
		} else {			
			addInputResource(new FileSystemFile(file));
		}
	}

	private void addInputResource(SpoonResource resource) {
		modelBuilder.addInputSource(resource);
	}

	@Override
	public void addProcessor(String name) {
		processors.add(name);
	}

	public void addTemplateResource(SpoonResource resource) {
		modelBuilder.addTemplateSource(resource);
	}

	@Override
	public Environment getEnvironment() {
		return factory.getEnvironment();
	}
	

	/**
	 * Defines the common arguments for sub-launchers.
	 *
	 * @return the JSAP arguments
	 */
	protected static JSAP defineArgs() {
		try {
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
			opt2.setHelp("Java source code compliance level (1,2,3,4,5, 6, 7 or 8).");
			opt2.setStringParser(JSAP.INTEGER_PARSER);
			opt2.setDefault("8");
			jsap.registerParameter(opt2);

			// compiler's encoding
			opt2 = new FlaggedOption("encoding");
			opt2.setLongFlag("encoding");
			opt2.setStringParser(JSAP.STRING_PARSER);
			opt2.setRequired(false);
			opt2.setDefault("UTF-8");
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
			opt2.setDefault(OUTPUTDIR);
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

			// nobinding
			sw1 = new Switch("noclasspath");
			sw1.setShortFlag('x');
			sw1.setLongFlag("noclasspath");
			sw1.setHelp("Does not assume a full classpath");
			jsap.registerParameter(sw1);

			// show GUI
			sw1 = new Switch("gui");
			sw1.setShortFlag('g');
			sw1.setLongFlag("gui");
			sw1.setHelp("Show spoon model after processing");
			jsap.registerParameter(sw1);

			// Disable copy of resources.
			sw1 = new Switch("no-copy-resources");
			sw1.setShortFlag('r');
			sw1.setLongFlag("no-copy-resources");
			sw1.setHelp("Disable the copy of resources from source to destination folder.");
			sw1.setDefault("false");
			jsap.registerParameter(sw1);

			// Enable generation of javadoc.
			sw1 = new Switch("generate-javadoc");
			sw1.setShortFlag('j');
			sw1.setLongFlag("generate-javadoc");
			sw1.setHelp("Enable the generation of the javadoc.");
			sw1.setDefault("false");
			jsap.registerParameter(sw1);

			return jsap;
		} catch (JSAPException e) {
			throw new SpoonException(e.getMessage(),e);
		}
	}

	/**
	 * Returns the command-line given launching arguments in JSAP format.
	 */
	protected final JSAPResult getArguments() {
		return parseArgs();
	}

	protected void processArguments() {
		jsapActualArgs = getArguments();


		Environment environment = factory.getEnvironment();
		boolean debug = jsapActualArgs.getBoolean("debug");
		// environment initialization
		environment.setComplianceLevel(jsapActualArgs.getInt("compliance"));
		environment.setVerbose(true);
		environment.setXmlRootFolder(jsapActualArgs.getFile("properties"));

		JavaOutputProcessor printer = createOutputWriter(
				jsapActualArgs.getFile(
						"output"), environment);
		environment.setDefaultFileGenerator(printer);

		environment.setVerbose(jsapActualArgs.getBoolean("verbose")
				|| debug);
		environment.setDebug(debug);
		environment.setAutoImports(jsapActualArgs.getBoolean("imports"));
		environment
				.setNoClasspath(jsapActualArgs.getBoolean("noclasspath"));
		environment.setPreserveLineNumbers(jsapActualArgs
				.getBoolean("lines"));

		environment.setTabulationSize(jsapActualArgs.getInt("tabsize"));
		environment.useTabulations(jsapActualArgs.getBoolean("tabs"));
		environment.setCopyResources(!jsapActualArgs.getBoolean("no-copy-resources"));
    environment.setGenerateJavadoc(jsapActualArgs.getBoolean("generate-javadoc"));
		
		
		// now we are ready to create a spoon compiler
		modelBuilder = createCompiler();
		
		if (getArguments().getString("input") != null) {
			for (String s : getArguments().getString("input").split(
					"[" + File.pathSeparatorChar + "]")) {
				try {
					modelBuilder.addInputSource(SpoonResourceHelper
							.createResource(new File(s)));
				} catch (FileNotFoundException e) {
					throw new SpoonException(e);
				}
			}
		}

		// Adding template from command-line
		if (getArguments().getString("template") != null) {
			for (String s : getArguments().getString("template").split(
					"[" + File.pathSeparatorChar + "]")) {
				try {
					modelBuilder.addTemplateSource(SpoonResourceHelper
							.createResource(new File(s)));
				} catch (FileNotFoundException e) {
					environment.report(
							null,
							Severity.ERROR,
							"Unable to add template file: "
									+ e.getMessage());
					if (environment.isDebug()) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}

		if (getArguments().getString("processors") != null) {
			for (String processorName : getArguments().getString(
					"processors").split(File.pathSeparator)) {
				addProcessor(processorName);
			}
		}

	}

	/**
	 * Gets the list of processor types to be initially applied during the
	 * processing (-p option).
	 */
	protected java.util.List<String> getProcessorTypes() {
		return processors;
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
	 */
	protected JSAPResult parseArgs() {
		if (jsapSpec==null) {
			throw new IllegalStateException("no args, please call setArgs before");
		}
		JSAPResult arguments = jsapSpec.parse(commandLineArgs);
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
			System.err.println(jsapSpec.getHelp());
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
		JDTBasedSpoonCompiler comp = new JDTBasedSpoonCompiler(factory);
		Environment env = getEnvironment();
		// building
		comp.setEncoding(getArguments().getString("encoding"));
		comp.setBuildOnlyOutdatedFiles(jsapActualArgs.getBoolean("buildOnlyOutdatedFiles"));
		comp.setDestinationDirectory(jsapActualArgs.getFile("destination"));
		comp.setOutputDirectory(jsapActualArgs.getFile("output"));
		comp.setEncoding(jsapActualArgs.getString("encoding"));
		
		// backward compatibility
		// we don't have to set the source classpath
		if (jsapActualArgs.contains("source-classpath")) {
			comp.setSourceClasspath(jsapActualArgs.getString("source-classpath")
					.split(System.getProperty("path.separator")));
		}

		env.debugMessage("output: " + comp.getDestinationDirectory());
		env.debugMessage("destination: "
				+ comp.getDestinationDirectory());
		env.debugMessage("source classpath: "
				+ Arrays.toString(comp.getSourceClasspath()));
		env.debugMessage("template classpath: "
				+ Arrays.toString(comp.getTemplateClasspath()));

		if (jsapActualArgs.getBoolean("precompile")) {
			comp.compileInputSources();
		}

		return comp;
	}

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

	@Override
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

	@Override
	public Factory createFactory() {
		return new FactoryImpl(new DefaultCoreFactory(), createEnvironment());
	}

	@Override
	public Factory getFactory() {
		return factory;
	}

	@Override
	public Environment createEnvironment() {
		return new StandardEnvironment();
	}

	public JavaOutputProcessor createOutputWriter(File sourceOutputDir, Environment environment) {
		return new JavaOutputProcessor(sourceOutputDir, createPrettyPrinter());
	}

	public PrettyPrinter createPrettyPrinter() {
		return new DefaultJavaPrettyPrinter(getEnvironment());
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

	 */
	@Override
	public void run() {
		Environment env = modelBuilder.getFactory().getEnvironment();
		env.reportProgressMessage(getVersionMessage());
		env.reportProgressMessage("running Spoon...");

		env.reportProgressMessage("start processing...");

		long t = 0 ;
		long tstart = System.currentTimeMillis();

		buildModel();

		process();

		prettyprint();

		if (jsapActualArgs.getBoolean("compile")) {
			modelBuilder.compile();
		}


		t = System.currentTimeMillis();

		env.debugMessage("program spooning done in " + (t - tstart) + " ms");
		env.reportEnd();

	}

	private String getVersionMessage() {
		return "Spoon version "+ResourceBundle.getBundle("spoon").getString("application.version");
	}

	public static final IOFileFilter RESOURCES_FILE_FILTER = new IOFileFilter() {
		@Override
		public boolean accept(File file) {
			return !file.getName().endsWith(".java");
		}

		@Override
		public boolean accept(File file, String s) {
			return false;
		}
	};

	public static final IOFileFilter ALL_DIR_FILTER = new IOFileFilter() {
		@Override
		public boolean accept(File file) {
			return true;
		}

		@Override
		public boolean accept(File file, String s) {
			return false;
		}
	};

	@Override
	public void buildModel() {
		long tstart = System.currentTimeMillis();
		modelBuilder.build();
		getEnvironment().debugMessage("model built in " + (System.currentTimeMillis() - tstart));
	}

	@Override
	public void process() {
		long tstart = System.currentTimeMillis();
		modelBuilder.process(getProcessorTypes());
		getEnvironment().debugMessage("model processed in "
				+ (System.currentTimeMillis() - tstart) + " ms");
	}

	@Override
	public void prettyprint() {
		long tstart = System.currentTimeMillis();
		try {
			OutputType outputType = OutputType.fromString(jsapActualArgs.getString("output-type"));
			modelBuilder.generateProcessedSourceFiles(outputType);
		} catch (Exception e) {
			throw new SpoonException(e);
		}
		
		if (getEnvironment().isCopyResources()) {
			for (File dirInputSource : modelBuilder.getInputSources()) {
				if (dirInputSource.isDirectory()) {
					final Collection resources = FileUtils.listFiles(dirInputSource, RESOURCES_FILE_FILTER, ALL_DIR_FILTER);
					for (Object resource : resources) {
						final String resourceParentPath = ((File) resource).getParent();
						final String packageDir = resourceParentPath.substring(dirInputSource.getPath().length());
						final String targetDirectory = modelBuilder.getOutputDirectory() + packageDir;
						try {
							FileUtils.copyFileToDirectory((File) resource, new File(targetDirectory));
						} catch (IOException e) {
							throw new SpoonException(e);
						}
					}
				}
			}
		}
		
		getEnvironment().debugMessage("pretty-printed in "
				+ (System.currentTimeMillis() - tstart) + " ms");
	}

	public SpoonModelBuilder getModelBuilder() {
		return modelBuilder;
	}

	@Override
	public void setOutputDirectory(String path) {
		modelBuilder.setOutputDirectory(new File(path));
	}

}
