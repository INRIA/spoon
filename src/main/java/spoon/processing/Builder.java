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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import spoon.support.builder.CtResource;

/**
 * This interface defines the API to build a Spoon meta-model from input sources
 * given as files. You should add your sources, and use {@link #build()}
 * to create the Spoon meta-model. Once the meta-model is built and stored in
 * the factory, it can be processed by using a
 * {@link spoon.processing.ProcessingManager}. As an example of use, take a
 * look at the {@link spoon.Launcher} implementation.
 */
public interface Builder extends FactoryAccessor {
	/**
	 * Adds a file/directory to be built. By default, the files could be Java
	 * source files or Jar files. Directories are processed recursively.
	 * 
	 * @param source
	 *            file or directory to add
	 */
	void addInputSource(File source) throws IOException;

	/**
	 * Adds a file/directory (as a CtResource) to be built. By default, the
	 * files could be Java source files or Jar files. Directories are processed
	 * recursively.
	 * 
	 * @param source
	 *            file or directory to add
	 */
	void addInputSource(CtResource source) throws IOException;

	/**
	 * Gets all the files/directories given as input sources to this builder
	 * (see {@link #addInputSource(File)}).
	 */
	Set<File> getInputSources();

	/**
	 * Adds a file/directory to be used to build templates. By default, the
	 * files should be Java source files or Jar files containing the sources.
	 * Directories are processed recursively. Templates are set apart from the
	 * program to be processed for logical reasons. However, if a template was
	 * needed to be processed, it could be added as an input source.
	 * 
	 * @param source
	 *            file or directory to add
	 */
	void addTemplateSource(File source) throws IOException;

	/**
	 * Adds a file/directory (as a CtResource) to be used to build templates. By
	 * default, the files should be Java source files or Jar files containing
	 * the sources. Directories are processed recursively. Templates are set
	 * apart from the program to be processed for logical reasons. However, if a
	 * template was needed to be processed, it could be added as an input
	 * source.
	 * 
	 * @param source
	 *            file or directory to add
	 */
	void addTemplateSource(CtResource source) throws IOException;

	/**
	 * Gets all the files/directories given as template sources to this builder
	 * (see {@link #addTemplateSource(File)}).
	 */
	Set<File> getTemplateSources();

	/**
	 * Builds the program's model with the current factory and stores the result
	 * into this factory. Note that this method can only be used once on a given
	 * factory. If more attempts are made, it throws an exception.
	 * 
	 * @return true if the Java was successfully compiled with the core Java
	 *         compiler, false if some errors were encountered while compiling
	 * 
	 * @exception Exception
	 *                when a building problem occurs
	 */
	boolean build() throws Exception;

	/**
	 * This method should be called before starting the compilation in order to
	 * perform plateform specific initializations. Override the method in
	 * subclasses do add new initializations.
	 */
	void initCompiler();

	/**
	 * Gets the list of problems that may have been reported by the compiler
	 * when building the model.
	 */
	List<String> getProblems();

}
