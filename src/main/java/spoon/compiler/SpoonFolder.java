/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.compiler;

import java.util.List;

/**
 * A Spoon resource that represents a folder.
 */
public interface SpoonFolder extends SpoonResource {

	/**
	 * Gets all the files (excluding folders) in the folder.
	 */
	List<SpoonFile> getFiles();

	/**
	 * Gets all the files (including folders) in the folder.
	 */
	List<SpoonFile> getAllFiles();

	/**
	 * Gets all the Java source files in the folder.
	 */
	List<SpoonFile> getAllJavaFiles();

	/**
	 * Gets the subfolders in this folder.
	 */
	List<SpoonFolder> getSubFolders();

	/**
	 * Adds a file in this folder
	 */
	void addFile(SpoonFile source);

	/**
	 * Adds a sub folder in this folder
	 */
	void addFolder(SpoonFolder source);
}
