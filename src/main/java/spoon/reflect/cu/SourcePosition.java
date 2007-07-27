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

package spoon.reflect.cu;

import java.io.File;

import spoon.processing.Environment;

/**
 * This interface represents the position of a program element in a source file.
 */
public interface SourcePosition extends Cloneable {

	/**
	 * Returns a string representation of this position in the form
	 * "sourcefile:line", or "sourcefile" if no line number is available.
	 */
	String toString();

	/**
	 * Gets the file for this position.
	 */
	File getFile();

	/**
	 * Gets the compilation unit for this position.
	 */
	CompilationUnit getCompilationUnit();

	/**
	 * Gets the line in the source file (1 indexed). Prefer using
	 * {@link #getSourceStart()}}.
	 */
	int getLine();

	/**
	 * Gets the end line in the source file (1 indexed). Prefer using
	 * {@link #getSourceEnd()}}.
	 */
	int getEndLine();

	/**
	 * Gets the column in the source file (1 indexed). This method is slow
	 * because it has to calculate the column number depending on
	 * {@link Environment#getTabulationSize()} and
	 * {@link CompilationUnit#getOriginalSourceCode()}. Prefer {@link #getSourceStart()}.
	 */
	int getColumn();

	/**
	 * Gets the end column in the source file (1 indexed). This method is slow
	 * because it has to calculate the column number depending on
	 * {@link Environment#getTabulationSize()} and
	 * {@link CompilationUnit#getOriginalSourceCode()}. Prefer {@link #getSourceEnd()}.
	 */
	int getEndColumn();

	/**
	 * Clones this position.
	 */
	Object clone() throws CloneNotSupportedException;

	/**
	 * Gets the index at which the position ends in the source file.
	 */
	int getSourceEnd();

	/**
	 * Gets the index at which the position starts in the source file.
	 */
	int getSourceStart();

}
