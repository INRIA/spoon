/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.cu.position;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.position.DeclarationSourcePosition;

import java.io.Serializable;

/**
 * This class represents the position of a Java program element in a source
 * file.
 */
public class DeclarationSourcePositionImpl extends CompoundSourcePositionImpl
		implements DeclarationSourcePosition, Serializable {

	private static final long serialVersionUID = 1L;
	private int modifierSourceEnd;
	private int modifierSourceStart;

	public DeclarationSourcePositionImpl(CompilationUnit compilationUnit, int sourceStart, int sourceEnd,
			int modifierSourceStart, int modifierSourceEnd, int declarationSourceStart, int declarationSourceEnd,
			int[] lineSeparatorPositions) {
		super(compilationUnit,
				sourceStart, sourceEnd, declarationSourceStart, declarationSourceEnd,
				lineSeparatorPositions);
		checkArgsAreAscending(declarationSourceStart, modifierSourceStart, modifierSourceEnd + 1, sourceStart, sourceEnd + 1, declarationSourceEnd + 1);
		this.modifierSourceStart = modifierSourceStart;
		if (this.modifierSourceStart == 0) {
			this.modifierSourceStart = declarationSourceStart;
		}
		this.modifierSourceEnd = modifierSourceEnd;
	}

	@Override
	public int getModifierSourceStart() {
		return modifierSourceStart;
	}

	public void setModifierSourceEnd(int modifierSourceEnd) {
		this.modifierSourceEnd = modifierSourceEnd;
	}

	@Override
	public int getModifierSourceEnd() {
		return modifierSourceEnd;
	}

	@Override
	public String getSourceDetails() {
		return getFragment(getSourceStart(), getSourceEnd())
				+ "\nmodifier = " + getFragment(getModifierSourceStart(), getModifierSourceEnd())
				+ "\nname = " + getFragment(getNameStart(), getNameEnd());
	}

}
