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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import spoon.processing.Builder;
import spoon.processing.FileGenerator;
import spoon.processing.ProcessingManager;
import spoon.processing.Severity;
import spoon.reflect.Factory;
import spoon.support.DefaultCoreFactory;
import spoon.support.JavaOutputProcessor;
import spoon.support.QueueProcessingManager;
import spoon.support.StandardEnvironment;
import spoon.support.builder.CtFile;
import spoon.support.builder.CtFolder;
import spoon.support.builder.CtResource;
import spoon.support.builder.FileFactory;
import spoon.support.builder.support.CtFolderZip;
import spoon.support.processing.SpoonletXmlHandler;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

/**
 * This abstract class defines the common tasks and options for launching a
 * program processing. To be subclassed for concrete launchers.
 */
public abstract class AbstractLauncher {

	private String[] args = new String[0];

	private JSAPResult arguments;

	private Factory factory;

	private List<CtResource> inputResources = new ArrayList<CtResource>();

	/**
	 * Contains the arguments accepted by this launcher (available after
	 * construction and accessible by sub-classes).
	 */
	protected JSAP jsapArgs;

	private List<String> processors = new ArrayList<String>();

	private List<CtResource> templateResources = new ArrayList<CtResource>();

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
	public void addInputResource(CtResource resource) {
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
	public void addTemplateResource(CtResource resource) {
		templateResources.add(resource);
	}

	/**
	 * Do the model building.
	 * 
	 * @return true if the Java sources was succesfully compiled by the core
	 *         Java compiler, false if some errors were encountered
	 */
	protected boolean build() {
		// building
		Builder builder = getFactory().getBuilder();

		try {
			for (CtResource f : getInputSources()) {
				builder.addInputSource(f);
			}
			for (CtResource f : getTemplateSources()) {
				builder.addTemplateSource(f);
			}
		} catch (IOException e) {
			getFactory().getEnvironment().report(null, Severity.ERROR,
					"Error while loading resource : " + e.getMessage());
			if (getFactory().getEnvironment().isDebug())
				e.printStackTrace();
		}
		boolean success = false;
		try {
			success = builder.build();
		} catch (Exception e) {
			getFactory().getEnvironment().report(null, Severity.ERROR,
					"Error while loading resource : " + e.getMessage());
			if (getFactory().getEnvironment().isDebug())
				e.printStackTrace();
		}
		return success;
	}

	/**
	 * Creates the factory and associated environment for constructing the
	 * model, initialized with the launcher's arguments.
	 */
	protected Factory createFactory() {
		StandardEnvironment env = new StandardEnvironment();
		Factory factory = new Factory(new DefaultCoreFactory(), env);

		// environment initialization
		env.setComplianceLevel(getArguments().getInt("compliance"));
		env.setVerbose(true);
		env.setXmlRootFolder(getArguments().getFile("properties"));

		JavaOutputProcessor printer = new JavaOutputProcessor(getArguments()
				.getFile("output"));
		env.setDefaultFileGenerator(printer);

		env.setVerbose(getArguments().getBoolean("verbose")
				|| getArguments().getBoolean("debug"));
		env.setDebug(getArguments().getBoolean("debug"));

		env.setTabulationSize(getArguments().getInt("tabsize"));
		env.useTabulations(getArguments().getBoolean("tabs"));
		env.useSourceCodeFragments(getArguments().getBoolean("fragments"));
		return factory;
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
		sw1.setHelp("Output messages about what the compiler is doing");
		jsap.registerParameter(sw1);

		// Tabs
		sw1 = new Switch("tabs");
		sw1.setLongFlag("tabs");
		sw1.setDefault("false");
		sw1
				.setHelp("Use tabulations instead of spaces in the generated code (use spaces by default)");
		jsap.registerParameter(sw1);

		// Tabs
		sw1 = new Switch("fragments");
		sw1.setLongFlag("fragments");
		sw1.setShortFlag('f');
		sw1.setDefault("false");
		sw1
				.setHelp("Use source code fragments to generate source code (preserve formatting)");
		jsap.registerParameter(sw1);

		// Tab size
		FlaggedOption opt2 = new FlaggedOption("tabsize");
		opt2.setLongFlag("tabsize");
		opt2.setStringParser(JSAP.INTEGER_PARSER);
		opt2.setDefault("4");
		opt2.setHelp("Define tabulation size");
		jsap.registerParameter(opt2);

		// Super Verbose
		sw1 = new Switch("debug");
		sw1.setLongFlag("vvv");
		sw1.setDefault("false");
		sw1.setHelp("Generate all debugging info");
		jsap.registerParameter(sw1);

		// java compliance
		opt2 = new FlaggedOption("compliance");
		opt2.setLongFlag("compliance");
		opt2.setHelp("set java compliance level (1,2,3,4,5 or 6)");
		opt2.setStringParser(JSAP.INTEGER_PARSER);
		opt2.setDefault("5");
		jsap.registerParameter(opt2);

		// setting Input files & Directory
		opt2 = new FlaggedOption("spoonlet");
		opt2.setShortFlag('s');
		opt2.setLongFlag("spoonlet");
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setRequired(false);
		opt2.setHelp("List of spoonlet files to load");
		jsap.registerParameter(opt2);

		// setting Input files & Directory
		opt2 = new FlaggedOption("input");
		opt2.setShortFlag('i');
		opt2.setLongFlag("input");
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setRequired(false);
		opt2.setHelp("List of path to sources files");
		jsap.registerParameter(opt2);

		// Processor qualified name
		opt2 = new FlaggedOption("processors");
		opt2.setShortFlag('p');
		opt2.setLongFlag("processors");
		opt2.setHelp("List of processor's qualified name to be used");
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setRequired(false);
		jsap.registerParameter(opt2);

		// setting input template
		opt2 = new FlaggedOption("template");
		opt2.setShortFlag('t');
		opt2.setLongFlag("template");
		opt2.setHelp("list of source templates");
		opt2.setStringParser(JSAP.STRING_PARSER);
		opt2.setRequired(false);
		opt2.setHelp("list of path to templates java files");
		jsap.registerParameter(opt2);

		// Spooned output directory
		opt2 = new FlaggedOption("output");
		opt2.setShortFlag('o');
		opt2.setLongFlag("output");
		opt2.setDefault("spooned");
		opt2.setHelp("specify where to place generated java files");
		opt2.setStringParser(FileStringParser.getParser());
		opt2.setRequired(false);
		jsap.registerParameter(opt2);

		// Location of properties files
		opt2 = new FlaggedOption("properties");
		opt2.setLongFlag("properties");
		opt2.setStringParser(FileStringParser.getParser());
		opt2.setRequired(false);
		opt2.setHelp("Directory to search for spoon properties files");
		jsap.registerParameter(opt2);

		// class to be run
		UnflaggedOption opt3 = new UnflaggedOption("class");
		opt3.setStringParser(JSAP.STRING_PARSER);
		opt3.setRequired(false);
		opt3.setHelp("class to launch within the Spoon context (Main class)");
		jsap.registerParameter(opt3);

		opt3 = new UnflaggedOption("arguments");
		opt3.setStringParser(JSAP.STRING_PARSER);
		opt3.setRequired(false);
		opt3.setGreedy(true);
		opt3.setHelp("parameters to be passed to the main method");
		jsap.registerParameter(opt3);

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
	 * Gets the factory which contains the built model.
	 */
	public final Factory getFactory() {
		if (factory == null) {
			factory = createFactory();
		}
		return factory;
	}

	/**
	 * Processes the arguments.
	 */
	protected void processArguments() {

		if (getArguments().getString("input") != null) {
			for (String s : getArguments().getString("input").split(
					"[" + File.pathSeparatorChar + "]")) {
				try {
					inputResources.add(FileFactory.createResource(new File(s)));
				} catch (FileNotFoundException e) {
					getFactory().getEnvironment().report(null, Severity.ERROR,
							"Unable to add source file : " + e.getMessage());
					if (getFactory().getEnvironment().isDebug())
						e.printStackTrace();
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
					addTemplateResource(FileFactory.createResource(new File(s)));
				} catch (FileNotFoundException e) {
					getFactory().getEnvironment().report(null, Severity.ERROR,
							"Unable to add template file: " + e.getMessage());
					if (getFactory().getEnvironment().isDebug())
						e.printStackTrace();
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
	protected java.util.List<CtResource> getInputSources() {
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
	protected List<CtResource> getTemplateSources() {
		return templateResources;
	}

	/**
	 * Load content of spoonlet file (template and processor list).
	 */
	protected void loadSpoonlet(File spoonletFile) {
		CtFolder folder;
		try {
			folder = new CtFolderZip(spoonletFile);
		} catch (IOException e) {
			getFactory().getEnvironment().report(null, Severity.ERROR,
					"Unable to load spoonlet: " + e.getMessage());
			if (getFactory().getEnvironment().isDebug())
				e.printStackTrace();
			return;
		}
		List<CtResource> spoonletIndex = new ArrayList<CtResource>();
		CtFile configFile = null;
		for (CtFile file : folder.getAllFiles()) {
			if (file.isJava())
				spoonletIndex.add(file);
			else if (file.getName().endsWith("spoon.xml")) {
				// Loading spoonlet properties
				configFile = file;
			}
		}

		if (configFile == null) {
			getFactory().getEnvironment().report(
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
			for (java.util.Iterator<?> errs = arguments.getErrorMessageIterator(); errs
					.hasNext();) {
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
	protected void print() {
		if (getFactory().getEnvironment().getDefaultFileGenerator() != null) {
			ProcessingManager processing = new QueueProcessingManager(
					getFactory());
			processing.addProcessor(getFactory().getEnvironment()
					.getDefaultFileGenerator());
			processing.process();
		}
	}

	/**
	 * Processes the built model with the processors.
	 */
	protected void process() {
		// processing (consume all the processors)
		ProcessingManager processing = new QueueProcessingManager(getFactory());
		for (String processorName : getProcessorTypes()) {
			processing.addProcessor(processorName);
			getFactory().getEnvironment().debugMessage(
					"Loaded processor " + processorName + ".");
		}

		processing.process();
	}

	/**
	 * Starts the Spoon processing.
	 */
	public void run() throws Exception {

		getFactory().getEnvironment()
				.reportProgressMessage("Spoon version 2.0");

		getFactory().getEnvironment().debugMessage(
				"loading command-line arguments...");
		processArguments();

		if (arguments.getBoolean("fragments")) {
			getFactory().getEnvironment().reportProgressMessage(
					"running in 'fragments' mode: AST changes will be ignored");
		}
		getFactory().getEnvironment().reportProgressMessage(
				"start processing...");

		long t = System.currentTimeMillis();
		long tstart = t;
		build();
		getFactory().getEnvironment().debugMessage(
				"model built in " + (System.currentTimeMillis() - t) + " ms");
		t = System.currentTimeMillis();
		process();
		getFactory().getEnvironment().debugMessage(
				"model processed in " + (System.currentTimeMillis() - t)
						+ " ms");
		t = System.currentTimeMillis();
		print();
		FileGenerator<?> fg = getFactory().getEnvironment()
				.getDefaultFileGenerator();
		if (fg != null) {
			if (arguments.getBoolean("compile")) {
				getFactory().getEnvironment().debugMessage(
						"generated bytecode in "
								+ (System.currentTimeMillis() - t) + " ms");
			} else {
				getFactory().getEnvironment().debugMessage(
						"generated source in "
								+ (System.currentTimeMillis() - t) + " ms");
			}
			getFactory().getEnvironment().debugMessage(
					"output directory: " + fg.getOutputDirectory());
		}
		t = System.currentTimeMillis();

		getFactory().getEnvironment().debugMessage(
				"program spooning done in " + (t - tstart) + " ms");
		getFactory().getEnvironment().reportEnd();

		// Gets main class
		String progClass = getArguments().getString("class");
		String progArgs[] = getArguments().getStringArray("arguments");

		if (progClass != null) {
			// Launch main class using reflection
			getFactory().getEnvironment().debugMessage(
					"running class: '" + progClass + "'...");
			Class<?> clas = getClass().getClassLoader().loadClass(progClass);
			Class<?> mainArgType[] = { (new String[0]).getClass() };
			Method main = clas.getMethod("main", mainArgType);
			Object argsArray[] = { progArgs };
			main.invoke(null, argsArray);
		}

	}
}
