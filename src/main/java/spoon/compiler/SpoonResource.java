/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.compiler;

import java.io.File;

/**
 * This interface defines generic resources that are used by
 * {@link spoon.SpoonModelBuilder} in the Java compilation process.
 */
public interface SpoonResource {

	/**
	 * Gets the folder that contains this resource.
	 */
	SpoonFolder getParent();

	/**
	 * Gets the name of this resource.
	 */
	String getName();

	/**
	 * Tells if this resource is a file.
	 */
	boolean isFile();

	/**
	 * Tells if this resource is an archive.
	 */
	boolean isArchive();

	/**
	 * Gets this resource path.
	 */
	String getPath();

	/**
	 * Gets the parent of this resource on the file system.
	 */
	File getFileSystemParent();

	/**
	 * Gets the corresponding file if possible (returns null if this resource
	 * does not correspond to any file on the filesystem).
	 */
	File toFile();
}
