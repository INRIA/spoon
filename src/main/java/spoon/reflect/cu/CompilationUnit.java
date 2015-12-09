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
package spoon.reflect.cu;

import java.io.File;
import java.util.List;

import spoon.processing.FactoryAccessor;
import spoon.reflect.declaration.CtType;

/**
 * Defines a compilation unit. In Java, a compilation unit can contain only one
 * public type declaration and other secondary types declarations (not public).
 */
public interface CompilationUnit extends FactoryAccessor {

	/**
	 * Gets the file that corresponds to this compilation unit if any (contains
	 * the source code).
	 */
	File getFile();

	/**
	 * Sets the file that corresponds to this compilation unit.
	 */
	void setFile(File file);

	/**
	 * Gets all the types declared in this compilation unit.
	 */
	List<CtType<?>> getDeclaredTypes();

	/**
	 * Sets the types declared in this compilation unit.
	 */
	void setDeclaredTypes(List<CtType<?>> types);

	/**
	 * Searches and returns the main type (the type which has the same name as
	 * the file).
	 */
	CtType<?> getMainType();

	/**
	 * Gets the original source code as a string.
	 */
	String getOriginalSourceCode();

	/**
	 * Helper method to get the begin index of the line that corresponds to the
	 * given index.
	 *
	 * @param index
	 * 		an arbitrary index in the source code
	 * @return the index where the line starts
	 */
	int beginOfLineIndex(int index);

	/**
	 * Helper method to get the begin index of the line that corresponds to the
	 * next line of the given index.
	 *
	 * @param index
	 * 		an arbitrary index in the source code
	 * @return the index where the next line starts
	 */
	int nextLineIndex(int index);

	/**
	 * Gets the number of tabulations for a given line.
	 *
	 * @param index
	 * 		the index where the line starts in the source code
	 * @return the number of tabs for this line
	 */
	int getTabCount(int index);

}
