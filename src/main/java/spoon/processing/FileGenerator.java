/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
	 * Gets the root directory where files are created.
	 */
	File getOutputDirectory();

	/**
	 * Gets the created files.
	 */
	List<File> getCreatedFiles();

}
