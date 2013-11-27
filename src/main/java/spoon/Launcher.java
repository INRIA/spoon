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
import spoon.reflect.Factory;
import spoon.support.compiler.ZipFolder;
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
 * @see spoon.reflect.Factory
 * @see spoon.compiler.SpoonCompiler
 * @see spoon.processing.ProcessingManager
 * @see spoon.processing.Processor
 */
public class Launcher {

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

	// private Environment environment;

	// private Factory factory;

	// protected boolean nooutput;

	/**
	 * A default program entry point (instantiates a launcher with the given
	 * arguments and calls {@link #run()}).
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 0) {
			new Launcher(args).run();
		} else {
			printUsage();
		}
	}

	/**
	 * Print the usage for this command-line launcher.
	 */
	public static void printUsage() {
		try {
			new Launcher(new String[] { "--help" }).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor with no arguments.
	 */
	protected Launcher() throws JSAPException {
		jsapArgs = defineArgs();
	}

	/**
	 * Default constructor takes the command-line arguments.
	 */
	protected Launcher(String[] args) throws JSAPException {
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
	protected void processArguments(Factory factory) {

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
						e.printStackTrace();
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
	protected void loadSpoonlet(Factory factory, File spoonletFile) {
		Environment env = factory.getEnvironment();
		SpoonFolder folder;
		try {
			folder = new ZipFolder(spoonletFile);
		} catch (IOException e) {
			env.report(null, Severity.ERROR,
					"Unable to load spoonlet: " + e.getMessage());
			if (env.isDebug()) {
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
	 * Starts the Spoon processing.
	 */
	public void run() throws Exception {

		JSAPResult args = getArguments();
		Environment env = Spoon.createEnvironment();
		Factory factory = Spoon.createFactory(env);
		Spoon.initEnvironment(env, args.getInt("compliance"),
				args.getBoolean("verbose"), args.getBoolean("debug"),
				args.getFile("properties"), args.getBoolean("imports"),
				args.getInt("tabsize"), args.getBoolean("tabs"),
				args.getBoolean("fragments"), args.getFile("output"));

		env.reportProgressMessage("Spoon version 2.0");

		env.debugMessage("loading command-line arguments: "
				+ Arrays.asList(this.args));

		processArguments(factory);

		OutputType outputType = OutputType.fromString(args
				.getString("output-type"));
		if (outputType == null) {
			env.report(null, Severity.ERROR,
					"unsupported output type: " + args.getString("output-type"));
			printUsage();
			throw new Exception("unsupported output type: "
					+ args.getString("output-type"));
		}
		SpoonCompiler compiler = Spoon.run(factory,
				arguments.getBoolean("precompile"), outputType,
				args.getFile("output"), getProcessorTypes(),
				arguments.getBoolean("compile"), args.getFile("destination"),
				args.getBoolean("buildOnlyOutdatedFiles"),
				args.getString("source-classpath"),
				args.getString("template-classpath"), getInputSources(),
				getTemplateSources());

		// display GUI
		if (getArguments().getBoolean("gui")) {
			new SpoonModelTree(compiler.getFactory());
		}

	}

}
