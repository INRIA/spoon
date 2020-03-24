/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.compiler.InvalidClassPathException;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;
import spoon.compiler.builder.EncodingProvider;
import spoon.processing.FileGenerator;
import spoon.processing.ProblemFixer;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.processing.ProcessorProperties;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.visitor.DefaultImportComparator;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.ForceFullyQualifiedProcessor;
import spoon.reflect.visitor.ForceImportProcessor;
import spoon.reflect.visitor.ImportCleaner;
import spoon.reflect.visitor.ImportConflictDetector;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.compiler.FileSystemFolder;
import spoon.support.compiler.SpoonProgress;
import spoon.support.modelobs.EmptyModelChangeListener;
import spoon.support.modelobs.FineModelChangeListener;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;


/**
 * This class implements a simple Spoon environment that reports messages in the
 * standard output stream (Java-compliant).
 */
public class StandardEnvironment implements Serializable, Environment {

	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_CODE_COMPLIANCE_LEVEL = 8;

	private transient  FileGenerator<? extends CtElement> defaultFileGenerator;

	private int errorCount = 0;

	transient ProcessingManager manager;

	private boolean processingStopped = false;

	@Override
	public PRETTY_PRINTING_MODE getPrettyPrintingMode() {
		return prettyPrintingMode;
	}

	@Override
	public void setPrettyPrintingMode(PRETTY_PRINTING_MODE prettyPrintingMode) {
		this.prettyPrintingMode = prettyPrintingMode;
	}

	// the default value is set to maximize backward compatibility
	private PRETTY_PRINTING_MODE prettyPrintingMode = PRETTY_PRINTING_MODE.FULLYQUALIFIED;

	private int warningCount = 0;

	private String[] sourceClasspath = null;

	private boolean preserveLineNumbers = false;

	private boolean copyResources = true;

	private boolean enableComments = true;

	private transient  Logger logger = Launcher.LOGGER;

	private Level level = Level.OFF;

	private boolean shouldCompile = false;

	private boolean skipSelfChecks = false;

	private transient FineModelChangeListener modelChangeListener = new EmptyModelChangeListener();

	private transient Charset encoding = Charset.defaultCharset();

	private transient EncodingProvider encodingProvider;

	private int complianceLevel = DEFAULT_CODE_COMPLIANCE_LEVEL;

	private boolean previewFeaturesEnabled = false;

	private transient OutputDestinationHandler outputDestinationHandler = new DefaultOutputDestinationHandler(new File(Launcher.OUTPUTDIR), this);

	private OutputType outputType = OutputType.CLASSES;

	private Boolean noclasspath = null;

	private transient SpoonProgress spoonProgress = null;

	private CompressionType compressionType = CompressionType.GZIP;

	private boolean sniperMode = false;

	private boolean ignoreDuplicateDeclarations = false;

	private Supplier<PrettyPrinter> prettyPrinterCreator;

	/**
	 * Creates a new environment with a <code>null</code> default file
	 * generator.
	 */
	public StandardEnvironment() {
	}

	@Override
	public void debugMessage(String message) {
		print(message, Level.DEBUG);
	}

	@Override
	public boolean isAutoImports() {
		return PRETTY_PRINTING_MODE.AUTOIMPORT.equals(prettyPrintingMode);
	}

	@Override
	public void setAutoImports(boolean autoImports) {
		if (autoImports == true) {
			prettyPrintingMode = PRETTY_PRINTING_MODE.AUTOIMPORT;
		} else {
			prettyPrintingMode = PRETTY_PRINTING_MODE.FULLYQUALIFIED;
		}
	}

	@Override
	public FileGenerator<? extends CtElement> getDefaultFileGenerator() {
		return defaultFileGenerator;
	}

	@Override
	public Level getLevel() {
		return this.level;
	}

	@Override
	public void setLevel(String level) {
		this.level = toLevel(level);
	}

	@Override
	public boolean shouldCompile() {
		return shouldCompile;
	}

	@Override
	public void setShouldCompile(boolean shouldCompile) {
		this.shouldCompile = shouldCompile;
	}

	@Override
	public boolean checksAreSkipped() {
		return skipSelfChecks;
	}

	@Override
	public void disableConsistencyChecks() {
		skipSelfChecks = true;
	}

	private Level toLevel(String level) {
		if (level == null || level.isEmpty()) {
			throw new SpoonException("Wrong level given at Spoon.");
		}
		return Level.toLevel(level, Level.ALL);
	}

	@Override
	public ProcessingManager getManager() {
		return manager;
	}

	transient Map<String, ProcessorProperties> processorProperties = new TreeMap<>();

	@Override
	public ProcessorProperties getProcessorProperties(String processorName) {
		if (processorProperties.containsKey(processorName)) {
			return processorProperties.get(processorName);
		}
		return null;
	}

	/**
	 * Tells if the processing is stopped, generally because one of the
	 * processors called {@link #setProcessingStopped(boolean)} after reporting
	 * an error.
	 */
	@Override
	public boolean isProcessingStopped() {
		return processingStopped;
	}

	private void prefix(StringBuilder buffer, Level level) {
		if (level == Level.ERROR) {
			buffer.append("error: ");
			errorCount++;
		} else if (level == Level.WARN) {
			buffer.append("warning: ");
			warningCount++;
		}
	}

	@Override
	public void report(Processor<?> processor, Level level, CtElement element, String message) {
		StringBuilder buffer = new StringBuilder();

		prefix(buffer, level);

		// Adding message
		buffer.append(message);

		// Add sourceposition (javac format)
		try {
			CtType<?> type = (element instanceof CtType) ? (CtType<?>) element : element.getParent(CtType.class);
			SourcePosition sp = element.getPosition();

			if (sp == null) {
				buffer.append(" (Unknown Source)");
			} else {
				buffer.append(" at " + type.getQualifiedName() + ".");
				CtExecutable<?> exe = (element instanceof CtExecutable) ? (CtExecutable<?>) element : element.getParent(CtExecutable.class);
				if (exe != null) {
					buffer.append(exe.getSimpleName());
				}
				buffer.append("(" + sp.getFile().getName() + ":" + sp.getLine() + ")");
			}
		} catch (ParentNotInitializedException e) {
			buffer.append(" (invalid parent)");
		}

		print(buffer.toString(), level);
	}

	@Override
	public void report(Processor<?> processor, Level level, CtElement element, String message, ProblemFixer<?>... fixes) {
		report(processor, level, element, message);
	}

	@Override
	public void report(Processor<?> processor, Level level, String message) {
		StringBuilder buffer = new StringBuilder();

		prefix(buffer, level);
		// Adding message
		buffer.append(message);
		print(buffer.toString(), level);
	}

	private void print(String message, Level messageLevel) {
		if (messageLevel.isMoreSpecificThan(this.level)) {
			logger.log(messageLevel, message);
		}
	}

	/**
	 * This method should be called to report the end of the processing.
	 */
	@Override
	public void reportEnd() {
		print("end of processing: ", Level.INFO);
		if (warningCount > 0) {
			print(warningCount + " warning", Level.INFO);
			if (warningCount > 1) {
				print("s", Level.INFO);
			}
			if (errorCount > 0) {
				print(", ", Level.INFO);
			}
		}
		if (errorCount > 0) {
			print(errorCount + " error", Level.INFO);
			if (errorCount > 1) {
				print("s", Level.INFO);
			}
		}
		if ((errorCount + warningCount) > 0) {
			print("\n", Level.INFO);
		} else {
			print("no errors, no warnings", Level.INFO);
		}
	}

	@Override
	public void reportProgressMessage(String message) {
		print(message, Level.INFO);
	}

	public void setDebug(boolean debug) {
	}

	@Override
	public void setDefaultFileGenerator(FileGenerator<? extends CtElement> defaultFileGenerator) {
		this.defaultFileGenerator = defaultFileGenerator;
	}

	@Override
	public void setManager(ProcessingManager manager) {
		this.manager = manager;
	}

	@Override
	public void setProcessingStopped(boolean processingStopped) {
		this.processingStopped = processingStopped;
	}

	public void setVerbose(boolean verbose) {
	}



	@Override
	public int getComplianceLevel() {
		return complianceLevel;
	}

	@Override
	public void setComplianceLevel(int level) {
		complianceLevel = level;
	}

	@Override
	public boolean isPreviewFeaturesEnabled() {
		return previewFeaturesEnabled;
	}

	@Override
	public void setPreviewFeaturesEnabled(boolean previewFeaturesEnabled) {
		this.previewFeaturesEnabled = previewFeaturesEnabled;
	}

	@Override
	public void setProcessorProperties(String processorName, ProcessorProperties prop) {
		processorProperties.put(processorName, prop);
	}

	boolean useTabulations = false;

	@Override
	public boolean isUsingTabulations() {
		return useTabulations;
	}

	@Override
	public void useTabulations(boolean tabulation) {
		useTabulations = tabulation;
	}

	int tabulationSize = 4;

	@Override
	public int getTabulationSize() {
		return tabulationSize;
	}

	@Override
	public void setTabulationSize(int tabulationSize) {
		this.tabulationSize = tabulationSize;
	}

	private transient  ClassLoader classloader;
	/*
	 * cache class loader which loads classes from source class path
	 * we must cache it to make all the loaded classes compatible
	 * The cache is reset when setSourceClasspath(...) is called
	 */
private transient  ClassLoader inputClassloader;

	@Override
	public void setInputClassLoader(ClassLoader aClassLoader) {
		if (aClassLoader instanceof URLClassLoader) {
			final URL[] urls = ((URLClassLoader) aClassLoader).getURLs();
			if (urls != null && urls.length > 0) {
				// Check that the URLs are only file URLs
				boolean onlyFileURLs = true;
				for (URL url : urls) {
					if (!"file".equals(url.getProtocol())) {
						onlyFileURLs = false;
					}
				}
				if (onlyFileURLs) {
					List<String> classpath = new ArrayList<>();
					for (URL url : urls) {
						classpath.add(url.getPath());
					}
					setSourceClasspath(classpath.toArray(new String[0]));
				} else {
					throw new SpoonException("Spoon does not support a URLClassLoader containing other resources than local file.");
				}
			}
			return;
		}
		this.classloader = aClassLoader;
	}

	@Override
	public ClassLoader getInputClassLoader() {
		if (classloader != null) {
			return classloader;
		}
		if (inputClassloader == null) {
			inputClassloader = new URLClassLoader(urlClasspath(), Thread.currentThread().getContextClassLoader());
		}
		return inputClassloader;
	}

	/**
	 * Creates a URL class path from {@link Environment#getSourceClasspath()}
	 */
	public URL[] urlClasspath() {
		String[] classpath = getSourceClasspath();
		int length = (classpath == null) ? 0 : classpath.length;
		URL[] urls = new URL[length];
		for (int i = 0; i < length; i += 1) {
			try {
				urls[i] = new File(classpath[i]).toURI().toURL();
			} catch (MalformedURLException e) {
				throw new IllegalStateException("Invalid classpath: " + Arrays.toString(classpath), e);
			}
		}
		return urls;
	}

	@Override
	public String[] getSourceClasspath() {
		return sourceClasspath;
	}

	@Override
	public void setSourceClasspath(String[] sourceClasspath) {
		verifySourceClasspath(sourceClasspath);
		this.sourceClasspath = sourceClasspath;
		this.inputClassloader = null;
	}

	private void verifySourceClasspath(String[] sourceClasspath) throws InvalidClassPathException {
		for (String classPathElem : sourceClasspath) {
			// preconditions
			File classOrJarFolder = new File(classPathElem);
			if (!classOrJarFolder.exists()) {
				throw new InvalidClassPathException(classPathElem + " does not exist, it is not a valid folder");
			}

			if (classOrJarFolder.isDirectory()) {
				// it should not contain a java file
				SpoonFolder tmp = new FileSystemFolder(classOrJarFolder);
				List<SpoonFile> javaFiles = tmp.getAllJavaFiles();
				if (!javaFiles.isEmpty()) {
					print("You're trying to give source code in the classpath, this should be given to " + "addInputSource " + javaFiles, Level.WARN);
				}
				print("You specified the directory " + classOrJarFolder.getPath() + " in source classpath, please note that only class files will be considered. Jars and subdirectories will be ignored.", Level.WARN);
			} else if (classOrJarFolder.getName().endsWith(".class")) {
				throw new InvalidClassPathException(".class files are not accepted in source classpath.");
			}
		}
	}

	@Override
	public int getErrorCount() {
		return errorCount;
	}

	@Override
	public int getWarningCount() {
		return warningCount;
	}

	@Override
	public boolean isPreserveLineNumbers() {
		return preserveLineNumbers;
	}

	@Override
	public void setPreserveLineNumbers(boolean preserveLineNumbers) {
		this.preserveLineNumbers = preserveLineNumbers;
	}

	@Override
	public void setNoClasspath(boolean option) {
		noclasspath = option;
	}

	@Override
	public boolean getNoClasspath() {
		if (this.noclasspath == null) {
			print("Spoon is used with the default noClasspath option set as true. See: http://spoon.gforge.inria.fr/launcher.html#about-the-classpath", level.INFO);
			this.noclasspath = true;
		}
		return noclasspath;
	}

	@Override
	public boolean isCopyResources() {
		return copyResources;
	}

	@Override
	public void setCopyResources(boolean copyResources) {
		this.copyResources = copyResources;
	}

	@Override
	public boolean isCommentsEnabled() {
		return enableComments;
	}

	@Override
	public void setCommentEnabled(boolean commentEnabled) {
		this.enableComments = commentEnabled;
	}

	private String binaryOutputDirectory = Launcher.SPOONED_CLASSES;

	@Override
	public void setBinaryOutputDirectory(String s) {
		this.binaryOutputDirectory = s;

	}

	@Override
	public String getBinaryOutputDirectory() {
		return binaryOutputDirectory;
	}

	@Override
	public void setSourceOutputDirectory(File directory) {
		if (directory == null) {
			throw new SpoonException("You must specify a directory.");
		}
		if (directory.isFile()) {
			throw new SpoonException("Output must be a directory");
		}

		try {
			this.outputDestinationHandler = new DefaultOutputDestinationHandler(directory.getCanonicalFile(),
					this);
		} catch (IOException e) {
			print(e.getMessage(), Level.WARN);
			throw new SpoonException(e);
		}
	}

	@Override
	public File getSourceOutputDirectory() {
		return this.outputDestinationHandler.getDefaultOutputDirectory();
	}

	@Override
	public void setOutputDestinationHandler(OutputDestinationHandler outputDestinationHandler) {
		this.outputDestinationHandler = outputDestinationHandler;
	}

	@Override
	public OutputDestinationHandler getOutputDestinationHandler() {
		return outputDestinationHandler;
	}

	@Override
	public FineModelChangeListener getModelChangeListener() {
		return modelChangeListener;
	}

	@Override
	public void setModelChangeListener(FineModelChangeListener modelChangeListener) {
		this.modelChangeListener = modelChangeListener;
	}

	@Override
	public Charset getEncoding() {
		return this.encoding;
	}

	@Override
	public EncodingProvider getEncodingProvider() {
		return encodingProvider;
	}

	@Override
	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
	}

	@Override
	public void setEncodingProvider(EncodingProvider encodingProvider) {
		this.encodingProvider = encodingProvider;
	}

	@Override
	public void setOutputType(OutputType outputType) {
		this.outputType = outputType;
	}

	@Override
	public OutputType getOutputType() {
		return this.outputType;
	}

	@Override
	public SpoonProgress getSpoonProgress() {
		return this.spoonProgress;
	}

	@Override
	public void setSpoonProgress(SpoonProgress spoonProgress) {
		this.spoonProgress = spoonProgress;
	}

	@Override
	public CompressionType getCompressionType() {
		return compressionType;
	}

	@Override
	public void setCompressionType(CompressionType serializationType) {
		this.compressionType = serializationType;
	}

	@Override
	public PrettyPrinter createPrettyPrinterAutoImport() {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(this);
		List<Processor<CtElement>> preprocessors = Collections.unmodifiableList(Arrays.<Processor<CtElement>>asList(
				//try to import as much types as possible
				new ForceImportProcessor(),
				//remove unused imports first. Do not add new imports at time when conflicts are not resolved
				new ImportCleaner().setCanAddImports(false),
				//solve conflicts, the current imports are relevant too
				new ImportConflictDetector(),
				//compute final imports
				new ImportCleaner().setImportComparator(new DefaultImportComparator())
		));
		printer.setIgnoreImplicit(false);
		printer.setPreprocessors(preprocessors);
		return printer;
	}

	@Override
	public PrettyPrinter createPrettyPrinter() {
		if (prettyPrinterCreator == null) {
			if (PRETTY_PRINTING_MODE.AUTOIMPORT.equals(prettyPrintingMode)) {
				return createPrettyPrinterAutoImport();
			}


			if (PRETTY_PRINTING_MODE.DEBUG.equals(prettyPrintingMode)) {
				DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(this);
				return printer;
			}

			if (PRETTY_PRINTING_MODE.FULLYQUALIFIED.equals(prettyPrintingMode)) {
				DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(this);
				List<Processor<CtElement>> preprocessors = Collections.unmodifiableList(Arrays.<Processor<CtElement>>asList(
						//force fully qualified
						new ForceFullyQualifiedProcessor(),
						//solve conflicts, the current imports are relevant too
						new ImportConflictDetector(),
						//compute final imports
						new ImportCleaner().setImportComparator(new DefaultImportComparator())
				));
				printer.setIgnoreImplicit(false);
				printer.setPreprocessors(preprocessors);
				return printer;
			}

			throw new UnsupportedOperationException();

		}
		return prettyPrinterCreator.get();
	}

	@Override
	public void setPrettyPrinterCreator(Supplier<PrettyPrinter> creator) {
		this.prettyPrinterCreator = creator;
	}

	@Override
	public boolean isIgnoreDuplicateDeclarations() {
		return ignoreDuplicateDeclarations;
	}

	@Override
	public void setIgnoreDuplicateDeclarations(boolean ignoreDuplicateDeclarations) {
		this.ignoreDuplicateDeclarations = ignoreDuplicateDeclarations;
	}
}
