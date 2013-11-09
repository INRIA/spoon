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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

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
import spoon.processing.ProcessingManager;
import spoon.processing.Severity;
import spoon.reflect.Factory;
import spoon.support.DefaultCoreFactory;
import spoon.support.JavaOutputProcessor;
import spoon.support.QueueProcessingManager;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.JDTCompiler;
import spoon.support.compiler.ZipFolder;
import spoon.support.processing.SpoonletXmlHandler;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

/**
 * This abstract class defines the common tasks and options for launching a
 * program processing. To be subclassed for concrete launchers.
 */
public abstract class AbstractLauncher {

	private String[] args = new String[0];

	private JSAPResult arguments;

	private List<SpoonResource> inputResources = new ArrayList<SpoonResource>();

	/**
	 * Contains the arguments accepted by this launcher (available after
	 * construction and accessible by sub-classes).
	 */
	protected JSAP jsapArgs;

	private List<String> processors = new ArrayList<String>();

	private List<SpoonResource> templateResources = new ArrayList<SpoonResource>();

	private Environment environment;

	private Factory factory;

	protected boolean nooutput;

	/**
	 * Constructor with no arguments.
	 */
	protected AbstractLauncher() throws JSAPException {
		jsapArgs = defineArgs();
	}

	/**
	 * Default constructor takes the command-line arguments.
	 */
	protected AbstractLauncher(String[] args) throws JSAPException {
		this();
		this.args = args;
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
	 * Creates the environment initialized with the launcher's arguments. This
	 * method can be overridden to tune the environment initialization.
	 */
	protected Environment createEnvironment() {
		Environment environment = new StandardEnvironment();

		// environment initialization
		environment.setComplianceLevel(getArguments().getInt("compliance"));
		environment.setVerbose(true);
		environment.setXmlRootFolder(getArguments().getFile("properties"));

		JavaOutputProcessor printer = new JavaOutputProcessor(getArguments()
				.getFile("output"));
		environment.setDefaultFileGenerator(printer);

		nooutput = getArguments().getBoolean("nooutput");
		environment.setVerbose(getArguments().getBoolean("verbose")
				|| getArguments().getBoolean("debug"));
		environment.setDebug(getArguments().getBoolean("debug"));
		environment.setAutoImports(getArguments().getBoolean("imports"));

		environment.setTabulationSize(getArguments().getInt("tabsize"));
		environment.useTabulations(getArguments().getBoolean("tabs"));
		environment.useSourceCodeFragments(getArguments().getBoolean(
				"fragments"));
		return environment;
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

		// setting Input files & Directory
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

		// classpath
		opt2 = new FlaggedOption("classpath");
		opt2.setShortFlag('c');
		opt2.setLongFlag("classpath");
		opt2.setHelp("An optional classpath to be passed to the internal Java compiler.");
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setRequired(false);
		jsap.registerParameter(opt2);

		// classpath
		opt2 = new FlaggedOption("destination");
		opt2.setShortFlag('d');
		opt2.setLongFlag("destination");
		opt2.setDefault("spooned-classes");
		opt2.setHelp("An optional destination directory for the generated class files.");
		opt2.setStringParser(FileStringParser.getParser());
		opt2.setRequired(false);
		jsap.registerParameter(opt2);

		// Disable output generation
		sw1 = new Switch("nooutput");
		sw1.setLongFlag("no");
		sw1.setHelp("Disable output printing of processed source code.");
		sw1.setDefault("false");
		jsap.registerParameter(sw1);

		// Enables compilation
		sw1 = new Switch("compile");
		sw1.setLongFlag("compile");
		sw1.setHelp("Enable compilation and output class files.");
		sw1.setDefault("false");
		jsap.registerParameter(sw1);

		// Enable pre-compilation
		sw1 = new Switch("precompile");
		sw1.setLongFlag("precompile");
		sw1.setHelp("Enable pre-compilation of input source files before processing. Compiled classes will be added to the classpath so that they are accessible to the processing manager (typically, processors, annotations, and templates should be pre-compiled most of the time).");
		sw1.setDefault("false");
		jsap.registerParameter(sw1);

		return jsap;
	}

	/**
	 * Returns the command-line given launching arguments in JSAP format.
	 */
	protected final JSAPResult getArguments() {
		if (arguments == null) {
			try {
				arguments = parseArgs(args);
			} catch (JSAPException e) {
				throw new RuntimeException(e);
			}
		}
		return arguments;
	}

	/**
	 * Processes the arguments.
	 */
	protected void processArguments() {

		if (getArguments().getString("input") != null) {
			for (String s : getArguments().getString("input").split(
					"[" + File.pathSeparatorChar + "]")) {
				try {
					inputResources.add(SpoonResourceHelper
							.createResource(new File(s)));
				} catch (FileNotFoundException e) {
					getEnvironment().report(null, Severity.ERROR,
							"Unable to add source file : " + e.getMessage());
					if (getEnvironment().isDebug()) {
						e.printStackTrace();
					}
				}
			}
		}

		if (getArguments().getString("spoonlet") != null) {
			for (String s : getArguments().getString("spoonlet").split(
					"[" + File.pathSeparatorChar + "]")) {
				loadSpoonlet(new File(s));
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
					getEnvironment().report(null, Severity.ERROR,
							"Unable to add template file: " + e.getMessage());
					if (getEnvironment().isDebug()) {
						e.printStackTrace();
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

		if (getArguments().getString("classpath") != null) {
			getEnvironment()
					.setClasspath(getArguments().getString("classpath"));
		}

	}

	/**
	 * Gets the list of input sources as files. This method can be overriden to
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
	protected void loadSpoonlet(File spoonletFile) {
		SpoonFolder folder;
		try {
			folder = new ZipFolder(spoonletFile);
		} catch (IOException e) {
			getEnvironment().report(null, Severity.ERROR,
					"Unable to load spoonlet: " + e.getMessage());
			if (getEnvironment().isDebug()) {
				e.printStackTrace();
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
			getEnvironment().report(
					null,
					Severity.ERROR,
					"No configuration file in spoonlet "
							+ spoonletFile.getName());
		} else {
			try {
				XMLReader xr = XMLReaderFactory.createXMLReader();
				SpoonletXmlHandler loader = new SpoonletXmlHandler(this,
						spoonletIndex);
				xr.setContentHandler(loader);
				InputStream stream = configFile.getContent();
				xr.parse(new InputSource(stream));
				stream.close();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Parses the arguments given by the command line.
	 * 
	 * @param args
	 *            the command-line arguments as a string array
	 * @return the JSAP-presented arguments
	 * @throws JSAPException
	 *             when an error occurs in the argument parsing
	 */
	protected JSAPResult parseArgs(String[] args) throws JSAPException {
		JSAPResult arguments = jsapArgs.parse(args);
		if (!arguments.success()) {
			// print out specific error messages describing the problems
			for (java.util.Iterator<?> errs = arguments
					.getErrorMessageIterator(); errs.hasNext();) {
				System.err.println("Error: " + errs.next());
			}
		}
		if (!arguments.success() || arguments.getBoolean("help")) {
			System.err.println();
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
	 * Prints out the built model into files.
	 */
	protected void print(Factory factory) {
		if (getEnvironment().getDefaultFileGenerator() != null) {
			ProcessingManager processing = new QueueProcessingManager(factory);
			processing.addProcessor(getEnvironment().getDefaultFileGenerator());
			processing.process();
		}
	}

	/**
	 * Processes the built model with the processors.
	 */
	protected void process(Factory factory) {
		// processing (consume all the processors)
		ProcessingManager processing = new QueueProcessingManager(factory);
		for (String processorName : getProcessorTypes()) {
			processing.addProcessor(processorName);
			getEnvironment().debugMessage(
					"Loaded processor " + processorName + ".");
		}

		processing.process();
	}

	/**
	 * Starts the Spoon processing.
	 */
	public void run() throws Exception {

		getEnvironment().reportProgressMessage("Spoon version 2.0");

		getEnvironment().debugMessage("loading command-line arguments...");
		processArguments();

		if (arguments.getBoolean("precompile")) {
			ClassLoader currentThreadClassLoader = Thread.currentThread()
					.getContextClassLoader();

			URLClassLoader urlClassLoader = new URLClassLoader(
					new URL[] { arguments.getFile("destination").toURI()
							.toURL() }, currentThreadClassLoader);

			Thread.currentThread().setContextClassLoader(urlClassLoader);
		}

		if (arguments.getBoolean("fragments")) {
			getEnvironment().reportProgressMessage(
					"running in 'fragments' mode: AST changes will be ignored");
		}
		getEnvironment().reportProgressMessage("start processing...");

		long t = System.currentTimeMillis();
		long tstart = t;

		// building
		SpoonCompiler compiler = new JDTCompiler(factory = createFactory());
		compiler.setDestinationDirectory(arguments.getFile("destination"));
		compiler.setOutputDirectory(arguments.getFile("output"));

		getEnvironment().debugMessage(
				"output: " + compiler.getOutputDirectory());
		getEnvironment().debugMessage(
				"destination: " + compiler.getDestinationDirectory());
		getEnvironment().debugMessage(
				"classpath: " + environment.getClasspath());

		try {
			for (SpoonResource f : getInputSources()) {
				getEnvironment().debugMessage("add input source: " + f);
				compiler.addInputSource(f);
			}
			for (SpoonResource f : getTemplateSources()) {
				getEnvironment().debugMessage("add template source: " + f);
				compiler.addTemplateSource(f);
			}
		} catch (IOException e) {
			getEnvironment().report(null, Severity.ERROR,
					"Error while loading resource : " + e.getMessage());
			if (getEnvironment().isDebug()) {
				e.printStackTrace();
			}
		}

		if (arguments.getBoolean("precompile")) {
			t = System.currentTimeMillis();
			compiler.compileInputSources();
			getEnvironment().debugMessage(
					"pre-compiled input sources in "
							+ (System.currentTimeMillis() - t) + " ms");
		}
		compiler.build();
		getEnvironment().debugMessage(
				"model built in " + (System.currentTimeMillis() - t) + " ms");

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
		process(factory);
		getEnvironment().debugMessage(
				"model processed in " + (System.currentTimeMillis() - t)
						+ " ms");

		t = System.currentTimeMillis();
		if (!nooutput) {
			compiler.generateProcessedSourceFiles();
			// print(factory);
			getEnvironment().debugMessage(
					"generated source in " + (System.currentTimeMillis() - t)
							+ " ms");
		}

		t = System.currentTimeMillis();
		if (arguments.getBoolean("compile")) {
			compiler.compile();
			getEnvironment().debugMessage(
					"generated bytecode in " + (System.currentTimeMillis() - t)
							+ " ms");
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

		getEnvironment().debugMessage(
				"program spooning done in " + (t - tstart) + " ms");
		getEnvironment().reportEnd();

	}

	/**
	 * Gets the launcher's environment (to be used by default).
	 */
	public final Environment getEnvironment() {
		if (environment == null) {
			environment = createEnvironment();
		}
		return environment;
	}

	/**
	 * This method can be overridden to tune the factory's initialization.
	 */
	protected Factory createFactory() {
		return new Factory(new DefaultCoreFactory(), getEnvironment());
	}

	/**
	 * Gets the launcher's factory (to be used by default).
	 */
	public final Factory getFactory() {
		if (factory == null) {
			factory = createFactory();
		}
		return factory;
	}

}
