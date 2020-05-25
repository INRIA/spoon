/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.cu.position;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.support.reflect.cu.CompilationUnitImpl;

import java.io.File;
import java.io.Serializable;

/**
 * This class represents the position of a program element in a source file.
 */
public class NoSourcePosition implements SourcePosition, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public File getFile() {
		return null;
	}

	// avoid null pointer exceptions later
	public static class NullCompilationUnit extends CompilationUnitImpl {
		private NullCompilationUnit() { }
	}
	private static final CompilationUnit instanceNullCompilationUnit = new NullCompilationUnit();

	@Override
	public CompilationUnit getCompilationUnit() {
		return instanceNullCompilationUnit;
	}

	@Override
	public boolean isValidPosition() {
		return false;
	}

	@Override
	public int getLine() {
		throw new UnsupportedOperationException("PartialSourcePosition only contains a CompilationUnit");
	}

	@Override
	public int getEndLine() {
		throw new UnsupportedOperationException("PartialSourcePosition only contains a CompilationUnit");
	}

	@Override
	public int getColumn() {
		throw new UnsupportedOperationException("PartialSourcePosition only contains a CompilationUnit");
	}

	@Override
	public int getEndColumn() {
		throw new UnsupportedOperationException("PartialSourcePosition only contains a CompilationUnit");
	}

	@Override
	public int getSourceEnd() {
		throw new UnsupportedOperationException("PartialSourcePosition only contains a CompilationUnit");
	}

	@Override
	public int getSourceStart() {
		throw new UnsupportedOperationException("PartialSourcePosition only contains a CompilationUnit");
	}

	@Override
	public String toString() {
		return "(unknown file)";
	}
}
