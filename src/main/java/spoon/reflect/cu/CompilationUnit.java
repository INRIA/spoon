/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.cu;

import spoon.reflect.declaration.CtCompilationUnit;

/**
 * Defines a compilation unit. In Java, a compilation unit can contain only one
 * public type declaration and other secondary types declarations (not public).
 */
public interface CompilationUnit extends CtCompilationUnit {


	/**
	 * Helper method to get the begin index of the line that corresponds to the
	 * given index.
	 *
	 * @param index
	 * 		an arbitrary index in the source code
	 * @return the index where the line starts
	 */
	@Deprecated
	int beginOfLineIndex(int index);

	/**
	 * Helper method to get the begin index of the line that corresponds to the
	 * next line of the given index.
	 *
	 * @param index
	 * 		an arbitrary index in the source code
	 * @return the index where the next line starts
	 */
	@Deprecated
	int nextLineIndex(int index);

	/**
	 * Gets the number of tabulations for a given line.
	 *
	 * @param index
	 * 		the index where the line starts in the source code
	 * @return the number of tabs for this line
	 */
	@Deprecated
	int getTabCount(int index);
}
