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

import java.util.List;

/**
 * A Spoon resource that represents a folder.
 */
public interface SpoonFolder extends SpoonResource {

	/**
	 * Gets all the files (excluding folders) in the folder.
	 *
	 * @return a List of all files but no folders
	 */
	List<SpoonFile> getFiles();

	/**
	 * Gets all the files (including folders) in the folder.
	 *
	 * @return a List of all files
	 */
	List<SpoonFile> getAllFiles();

	/**
	 * Gets all the Java source files in the folder.
	 *
	 * @return a List of all java source files
	 */
	List<SpoonFile> getAllJavaFiles();

	/**
	 * Gets the subfolders in this folder.
	 *
	 * @return the List of sub folders
	 */
	List<SpoonFolder> getSubFolders();

}
