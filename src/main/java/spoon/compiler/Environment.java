/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.compiler;

import spoon.OutputType;
import spoon.compiler.builder.EncodingProvider;
import spoon.processing.FileGenerator;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.processing.ProcessorProperties;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.CompressionType;
import spoon.support.Level;
import spoon.support.OutputDestinationHandler;
import spoon.support.compiler.SpoonProgress;
import spoon.support.modelobs.FineModelChangeListener;
import spoon.support.sniper.SniperJavaPrettyPrinter;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

/**
 * This interface represents the environment in which Spoon is launched -
 * accessible through {@link spoon.reflect.factory.Factory#getEnvironment()}. Its
 * primary use is to report messages, warnings, and errors.
 */
public interface Environment {

	/**
	 * Gets the Java version compliance level.
	 *
	 * @return the compliance level
	 */
	int getComplianceLevel();

	/**
	 * Sets the Java version compliance level.
	 *
	 * @param level the compliance level
	 */
	void setComplianceLevel(int level);

	/**
	 * Returns true if preview language features are enabled.
	 *
	 * @return true iff preview features are enabled
	 */
	boolean isPreviewFeaturesEnabled();

	/**
	 * Set to true to enable latest preview language features.
	 * Note: compliance level should be set to the latest.
	 *
	 * @param enabled whether to enable preview features
	 */
	void setPreviewFeaturesEnabled(boolean enabled);

	/**
	 * Get the current pretty-printing mode.
	 *
	 * most robust: {@link PRETTY_PRINTING_MODE#DEBUG}
	 * most sophisticated: {@link PRETTY_PRINTING_MODE#AUTOIMPORT}
	 *
	 * @return the kind of pretty-printing expected.
	 */
	PRETTY_PRINTING_MODE getPrettyPrintingMode();

	void setPrettyPrintingMode(PRETTY_PRINTING_MODE prettyPrintingMode);

	/**
	 * Logs a debug message
	 *
	 * @param message a message to write to the debug log
	 */
	void debugMessage(String message);

	/**
	 * Returns the default file generator for this environment (gives the
	 * default output directory for the created files).
	 *
	 * @return the default file generator
	 */
	FileGenerator<? extends CtElement> getDefaultFileGenerator();

	/**
	 * Gets the processing manager.
	 *
	 * @return the current processing manager
	 */
	ProcessingManager getManager();

	/**
	 * Returns the properties for a given processor.
	 *
	 * @param processorName fully qualified name of a processor
	 * @return properties for the processor, or {@code null} if there is no processor by that name
	 */
	@Nullable ProcessorProperties getProcessorProperties(String processorName);

	/**
	 * Sets the properties for a given processor.
	 *
	 * @param processorName fully qualified name of the processor
	 * @param prop properties to set
	 */
	void setProcessorProperties(String processorName, ProcessorProperties prop);

	/**
	 * Returns true if we let Spoon handle imports and turn fully qualified type names int
	 * simply qualified names.
	 *
	 * @return true iff Spoon is set to automatically import types and simplify type names
	 */
	boolean isAutoImports();

	/**
	 * Tells if the processing is stopped, generally because one of the
	 * processors called {@link #setProcessingStopped(boolean)} after reporting
	 * an error.
	 *
	 * @return true iff processing has been forcibly aborted
	 */
	boolean isProcessingStopped();

	/**
	 * Helper method called by a processor to report an error, warning or
	 * message as dictated by the severity parameter. Note that this does not
	 * stop the processing or any remaining task. To do so, use
	 * {@link #setProcessingStopped(boolean)}.
	 *
	 * @param processor
	 *            The processor that report this message. Can be null.
	 * @param level
	 *            The level of the report
	 * @param element
	 *            The CtElement to which the report is associated
	 * @param message
	 *            The message to report
	 */
	void report(Processor<?> processor, Level level, CtElement element, String message);

	/**
	 * This method should be called to print out a message during the
	 * processing.
	 *
	 * @param processor
	 *            The processor that report this message. Can be null.
	 * @param level
	 *            The level of the report
	 * @param message
	 *            The message to report
	 */
	void report(Processor<?> processor, Level level, String message);

	/**
	 * This method should be called to report the end of the processing.
	 */
	void reportEnd();

	/**
	 * This method should be called to print out a progress message during the
	 * processing. On contrary to regular messages, progress messages are not
	 * meant to remain in the message logs and just indicate to the user some
	 * task progression information.
	 *
	 * @param message a message to print
	 */
	void reportProgressMessage(String message);

	/**
	 * Sets the default file generator for this environment.
	 *
	 * @param generator a file generator to set as the default
	 */
	void setDefaultFileGenerator(FileGenerator<? extends CtElement> generator);

	/**
	 * Sets the processing manager of this environment.
	 *
	 * @param manager a processing manager to set as the default
	 */
	void setManager(ProcessingManager manager);

	/**
	 * This method can be called to stop the processing and all the remaining
	 * tasks. In general, a processor calls it after reporting a fatal error.
	 *
	 * @param processingStopped if true, any ongoing processing is aborted as soon as possible
	 *                          and future processing is prohibited
	 */
	void setProcessingStopped(boolean processingStopped);

	/**
	 * Gets the size of the tabulations in the generated source code.
	 *
	 * @return the current tabulation size
	 */
	int getTabulationSize();

	/**
	 * Sets the size of the tabulations in the generated source code.
	 *
	 * @param size tabulation size to set
	 */
	void setTabulationSize(int size);

	/**
	 * Tells if Spoon uses tabulations in the source code.
	 *
	 * @return true iff Spoon uses tabulations when pretty-printing
	 */
	boolean isUsingTabulations();

	/**
	 * Sets Spoon to use tabulations in the source code.
	 *
	 * @param b	whether Spoon should use tabulations when pretty-printing
	 */
	void useTabulations(boolean b);

	/**
	 * Tell to the Java printer to automatically generate imports and use simple
	 * names instead of fully-qualified name.
	 *
	 * @param autoImports whether Spoon should auto-import types and simplify names
	 */
	void setAutoImports(boolean autoImports);

	/**
	 * Gets the error count from building, processing, and compiling within this
	 * environment.
	 *
	 * @return the amount of errors that have occurred
	 */
	int getErrorCount();

	/**
	 * Gets the warning count from building, processing, and compiling within
	 * this environment.
	 *
	 * @return the amount of warnings that have occurred
	 */
	int getWarningCount();

	/**
	 * Returns the {@code ClassLoader} which is used by JDT and to resolve classes from references.
	 *
	 * By default, returns a class loader able to load classes from the
	 * Spoon-specific class path set with  {@link #setSourceClasspath(String[])}
	 *
	 * @return the currently configured classloader
	 */
	ClassLoader getInputClassLoader();

	/**
	 * Sets a specific classloader for JDT and reference resolution
	 *
	 * @param classLoader a classloader to set
	 */
	void setInputClassLoader(ClassLoader classLoader);

	/**
	 * When set, the generated source code will try to generate code that
	 * preserves the line numbers of the original source code. This option may
	 * lead to difficult-to-read indentation and formatting.
	 *
	 * @param preserveLineNumbers whether Spoon should attempt to preserve line numbers when
	 *                            pretty-printing
	 */
	void setPreserveLineNumbers(boolean preserveLineNumbers);

	/**
	 * Tells if the source generator will try to preserve the original line numbers.
	 *
	 * @return true iff Spoon attempts to preserve line numbers of elements when pretty-printing
	 */
	boolean isPreserveLineNumbers();

	/**
	 * Returns the source class path of the Spoon model.
	 * This class path is used when the SpoonCompiler is building the model and also
	 * to find external classes, referenced from within the model.
	 *
	 * @return all paths in the classpath
	 */
	String[] getSourceClasspath();

	/**
	 * Sets the source class path of the Spoon model.
	 * After the class path is set, it can be retrieved by
	 * {@link #getSourceClasspath()}. Only .jar files or directories with *.class files are accepted.
	 * The *.jar or *.java files contained in given directories are ignored.
	 *
	 * @throws InvalidClassPathException if a given classpath does not exists or
	 * does not have the right format (.jar file or directory)
	 *
	 * @param sourceClasspath classpath to set
	 */
	void setSourceClasspath(String[] sourceClasspath);

	/**
	 * Gets the module path used for sourcing the input modules.
	 * The returned list is immutable and does not contain null values.
	 *
	 * @return A list of strings representing the module path. Each string element
	 *         is the path to a directory or a module jar file.
	 */
	List<String> getSourceModulePath();

	/**
	 * Sets the module path that is used to build/compile the input sources.
	 * This is the equivalent to the {@code --module-path} option of {@code javac} and {@code java} executables.
	 *
	 * @param sourceModulePath The new module path to be set. Each string element
	 *                         should be the path to a directory or a module jar file.
	 * @throws NullPointerException if the argument is null or an element of the list is null.
	 */
	void setSourceModulePath(List<String> sourceModulePath);

	/**
	 * Sets the option "noclasspath", use with caution (see explanation below).
	 *
	 * With this option, Spoon does not require the full classpath to build the
	 * model. In this case, all references to classes that are not in the
	 * classpath are handled with the reference mechanism. The "simplename" of
	 * the reference object refers to the unbound identifier.
	 *
	 * This option facilitates the use of Spoon when it is hard to have the
	 * complete and correct classpath, for example for mining software
	 * repositories.
	 *
	 * For writing analyses, this option works well if you don't cross the
	 * reference by a call to getDeclaration() (if you really want to do so,
	 * then check for nullness of the result before).
	 *
	 * In normal mode, compilation errors are signaled as exception, with this
	 * option enabled they are signaled as message only. The reason is that in
	 * most cases, there are necessarily errors related to the missing classpath
	 * elements.
	 *
	 * @param option whether to set Spoon to noclasspath mode
	 */
	void setNoClasspath(boolean option);

	/**
	 * Returns the value ot the option noclasspath
	 *
	 * @return true iff Spoon is currently in noclasspath mode
	 */
	boolean getNoClasspath();

	/**
	 * Sets the option ignore-syntax-errors to remove files with any syntax errors or JLS violations from the compilation batch.
	 * Also while transformations no checks for JLS correctness are reported as error.
	 *
	 * @param ignoreSyntaxErrors whether Spoon should ignore files with any syntax errors or JLS violations
	 */
	void setIgnoreSyntaxErrors(boolean ignoreSyntaxErrors);

	/**
	 * Returns the value ot the option ignore-syntax-errors.
	 *
	 * @return true iff Spoon ignores files with any syntax errors, JLS violations or reports JLS correctness problems as exception.
	 */
	boolean getIgnoreSyntaxErrors();

	/**
	 * Returns the value of the option copy-resources.
	 *
	 * @return true iff Spoon should copy resource files from the project when pretty-printing
	 */
	boolean isCopyResources();

	/**
	 * Sets the option copy-resources to copy all resources in a project on the folder destination.
	 *
	 * @param copyResources whether Spoon should copy resources
	 */
	void setCopyResources(boolean copyResources);

	/**
	 * Returns the value of the option enable-comments.
	 *
	 * @return true iff Spoon respects comments in source code
	 */
	boolean isCommentsEnabled();

	/**
	 * Sets the option enable-comments to parse comments of the target project.
	 *
	 * @param commentEnabled whether Spoon should respect comments in source code
	 */
	void setCommentEnabled(boolean commentEnabled);

	/**
	 * Gets the level of loggers asked by the user.
	 *
	 * @return the current logging level
	 */
	Level getLevel();

	/**
	 * Sets the level of loggers asked by the user.
	 *
	 * @param level the logging level to set, see {@link Level} for options
	 */
	void setLevel(String level);

	/**
	 * Checks if we want compile the target source code and get their binary.
	 *
	 * @return true iff Spoon should compile target source code
	 */
	boolean shouldCompile();

	/**
	 * Sets the compile argument.
	 *
	 * @param shouldCompile whether Spoon should compile target source code
	 */
	void setShouldCompile(boolean shouldCompile);

	/**
	 * Tells whether Spoon does no checks at all.
	 * - parents are consistent (see {@link spoon.reflect.visitor.AstParentConsistencyChecker})
	 * - hashcode violation (see {@link spoon.support.reflect.declaration.CtElementImpl#equals(Object)})
	 * - method violation (see {@link spoon.reflect.declaration.CtType#addMethod(CtMethod)})
	 * are active or not.
	 *
	 * By default all checks are enabled and {@link #checksAreSkipped()} return false.
	 *
	 * @return true iff Spoon skips consistency checks
	 */
	boolean checksAreSkipped();

	/**
	 * Disable all consistency checks on the AST. Dangerous! The only valid usage of this is to keep
	 * full backward-compatibility.
	 */
	void disableConsistencyChecks();


	/**
	 * Set the directory where binary .class files are created
	 *
	 * @param directory path to set for the binary output directory
	 */
	void setBinaryOutputDirectory(String directory);

	/**
	 * Get the directory where binary .class files are created
	 *
	 * @return path to the binary output directory
	 */
	String getBinaryOutputDirectory();

	/**
	 * Sets the directory where source files are written
	 *
	 * @param directory path to set for the source output directory
	 */
	void setSourceOutputDirectory(File directory);

	/**
	 * Returns the directory where source files are written
	 *
	 * @return path to the source output directory
	 */
	File getSourceOutputDirectory();

	/**
	 * Set the output destination that handles where source files are written
	 *
	 * @param outputDestinationHandler handler for determining where to write source files
	 */
	void setOutputDestinationHandler(OutputDestinationHandler outputDestinationHandler);

	/**
	 * Returns the output destination that handles where source files are written
	 *
	 * @return the current output destination handler
	 */
	OutputDestinationHandler getOutputDestinationHandler();

	/**
	 * get the model change listener that is used to follow the change of the AST.
	 *
	 * @return the current change listener
	 */
	FineModelChangeListener getModelChangeListener();

	/**
	 * Set the model change listener
	 *
	 * @param modelChangeListener change listener to set
	 */
	void setModelChangeListener(FineModelChangeListener modelChangeListener);

	/**
	 * Get the encoding used inside the project
	 *
	 * @return encoding used in the project
	 */
	Charset getEncoding();

	/**
	 * Get encoding provider, which is used to detect encoding for each file separately
	 *
	 * @return the current encoding provider
	 */
	EncodingProvider getEncodingProvider();

	/**
	 * Set the encoding to use for parsing source code
	 *
	 * @param encoding the character set to use for source file encoding
	 */
	void setEncoding(Charset encoding);

	/**
	 * Set encoding provider, which is used to detect encoding for each file separately
	 *
	 * @param encodingProvider the encoding provider to set
	 */
	void setEncodingProvider(EncodingProvider encodingProvider);

	/**
	 * Set the output type used for processing files
	 *
	 * @param outputType output type to use when pretty-printing
	 */
	void setOutputType(OutputType outputType);

	/**
	 * Get the output type
	 *
	 * @return the current output type
	 */
	OutputType getOutputType();

	/**
	 * Get the spoonProgress logger. This method mustn't return null.
	 *
	 * @return the spoonProgress
	 */
	SpoonProgress getSpoonProgress();

	void setSpoonProgress(SpoonProgress spoonProgress);

	/**
	 * Get the type of serialization to be used by default
	 *
	 * @return the current type of serialization
	 */
	CompressionType getCompressionType();

	/**
	 * Set the type of serialization to be used by default
	 *
	 * @param serializationType the type of serialization to set
	 */
	void setCompressionType(CompressionType serializationType);

	/**
	 * Creates the default pretty-printer.
	 *
	 * @return new instance of {@link PrettyPrinter} which is configured for this environment
	 */
	PrettyPrinter createPrettyPrinter();

	/**
	 * Creates a pretty-printer that automatically imports used types and turns fully qualified
	 * type names into simply qualified names.
	 *
	 * This is roughly equivalent to setting {@link Environment#setAutoImports(boolean)} to
	 * {@code true} and then invokin {@link Environment#createPrettyPrinter()}, except that the
	 * environment is not modified.
	 *
	 * @return new instance of {@link PrettyPrinter} which prints nice code
	 */
	PrettyPrinter createPrettyPrinterAutoImport();

	/**
	 * Sets a custom pretty-printer that overrides the default pretty-printer.
	 *
	 * Can for example be used to create a {@link SniperJavaPrettyPrinter} for enabling the sniper
	 * mode.
	 *
	 * <code>
	 *     env.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(env));
	 * </code>
	 *
	 * @param creator a {@link Supplier}, which creates new instance of pretty printer.
	 */
	void setPrettyPrinterCreator(Supplier<PrettyPrinter> creator);

	/**
	 * Whether to use the new or legacy and (soon to be) deprecated type adaption. You should not use this method.
	 *
	 * @return true if spoon uses the old and (soon to be) deprecated type adaption.
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated(forRemoval = true, since = "10.2.0")
	boolean useLegacyTypeAdaption();

	/**
	 * Sets whether to use the new or legacy and (soon to be) deprecated type adaption. You should not use this method.
	 *
	 * @param useLegacyTypeAdaption whether to use the old type adaption implementation
	 */
	@Deprecated(forRemoval = true, since = "10.2.0")
	void setUseLegacyTypeAdaption(boolean useLegacyTypeAdaption);

	/**
	 * Whether Spoon currently ignores duplicate declarations of types.
	 *
	 * @return true if spoon is allowed to create a model of a project that contains multiple copies of the same class
	 */
	boolean isIgnoreDuplicateDeclarations();

	/**
	 * Set Spoon to ignore duplicate type declarations in a project.
	 *
	 * Setting this option to {@code true} causes Spoon to attempt to build a model even when
	 * the same qualified name appears for multiple types. This mode of operation makes Spoon less
	 * stable as duplicated types do not make sense in Java, and causes strange behavior in the
	 * underlying JDT compiler. Type resolution can become unpredictable as the order in which types
	 * are parsed becomes a determining factor in which duplicated type actually makes it into the
	 * model.
	 *
	 * @param ignoreDuplicateDeclarations (default false)  set to true to allow spoon to create a model of a project that
	 *                                 contains multiple times the same class
	 */
	void setIgnoreDuplicateDeclarations(boolean ignoreDuplicateDeclarations);

	/** Drives how the model is pretty-printed to disk, or when {@link CtElement#prettyprint()} is called */
	enum PRETTY_PRINTING_MODE {
		/** direct in {@link spoon.reflect.visitor.DefaultJavaPrettyPrinter}, no preprocessors are applied to the model before pretty-printing }. */
		DEBUG,

		/** autoimport mode, adds as many imports as possible */
		AUTOIMPORT,

		/** force everything to be fully-qualified */
		FULLYQUALIFIED
	}
}
