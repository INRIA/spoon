/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;
import com.martiansoftware.jsap.stringparsers.FileStringParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spoon.SpoonModelBuilder.InputType;
import spoon.compiler.Environment;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.processing.Processor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.JavaOutputProcessor;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FileSystemFolder;
import spoon.support.compiler.VirtualFile;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;
import spoon.support.gui.SpoonModelTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import static spoon.support.StandardEnvironment.DEFAULT_CODE_COMPLIANCE_LEVEL;

/**
 * This class implements an integrated command-line launcher for processing
 * programs at compile-time using the JDT-based builder (Eclipse). It takes
 * arguments that allow building, processing, printing, and compiling Java
 * programs. Launch with no arguments (see {@link #main(String[])}) for detailed
 * usage.
 */
public class Launcher implements SpoonAPI {

	enum CLASSPATH_MODE {
		NOCLASSPATH, FULLCLASSPATH
	}

	public static final String SPOONED_CLASSES = "spooned-classes";

	public static final String OUTPUTDIR = "spooned";

	protected Factory factory;

	private SpoonModelBuilder modelBuilder;

	private String[] commandLineArgs = new String[0];

	private Filter<CtType<?>> typeFilter;

	/**
	 * Contains the arguments accepted by this launcher (available after
	 * construction and accessible by sub-classes).
	 */
	private static JSAP jsapSpec;
	protected JSAPResult jsapActualArgs;

	private List<String> processorTypes = new ArrayList<>();
	private List<Processor<? extends CtElement>> processors = new ArrayList<>();

	/**
	 * This field is used to ensure that {@link #setArgs(String[])} is only called once.
 	 */
	private boolean processed = false;

	/**
	 * A default program entry point (instantiates a launcher with the given
	 * arguments and calls {@link #run()}).
	 */
	public static void main(String[] args) {
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
		if (processed) {
			throw new SpoonException("You cannot process twice the same launcher instance.");
		}
		processed = true;

		processArguments();
	}

	public void printUsage() {
		this.commandLineArgs = new String[] { "--help" };
		processArguments();
	}

	static {
		// Initialize log output path with the default value
		if (System.getProperty("spoon.log.path") == null) {
			System.setProperty("spoon.log.path", "${java.io.tmpdir}/spoon-log.log");
		}

		jsapSpec = defineArgs();
	}

	/**
	 * Creates a {@link Launcher} using the {@link Factory} returned by {@link #createFactory()}.
	 */
	public Launcher() {
		factory = createFactory();
		processArguments();
	}

	/**
	 * Creates a {@link Launcher} with {@link Factory} {@code pFactory}.
	 *
	 * @param pFactory
	 * 		The {@link Factory} that will be utilized in {@link #buildModel()}.
	 * @throws IllegalArgumentException
	 * 		If {@code pFactory == null}.
	 */
	public Launcher(final Factory pFactory) {
		if (pFactory == null) {
			throw new IllegalArgumentException("unable to create launcher with null factory");
		}
		factory = pFactory;
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

	/** adds a resource to be parsed to build the spoon model */
	public void addInputResource(SpoonResource resource) {
		modelBuilder.addInputSource(resource);
	}

	@Override
	public void addProcessor(String name) {
		processorTypes.add(name);
	}

	@Override
	public <T extends CtElement> void addProcessor(Processor<T> processor) {
		processors.add(processor);
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

			// Level logging.
			opt2 = new FlaggedOption("level");
			opt2.setLongFlag("level");
			opt2.setHelp("Level of the ouput messages about what spoon is doing.");
			opt2.setStringParser(JSAP.STRING_PARSER);
			opt2.setDefault(Level.ERROR.toString());
			jsap.registerParameter(opt2);

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
			opt2.setDefault(DEFAULT_CODE_COMPLIANCE_LEVEL + "");
			jsap.registerParameter(opt2);

			// compiler's encoding
			opt2 = new FlaggedOption("encoding");
			opt2.setLongFlag("encoding");
			opt2.setStringParser(JSAP.STRING_PARSER);
			opt2.setRequired(false);
			opt2.setDefault("UTF-8");
			opt2.setHelp("Forces the compiler to use a specific encoding (UTF-8, UTF-16, ...).");
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

			// Source classpath
			opt2 = new FlaggedOption("source-classpath");
			opt2.setLongFlag("source-classpath");
			opt2.setHelp("An optional classpath to be passed to the internal " + "Java compiler when building or compiling the " + "input sources.");
			opt2.setStringParser(JSAP.STRING_PARSER);
			opt2.setRequired(false);
			jsap.registerParameter(opt2);

			// Template classpath
			opt2 = new FlaggedOption("template-classpath");
			opt2.setLongFlag("template-classpath");
			opt2.setHelp("An optional classpath to be passed to the " + "internal Java compiler when building " + "the template sources.");
			opt2.setStringParser(JSAP.STRING_PARSER);
			opt2.setRequired(false);
			jsap.registerParameter(opt2);

			// Destination
			opt2 = new FlaggedOption("destination");
			opt2.setShortFlag('d');
			opt2.setLongFlag("destination");
			opt2.setDefault(SPOONED_CLASSES);
			opt2.setHelp("An optional destination directory for the generated class files.");
			opt2.setStringParser(FileStringParser.getParser());
			opt2.setRequired(false);
			jsap.registerParameter(opt2);

			// Sets output type generation
			opt2 = new FlaggedOption("output-type");
			opt2.setLongFlag(opt2.getID());
			StringBuilder msg = new StringBuilder("States how to print the processed source code: ");
			int i = 0;
			for (OutputType v : OutputType.values()) {
				i++;
				msg.append(v.toString());
				if (i != OutputType.values().length) {
					msg.append("|");
				}
			}
			opt2.setStringParser(JSAP.STRING_PARSER);
			opt2.setHelp(msg.toString());
			opt2.setDefault("classes");
			jsap.registerParameter(opt2);

			// Enable compilation
			sw1 = new Switch("compile");
			sw1.setLongFlag(sw1.getUsageName());
			sw1.setHelp("Compiles the resulting classes (after transformation) to bytecode.");
			sw1.setDefault("false");
			jsap.registerParameter(sw1);

			// Enable pre-compilation
			sw1 = new Switch("precompile");
			sw1.setLongFlag("precompile");
			sw1.setHelp("[experimental] Enable pre-compilation of input source files " + "before processing. The compiled classes " + "will be added to the classpath.");
			sw1.setDefault("false");
			jsap.registerParameter(sw1);

			sw1 = new Switch("lines");
			sw1.setLongFlag("lines");
			sw1.setHelp("Set Spoon to try to preserve the original line " + "numbers when generating the source " + "code (may lead to human-unfriendly " + "formatting).");
			sw1.setDefault("false");
			jsap.registerParameter(sw1);


			opt2 = new FlaggedOption("cpmode");
			opt2.setLongFlag(opt2.getID());
			String acceptedValues = StringUtils.join(CLASSPATH_MODE.values(), "; ");
			opt2.setStringParser(EnumeratedStringParser.getParser(acceptedValues));
			msg = new StringBuilder("Classpath mode to use in Spoon: " + acceptedValues);
			opt2.setHelp(msg.toString());
			opt2.setRequired(true);
			opt2.setDefault(CLASSPATH_MODE.NOCLASSPATH.name());
			jsap.registerParameter(opt2);

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
			sw1 = new Switch("enable-comments");
			sw1.setShortFlag('c');
			sw1.setLongFlag("enable-comments");
			sw1.setHelp("[DEPRECATED] Adds all code comments in the Spoon AST (Javadoc, line-based comments), rewrites them when pretty-printing. (deprecated: by default, the comments are enabled.)");
			sw1.setDefault("false");
			jsap.registerParameter(sw1);

			// Disable generation of javadoc.
			sw1 = new Switch("disable-comments");
			sw1.setLongFlag("disable-comments");
			sw1.setHelp("Disable the parsing of comments in Spoon.");
			sw1.setDefault("false");
			jsap.registerParameter(sw1);

			// Generate only java files specified.
			opt2 = new FlaggedOption("generate-files");
			opt2.setShortFlag('f');
			opt2.setLongFlag("generate-files");
			opt2.setHelp("Only generate the given fully qualified java classes (separated by ':' if multiple are given).");
			opt2.setStringParser(JSAP.STRING_PARSER);
			opt2.setRequired(false);
			jsap.registerParameter(opt2);

			// Disable checks.
			sw1 = new Switch("disable-model-self-checks");
			sw1.setShortFlag('a');
			sw1.setLongFlag("disable-model-self-checks");
			sw1.setHelp("Disables checks made on the AST (hashcode violation, method's signature violation and parent violation). Default: false.");
			sw1.setDefault("false");
			jsap.registerParameter(sw1);

			return jsap;
		} catch (JSAPException e) {
			throw new SpoonException(e.getMessage(), e);
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
		// environment initialization
		environment.setComplianceLevel(jsapActualArgs.getInt("compliance"));
		environment.setLevel(jsapActualArgs.getString("level"));
		if (jsapActualArgs.getBoolean("imports")) {
			environment.setPrettyPrintingMode(Environment.PRETTY_PRINTING_MODE.AUTOIMPORT);
		} else {
			environment.setPrettyPrintingMode(Environment.PRETTY_PRINTING_MODE.FULLYQUALIFIED);
		}

		String cpmode = jsapActualArgs.getString("cpmode").toUpperCase();
		Launcher.LOGGER.info("Running in " + cpmode + " mode (doc: http://spoon.gforge.inria.fr/launcher.html).");
		CLASSPATH_MODE classpath_mode = CLASSPATH_MODE.valueOf(cpmode);
		switch (classpath_mode) {
			case NOCLASSPATH:
				environment.setNoClasspath(true);
				break;

			case FULLCLASSPATH:
				environment.setNoClasspath(false);
				break;
		}

		environment.setPreserveLineNumbers(jsapActualArgs.getBoolean("lines"));
		environment.setTabulationSize(jsapActualArgs.getInt("tabsize"));
		environment.useTabulations(jsapActualArgs.getBoolean("tabs"));
		environment.setCopyResources(!jsapActualArgs.getBoolean("no-copy-resources"));

		if (jsapActualArgs.getBoolean("disable-comments")) {
			environment.setCommentEnabled(false);
		} else {
			environment.setCommentEnabled(true);
		}

		environment.setShouldCompile(jsapActualArgs.getBoolean("compile"));
		if (jsapActualArgs.getBoolean("disable-model-self-checks")) {
			environment.disableConsistencyChecks();
		}

		String outputString = jsapActualArgs.getString("output-type");
		OutputType outputType = OutputType.fromString(outputString);
		if (outputType == null) {
			throw  new SpoonException("Unknown output type: " + outputString);
		} else {
			environment.setOutputType(outputType);
		}

		try {
			Charset charset = Charset.forName(jsapActualArgs.getString("encoding"));
			environment.setEncoding(charset);
		} catch (Exception e) {
			throw new SpoonException(e);
		}

		if (getArguments().getString("generate-files") != null) {
			setOutputFilter(getArguments().getString("generate-files").split(":"));
		}

		// now we are ready to create a spoon compiler
		modelBuilder = createCompiler();

		if (getArguments().getString("input") != null) {
			for (String s : getArguments().getString("input").split("[" + File.pathSeparatorChar + "]")) {
				try {
					modelBuilder.addInputSource(SpoonResourceHelper.createResource(new File(s)));
				} catch (FileNotFoundException e) {
					throw new SpoonException(e);
				}
			}
		}

		if (jsapActualArgs.getBoolean("precompile")) {
			modelBuilder.compile(InputType.FILES);
			getEnvironment().setSourceClasspath(new String[]{getEnvironment().getBinaryOutputDirectory()});
		}

		if (getArguments().getFile("output") != null) {
			setSourceOutputDirectory(getArguments().getFile("output"));
		}

		// Adding template from command-line
		if (getArguments().getString("template") != null) {
			for (String s : getArguments().getString("template").split("[" + File.pathSeparatorChar + "]")) {
				try {
					modelBuilder.addTemplateSource(SpoonResourceHelper.createResource(new File(s)));
				} catch (FileNotFoundException e) {
					environment.report(null, Level.ERROR, "Unable to add template file: " + e.getMessage());
					LOGGER.error(e.getMessage(), e);
				}
			}
		}

		if (getArguments().getString("processors") != null) {
			for (String processorName : getArguments().getString("processors").split(File.pathSeparator)) {
				addProcessor(processorName);
			}
		}

	}

	/**
	 * Gets the list of processor types to be initially applied during the
	 * processing (-p option).
	 */
	protected java.util.List<String> getProcessorTypes() {
		return processorTypes;
	}

	/**
	 * Gets the list of processors instance to be initially applied during the
	 * processing.
	 */
	protected List<Processor<? extends CtElement>> getProcessors() {
		return processors;
	}

	/**
	 * Parses the arguments given by the command line.
	 *
	 * @return the JSAP-presented arguments
	 */
	protected JSAPResult parseArgs() {
		if (jsapSpec == null) {
			throw new IllegalStateException("no args, please call setArgs before");
		}
		JSAPResult arguments = jsapSpec.parse(commandLineArgs);
		if (!arguments.success()) {
			// print out specific error messages describing the problems
			for (java.util.Iterator<?> errs = arguments.getErrorMessageIterator(); errs.hasNext();) {
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
	public static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Creates a new Spoon Java compiler in order to process and compile Java
	 * source code.
	 *
	 * @param factory
	 * 		the factory this compiler works on
	 */
	public SpoonModelBuilder createCompiler(Factory factory) {
		SpoonModelBuilder comp = new JDTBasedSpoonCompiler(factory);
		Environment env = getEnvironment();
		// building
		comp.setBinaryOutputDirectory(jsapActualArgs.getFile("destination"));

		// backward compatibility
		// we don't have to set the source classpath
		if (jsapActualArgs.contains("source-classpath")) {
			comp.setSourceClasspath(jsapActualArgs.getString("source-classpath").split(System.getProperty("path.separator")));
		}

		env.debugMessage("destination: " + comp.getBinaryOutputDirectory());
		env.debugMessage("source classpath: " + Arrays.toString(comp.getSourceClasspath()));
		env.debugMessage("template classpath: " + Arrays.toString(comp.getTemplateClasspath()));

		return comp;
	}

	public SpoonModelBuilder createCompiler(Factory factory, List<SpoonResource> inputSources) {
		SpoonModelBuilder c = createCompiler(factory);
		c.addInputSources(inputSources);
		return c;
	}

	/**
	 * Creates a new Spoon Java compiler in order to process and compile Java
	 * source code.
	 */
	public SpoonModelBuilder createCompiler(Factory factory, List<SpoonResource> inputSources, List<SpoonResource> templateSources) {
		SpoonModelBuilder c = createCompiler(factory);
		c.addInputSources(inputSources);
		c.addTemplateSources(templateSources);
		return c;
	}

	@Override
	public SpoonModelBuilder createCompiler() {
		return createCompiler(factory);
	}

	/**
	 * Creates a new Spoon Java compiler with a default factory and a list of
	 * input sources.
	 */
	public SpoonModelBuilder createCompiler(List<SpoonResource> inputSources) {
		SpoonModelBuilder c = createCompiler(factory);
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

	public JavaOutputProcessor createOutputWriter() {
		/*
		 * create without printer, because Launcher is not initialized yet
		 * so we cannot setup correct printer validators at this time
		 * The printer is created lazily using Environment
		 */
		JavaOutputProcessor outputProcessor = new JavaOutputProcessor();
		outputProcessor.setFactory(this.getFactory());
		return outputProcessor;
	}

	public PrettyPrinter createPrettyPrinter() {
		return getEnvironment().createPrettyPrinter();
	}

	/**
	 * Runs Spoon using the given compiler, with the given run options. A Spoon
	 * run will perform the following tasks:
	 *
	 * <ol>
	 * <li>Source model building in the given compiler:
	 * {@link SpoonModelBuilder#build()}.</li>
	 * <li>Template model building in the given factory (if any template source
	 * is given): {@link SpoonModelBuilder#build()}.</li>
	 * <li>Model processing with the list of given processors if any:
	 * {@link SpoonModelBuilder#instantiateAndProcess(List)}.</li>
	 * <li>Processed Source code printing and generation (can be disabled with
	 * {@link OutputType#NO_OUTPUT}):
	 * {@link SpoonModelBuilder#generateProcessedSourceFiles(OutputType)}.</li>
	 * <li>Processed source code compilation (optional):
	 * </ol>
	 */
	@Override
	public void run() {
		Environment env = modelBuilder.getFactory().getEnvironment();
		env.reportProgressMessage(getVersionMessage());
		env.reportProgressMessage("running Spoon...");

		env.reportProgressMessage("start processing...");

		long tstart = System.currentTimeMillis();

		buildModel();

		process();

		prettyprint();

		if (env.shouldCompile()) {
			// we compile the types from the factory, they may have been modified by some processors
			modelBuilder.compile(InputType.CTTYPES);
		}

		long t = System.currentTimeMillis();

		env.debugMessage("program spooning done in " + (t - tstart) + " ms");
		env.reportEnd();

	}

	private String getVersionMessage() {
		return "Spoon version " + ResourceBundle.getBundle("spoon").getString("application.version");
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
	public CtModel buildModel() {
		long tstart = System.currentTimeMillis();
		modelBuilder.build();
		getEnvironment().debugMessage("model built in " + (System.currentTimeMillis() - tstart));
		return modelBuilder.getFactory().getModel();
	}

	@Override
	public void process() {
		long tstart = System.currentTimeMillis();
		modelBuilder.instantiateAndProcess(getProcessorTypes());
		modelBuilder.process(getProcessors());
		getEnvironment().debugMessage("model processed in " + (System.currentTimeMillis() - tstart) + " ms");
	}

	@Override
	public void prettyprint() {
		long tstart = System.currentTimeMillis();
		try {
			modelBuilder.generateProcessedSourceFiles(getEnvironment().getOutputType(), typeFilter);
		} catch (Exception e) {
			throw new SpoonException(e);
		}

		if (!getEnvironment().getOutputType().equals(OutputType.NO_OUTPUT) && getEnvironment().isCopyResources()) {
			for (File dirInputSource : modelBuilder.getInputSources()) {
				if (dirInputSource.isDirectory()) {
					final Collection<?> resources = FileUtils.listFiles(dirInputSource, RESOURCES_FILE_FILTER, ALL_DIR_FILTER);
					for (Object resource : resources) {
						final String resourceParentPath = ((File) resource).getParent();
						final String packageDir = resourceParentPath.substring(dirInputSource.getPath().length());
						final String targetDirectory = getEnvironment().getDefaultFileGenerator().getOutputDirectory() + packageDir;
						try {
							FileUtils.copyFileToDirectory((File) resource, new File(targetDirectory));
						} catch (IOException e) {
							throw new SpoonException(e);
						}
					}
				}
			}
		}

		getEnvironment().debugMessage("pretty-printed in " + (System.currentTimeMillis() - tstart) + " ms");
	}

	public SpoonModelBuilder getModelBuilder() {
		return modelBuilder;
	}

	@Override
	public void setSourceOutputDirectory(String path) {
		setSourceOutputDirectory(new File(path));
	}

	@Override
	public void setSourceOutputDirectory(File outputDirectory) {
		getEnvironment().setSourceOutputDirectory(outputDirectory);
		getEnvironment().setDefaultFileGenerator(createOutputWriter());
	}

	@Override
	public void setOutputFilter(Filter<CtType<?>> typeFilter) {
		this.typeFilter = typeFilter;
	}

	@Override
	public void setOutputFilter(final String... qualifedNames) {
		setOutputFilter(new AbstractFilter<CtType<?>>(CtType.class) {
			@Override
			public boolean matches(CtType<?> element) {
				for (String generateFile : qualifedNames) {
					if (generateFile.equals(element.getQualifiedName())) {
						return true;
					}
				}
				return false;
			}
		});
	}

	@Override
	public void setBinaryOutputDirectory(String path) {
		getFactory().getEnvironment().setBinaryOutputDirectory(path);
	}

	@Override
	public void setBinaryOutputDirectory(File outputDirectory) {
		setBinaryOutputDirectory(outputDirectory.getPath());
	}

	@Override
	public CtModel getModel() {
		return factory.getModel();
	}

	/** returns the AST of an inline class */
	public static CtClass<?> parseClass(String code) {
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile(code));
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setAutoImports(true);
		Collection<CtType<?>> allTypes = launcher.buildModel().getAllTypes();
		if (allTypes.size() != 1) {
			throw new SpoonException("parseClass only considers one class. Please consider using a Launcher object for more advanced usage.");
		}
		try {
			return (CtClass<?>) allTypes.stream().findFirst().get();
		} catch (ClassCastException e) {
			throw new SpoonException("parseClass only considers classes (and not interfaces and enums). Please consider using a Launcher object for more advanced usage.");
		}
	}
}
