/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.processing;

import java.io.File;
import java.util.List;

import spoon.reflect.declaration.CtElement;

/**
 * This interface should be implemented by processing tasks that generate new
 * files as processing results. This interface is typically implemented by
 * processors that generate files during processing. For a given processing
 * environment, the default file generator is set to the default output directory
 * that is retrieved by using
 * {@link spoon.compiler.Environment#getDefaultFileGenerator()}.
 */
public interface FileGenerator<T extends CtElement> extends Processor<T> {

	/**
	 * Sets the root directory where files should be created.
	 */
	void setOutputDirectory(File directory);

	/**
	 * Gets the root directory where files are created.
	 */
	File getOutputDirectory();

	/**
	 * Gets the created files.
	 */
	List<File> getCreatedFiles();

}
