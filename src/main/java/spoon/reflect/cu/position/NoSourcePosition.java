/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.reflect.cu.position;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;

import java.io.File;
import java.io.Serializable;

/**
 * This interface represents the position of a program element in a source file.
 */
public class NoSourcePosition implements SourcePosition, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public File getFile() {
		return null;
	}

	@Override
	public CompilationUnit getCompilationUnit() {
		return null;
	}

	@Override
	public int getLine() {
		return -1;
	}

	@Override
	public int getEndLine() {
		return -1;
	}

	@Override
	public int getColumn() {
		return -1;
	}

	@Override
	public int getEndColumn() {
		return -1;
	}

	@Override
	public int getSourceEnd() {
		return -1;
	}

	@Override
	public int getSourceStart() {
		return -1;
	}

	@Override
	public String toString() {
		return "(unknown file)";
	}
}
