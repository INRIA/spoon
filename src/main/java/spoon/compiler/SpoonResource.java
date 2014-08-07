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

/**
 * This interface defines generic resources that are used by
 * {@link SpoonCompiler} in the Java compilation process.
 */
public interface SpoonResource {

	/**
	 * Gets the folder that contains this resource.
	 *
	 * @return The parent spoon folder of this resource
	 */
	SpoonFolder getParent();

	/**
	 * Gets the name of this resource.
	 *
	 * @return the name of this resource
	 */
	String getName();

	/**
	 * Tells if this resource is a file.
	 *
	 * @return true if this file is an actual file on the filesystem
	 */
	boolean isFile();

	/**
	 * Tells if this resource is an archive.
	 *
	 * @return true if this is resource is an archive
	 */
	boolean isArchive();

	/**
	 * Gets this resource path.
	 *
	 * @return the path of this resource as a string
	 */
	String getPath();

	/**
	 * Gets the parent of this resource on the file system.
	 *
	 * @return a File instance for this resource' parent
	 */
	File getFileSystemParent();

	/**
	 * Gets the corresponding file if possible (returns null if this resource
	 * does not correspond to any file on the filesystem).
	 *
	 * @return a File instance for this resource
	 */
	File toFile();

}
