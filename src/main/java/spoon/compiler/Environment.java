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

package spoon.compiler;

import java.io.File;

import spoon.processing.FileGenerator;
import spoon.processing.ProblemFixer;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.processing.ProcessorProperties;
import spoon.processing.Severity;
import spoon.reflect.declaration.CtElement;

/**
 * This interface represents the environment in which Spoon is launched -
 * accessible through {@link spoon.reflect.factory.Factory#getEnvironment()}. Its
 * primary use is to report messages, warnings, and errors.
 */
public interface Environment {

	/**
	 * Gets the Java version compliance level.
	 *
	 * @return the level
	 */
	public int getComplianceLevel();

	/**
	 * Sets the Java version compliance level.
	 *
	 * @param level the level to set
	 */
	public void setComplianceLevel(int level);

	/**
	 * This method should be called to print out a message with a source
	 * position link during the processing.
	 *
	 * @param message The message to report
	 */
	public void debugMessage(String message);

	/**
	 * Returns the default file generator for this environment (gives the
	 * default output directory for the created files).
	 *
	 * @return the default file generator
	 */
	public FileGenerator<? extends CtElement> getDefaultFileGenerator();

	/**
	 * Gets the processing manager.
	 *
	 * @return the processing manager
	 */
	ProcessingManager getManager();

	/**
	 * Returns the properties for a given processor.
	 *
	 * @param processorName the name of the processor to get the properties for
	 *
	 * @return the properties of the processor
	 *
	 * @throws java.lang.Exception in case the configuration can not be processed
	 */
	ProcessorProperties getProcessorProperties(String processorName)
			throws Exception;

	/**
	 * Sets the properties for a given processor.
	 *
	 * @param processorName the name of the processor to set the properties for
	 * @param prop the properties to set
	 */
	void setProcessorProperties(String processorName, ProcessorProperties prop);

	/**
	 * Returns true if Spoon is in debug mode.
	 *
	 * @return true if debugging is enabled
	 */
	public boolean isDebug();

	/**
	 * Returns true is we let Spoon handle imports
	 *
	 * @return true if auto imports is enabled
	 */
	public boolean isAutoImports();

	/**
	 * Tells if the processing is stopped, generally because one of the
	 * processors called {@link #setProcessingStopped(boolean)} after reporting
	 * an error.
	 *
	 * @return true if the processing is stopped
	 */
	public boolean isProcessingStopped();

	/**
	 * Returns true if Spoon is in verbose mode.
	 *
	 * @return true if spoon is in verbose mode
	 */
	public boolean isVerbose();

	/**
	 * Helper method called by a processor to report an error, warning or
	 * message as dictated by the severity parameter. Note that this does not
	 * stop the processing or any remaing task. To do so, use
	 * {@link #setProcessingStopped(boolean)}.
	 * 
	 * @param processor
	 *            The processor that report this message. Can be null.
	 * @param severity
	 *            The severity of the report
	 * @param element
	 *            The CtElement to which the report is associated
	 * @param message
	 *            The message to report
	 */
	public void report(Processor<?> processor, Severity severity,
			CtElement element, String message);

	/**
	 * Helper method called by a processor to report an error, warning or
	 * message as dictated by the severity parameter. Note that this does not
	 * stop the processing or any remaining task. To do so, use
	 * {@link #setProcessingStopped(boolean)}.
	 * 
	 * @param processor
	 *            The processor that report this message. Can be null.
	 * @param severity
	 *            The severity of the report
	 * @param element
	 *            The CtElement to which the report is associated
	 * @param message
	 *            The message to report
	 * @param fixes
	 *            The problem fixer(s) to correct this problem
	 */
	public void report(Processor<?> processor, Severity severity,
			CtElement element, String message, ProblemFixer<?>... fixes);

	/**
	 * This method should be called to print out a message during the
	 * processing.
	 * 
	 * @param processor
	 *            The processor that report this message. Can be null.
	 * @param severity
	 *            The severity of the report
	 * @param message
	 *            The message to report
	 */
	public void report(Processor<?> processor, Severity severity, String message);

	/**
	 * This method should be called to report the end of the processing.
	 */
	public void reportEnd();

	/**
	 * This method should be called to print out a progress message during the
	 * processing. On contrary to regular messages, progress messages are not
	 * meant to remain in the message logs and just indicate to the user some
	 * task progression information.
	 *
	 * @param message The message to report
	 */
	public void reportProgressMessage(String message);

	/**
	 * Sets the debug mode.
	 *
	 * @param debug true to enable debugging, false to disable
	 */
	public void setDebug(boolean debug);

	/**
	 * Sets the default file generator for this environment.
	 *
	 * @param generator the file generator to set as default
	 */
	void setDefaultFileGenerator(FileGenerator<? extends CtElement> generator);

	/**
	 * Sets the processing manager of this environment.
	 *
	 * @param manager the processing manager to set
	 */
	void setManager(ProcessingManager manager);

	/**
	 * This method can be called to stop the processing and all the remaining
	 * tasks. In general, a processor calls it after reporting a fatal error.
	 *
	 * @param processingStopped the new state
	 */
	void setProcessingStopped(boolean processingStopped);

	/**
	 * Sets/unsets the verbose mode.
	 *
	 * @param verbose true to enable verbose mode, false to disable it
	 */
	void setVerbose(boolean verbose);

	/**
	 * Tells if the code generation use code fragments.
	 *
	 * @return true if source code fragments are used
	 */
	boolean isUsingSourceCodeFragments();

	/**
	 * Sets the code generation to use code fragments.
	 *
	 * @param b true to enable source code fragments
	 */
	void useSourceCodeFragments(boolean b);

	/**
	 * Gets the size of the tabulations in the generated source code.
	 *
	 * @return the tab size
	 */
	int getTabulationSize();

	/**
	 * Sets the size of the tabulations in the generated source code.
	 *
	 * @param size the tab size to set
	 */
	void setTabulationSize(int size);

	/**
	 * Tells if Spoon uses tabulations in the source code.
	 *
	 * @return true if tabs are used
	 */
	boolean isUsingTabulations();

	/**
	 * Sets Spoon to use tabulations in the source code.
	 *
	 * @param b true to enable tabs
	 */
	void useTabulations(boolean b);

	/**
	 * Gets the current input path
	 *
	 * @return the source path as a string
	 */
	String getSourcePath();

	/**
	 * Tell to the Java printer to automatically generate imports and use simple
	 * names instead of fully-qualified name.
	 *
	 * @param autoImports true to enable auto imports
	 */
	void setAutoImports(boolean autoImports);

	/**
	 * Sets the root folder where the processors' XML configuration files are
	 * located.
	 *
	 * @param xmlRootFolder the folder where processor configs are located
	 */
	void setXmlRootFolder(File xmlRootFolder);

	/**
	 * Gets the error count from building, processing, and compiling within this
	 * environment.
	 *
	 * @return the number of errors
	 */
	int getErrorCount();

	/**
	 * Gets the warning count from building, processing, and compiling within
	 * this environment.
	 *
	 * @return the number of warnings
	 */
	int getWarningCount();

	/**
	 * Gets the class loader used to compile/process the input source code.
	 *
	 * @return the classloader
	 */
	ClassLoader getInputClassLoader();

	/**
	 * Sets the class loader used to compile/process the input source code.
	 *
	 * @param classLoader the classloader to use
	 */
	void setInputClassLoader(ClassLoader classLoader);

	/**
	 * When set, the generated source code will try to generate code that
	 * preserves the line numbers of the original source code. This option may
	 * lead to difficult-to-read indentation and formatting.
	 *
	 * @param preserveLineNumbers true to preserve line numbers
	 */
	void setPreserveLineNumbers(boolean preserveLineNumbers);

	/**
	 * Tells if the source generator will try to preserve the original line numbers.
	 *
	 * @return true if line numbers are preserved
	 */
	boolean isPreserveLineNumbers();
	
}