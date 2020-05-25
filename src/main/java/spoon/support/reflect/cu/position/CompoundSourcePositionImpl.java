/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.cu.position;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.position.CompoundSourcePosition;

import java.io.Serializable;

/**
 * This class represents the position of a Java program element in a source
 * file.
 */
public class CompoundSourcePositionImpl extends SourcePositionImpl
		implements CompoundSourcePosition, Serializable {

	private static final long serialVersionUID = 1L;
	private int declarationSourceStart;
	private int declarationSourceEnd;

	public CompoundSourcePositionImpl(CompilationUnit compilationUnit, int sourceStart, int sourceEnd,
			int declarationSourceStart, int declarationSourceEnd,
			int[] lineSeparatorPositions) {
		super(compilationUnit,
				sourceStart, sourceEnd,
				lineSeparatorPositions);
		checkArgsAreAscending(declarationSourceStart, sourceStart, sourceEnd + 1, declarationSourceEnd + 1);
		this.declarationSourceStart = declarationSourceStart;
		this.declarationSourceEnd = declarationSourceEnd;
	}

	@Override
	public int getSourceEnd() {
		return declarationSourceEnd;
	}

	@Override
	public int getSourceStart() {
		return declarationSourceStart;
	}

	@Override
	public int getNameStart() {
		return super.getSourceStart();
	}

	@Override
	public int getNameEnd() {
		return super.getSourceEnd();
	}

	@Override
	public int getEndLine() {
		return searchLineNumber(declarationSourceEnd);
	}

	@Override
	public int getEndColumn() {
		return searchColumnNumber(declarationSourceEnd);
	}

	@Override
	public String getSourceDetails() {
		return super.getSourceDetails()
				+ "\nname = " + getFragment(getNameStart(), getNameEnd());
	}

}
