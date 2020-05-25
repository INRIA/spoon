/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.cu;

import spoon.compiler.Environment;
import spoon.reflect.cu.position.NoSourcePosition;

import java.io.File;
import java.io.Serializable;

/**
 * This interface represents the position of a program element in a source file.
 */
public interface SourcePosition extends Serializable {

	SourcePosition NOPOSITION = new NoSourcePosition();

	/**
	 * @return true if this instance holds start/end indexes of related sources.
	 * false if they are unknown
	 */
	boolean isValidPosition();

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
	 * For CtNamedElement the line is where the name is declared.
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
	 * Gets the index at which the position ends in the source file.
	 */
	int getSourceEnd();

	/**
	 * Gets the index at which the position starts in the source file.
	 */
	int getSourceStart();
}
