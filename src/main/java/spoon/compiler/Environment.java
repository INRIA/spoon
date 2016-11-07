/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.compiler;

import org.apache.log4j.Level;
import spoon.processing.FileGenerator;
import spoon.processing.ProblemFixer;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.processing.ProcessorProperties;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

/**
 * This interface represents the environment in which Spoon is launched -
 * accessible through {@link spoon.reflect.factory.Factory#getEnvironment()}. Its
 * primary use is to report messages, warnings, and errors.
 */
public interface Environment {

	/**
	 * Gets the Java version compliance level.
	 */
	int getComplianceLevel();

	/**
	 * Sets the Java version compliance level.
	 */
	void setComplianceLevel(int level);

	/**
	 * This method should be called to print out a message with a source
	 * position link during the processing.
	 */
	void debugMessage(String message);

	/**
	 * Returns the default file generator for this environment (gives the
	 * default output directory for the created files).
	 */
	FileGenerator<? extends CtElement> getDefaultFileGenerator();

	/**
	 * Gets the processing manager.
	 */
	ProcessingManager getManager();

	/**
	 * Returns the properties for a given processor.
	 */
	ProcessorProperties getProcessorProperties(String processorName)
			throws Exception;

	/**
	 * Sets the properties for a given processor.
	 */
	void setProcessorProperties(String processorName, ProcessorProperties prop);

	/**
	 * Returns true is we let Spoon handle imports
	 */
	boolean isAutoImports();

	/**
	 * Tells if the processing is stopped, generally because one of the
	 * processors called {@link #setProcessingStopped(boolean)} after reporting
	 * an error.
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
	 * @param fixes
	 *            The problem fixer(s) to correct this problem
	 */
	void report(Processor<?> processor, Level level,
				CtElement element, String message, ProblemFixer<?>... fixes);

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
	 */
	void reportProgressMessage(String message);

	/**
	 * Sets the default file generator for this environment.
	 */
	void setDefaultFileGenerator(FileGenerator<? extends CtElement> generator);

	/**
	 * Sets the processing manager of this environment.
	 */
	void setManager(ProcessingManager manager);

	/**
	 * This method can be called to stop the processing and all the remaining
	 * tasks. In general, a processor calls it after reporting a fatal error.
	 */
	void setProcessingStopped(boolean processingStopped);

	/**
	 * Gets the size of the tabulations in the generated source code.
	 */
	int getTabulationSize();

	/**
	 * Sets the size of the tabulations in the generated source code.
	 */
	void setTabulationSize(int size);

	/**
	 * Tells if Spoon uses tabulations in the source code.
	 */
	boolean isUsingTabulations();

	/**
	 * Sets Spoon to use tabulations in the source code.
	 */
	void useTabulations(boolean b);

	/**
	 * Tell to the Java printer to automatically generate imports and use simple
	 * names instead of fully-qualified name.
	 */
	void setAutoImports(boolean autoImports);

	/**
	 * Gets the error count from building, processing, and compiling within this
	 * environment.
	 */
	int getErrorCount();

	/**
	 * Gets the warning count from building, processing, and compiling within
	 * this environment.
	 */
	int getWarningCount();

	/**
	 * Returns the {@code ClassLoader} which is used by JDT and to resolve classes from references.
	 *
	 * By default, returns a class loader able to load classes from the
	 * Spoon-specific class path set with  {@link #setSourceClasspath(String[])}
	 */
	ClassLoader getInputClassLoader();

	/**
	 * Sets a specific classloader for JDT and reference resolution
	 */
	void setInputClassLoader(ClassLoader classLoader);

	/**
	 * When set, the generated source code will try to generate code that
	 * preserves the line numbers of the original source code. This option may
	 * lead to difficult-to-read indentation and formatting.
	 */
	void setPreserveLineNumbers(boolean preserveLineNumbers);

	/**
	 * Tells if the source generator will try to preserve the original line numbers.
	 */
	boolean isPreserveLineNumbers();

	/**
	 * Returns the source class path of the Spoon model.
	 * This class path is used when the SpoonCompiler is building the model and also
	 * to find external classes, referenced from within the model.
	 */
	String[] getSourceClasspath();

	/**
	 * Sets the source class path of the Spoon model.
	 * After the class path is set, it can be retrieved by
	 * {@link #getSourceClasspath()}.
	 */
	void setSourceClasspath(String[] sourceClasspath);

	/**
	 * Use {@link #getInputClassLoader()}
	 */
	@Deprecated
	ClassLoader getClassLoader();

	/**
	 * Sets the option "noclasspath", use with caution (see explanation below).
	 *
	 * With this option, Spoon does not require the full classpath to build the
	 * model. In this case, all references to classes that are not in the
	 * classpath are handled with the reference mechanism. The "simplename" of
	 * the reference object refers to the unbound identifier.
	 *
	 * This option facilitates the use of Spoon when is is hard to have the
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
	 */
	void setNoClasspath(boolean option);

	/** Returns the value ot the option noclasspath */
	boolean getNoClasspath();

	/**
	 * Returns the value of the option copy-resources.
	 */
	boolean isCopyResources();

	/**
	 * Sets the option copy-resources to copy all resources in a project on the folder destination.
	 */
	void setCopyResources(boolean copyResources);

	/**
	 * Returns the value of the option enable-comments.
	 */
	boolean isCommentsEnabled();

	/**
	 * Sets the option enable-comments to parse comments of the target project.
	 */
	void setCommentEnabled(boolean commentEnabled);

	/**
	 * Gets the factory of the environment.
	 */
	Factory getFactory();

	/**
	 * Gets the level of loggers asked by the user.
	 */
	Level getLevel();

	/**
	 * Sets the level of loggers asked by the user.
	 */
	void setLevel(String level);

	/**
	 * Checks if we want compile the target source code and get their binary.
	 */
	boolean shouldCompile();

	/**
	 * Sets the compile argument.
	 */
	void setShouldCompile(boolean shouldCompile);

	/**
	 * Checks if {@link spoon.reflect.visitor.AstParentConsistencyChecker},
	 * hashcode violation declared in CtElement#equals(CtElement) and
	 * method violation declared in {@link spoon.reflect.declaration.CtType#addMethod(CtMethod)}
	 * are active or not.
	 */
	boolean checksAreSkipped();

	/**
	 * Enable or not checks on the AST. See {@link #checksAreSkipped()} to know all checks enabled.
	 * true means that no self checks are made.
	 */
	void setSelfChecks(boolean skip);

	/** Return the directory where binary .class files are created */
	void setBinaryOutputDirectory(String directory);

	/** Set the directory where binary .class files are created */
	String getBinaryOutputDirectory();
}
