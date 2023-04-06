/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.cu.position;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.position.CompoundSourcePosition;

/**
 * This class represents the position of a named Java program element in a source
 * file.
 */
public class CompoundSourcePositionImpl extends SourcePositionImpl
		implements CompoundSourcePosition {

	private static final long serialVersionUID = 1L;
	private int declarationSourceStart;
	private int declarationSourceEnd;

	public CompoundSourcePositionImpl(CompilationUnit compilationUnit, int nameStart, int nameEnd,
			int declarationSourceStart, int declarationSourceEnd,
			int[] lineSeparatorPositions) {
		// by convention, the default start and end fields
		// are used for the name position
		super(compilationUnit,
				nameStart, nameEnd,
				lineSeparatorPositions);
		checkArgsAreAscending(declarationSourceStart, declarationSourceEnd);
		if (nameStart != 0) {
			checkArgsAreAscending(declarationSourceStart, nameStart, nameEnd + 1, declarationSourceEnd + 1);
		}
		this.declarationSourceStart = declarationSourceStart;
		this.declarationSourceEnd = declarationSourceEnd;
	}

	@Override
	public int getDeclarationEnd() {
		return declarationSourceEnd;
	}

	@Override
	public int getDeclarationStart() {
		return declarationSourceStart;
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
