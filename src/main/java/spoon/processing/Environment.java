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

package spoon.processing;

import spoon.reflect.declaration.CtElement;

/**
 * This interface represents the environment in which Spoon is launched -
 * accessible through {@link spoon.reflect.Factory#getEnvironment()}. Its
 * primary use is to report messages, warnings, and errors.
 */
public interface Environment extends FactoryAccessor {

	/**
	 * Gets the Java version compliance level.
	 */
	public int getComplianceLevel();

	/**
	 * Sets the Java version compliance level.
	 */
	public void setComplianceLevel(int level);

	/**
	 * This method should be called to print out a message with a source
	 * position link during the processing.
	 */
	public void debugMessage(String message);

	/**
	 * Returns the default file generator for this environment (gives the
	 * default output directory for the created files).
	 */
	public FileGenerator<? extends CtElement> getDefaultFileGenerator();

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
	 * Returns true if Spoon is in debug mode.
	 */
	public boolean isDebug();

	/**
	 * Tells if the processing is stopped, generally because one of the
	 * processors called {@link #setProcessingStopped(boolean)} after reporting
	 * an error.
	 */
	public boolean isProcessingStopped();

	/**
	 * Returns true if Spoon is in verbose mode.
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
	 */
	public void reportProgressMessage(String message);

	/**
	 * Sets the debug mode.
	 */
	public void setDebug(boolean debug);

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
	 * Sets/unsets the verbose mode.
	 */
	void setVerbose(boolean verbose);

	/**
	 * Tells if the code generation use code fragments.
	 */
	boolean isUsingSourceCodeFragments();

	/**
	 * Sets the code generation to use code fragments.
	 */
	void useSourceCodeFragments(boolean b);

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
	 * Gets the current input path
	 */
	String getSourcePath();
	
}