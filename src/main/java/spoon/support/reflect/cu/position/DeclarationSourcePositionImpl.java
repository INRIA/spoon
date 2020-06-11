/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.cu.position;

import spoon.SpoonException;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.position.DeclarationSourcePosition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This class represents the position of a Java program element in a source
 * file.
 */
public class DeclarationSourcePositionImpl extends CompoundSourcePositionImpl
		implements DeclarationSourcePosition, Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private int modifierSourceEnd;
	private int modifierSourceStart;

	/** int i=0, j=1 -> would end at the comma */
	private int endDefaultValueDeclaration = -1;

	@Override
	public int getDefaultValueEnd() {
		return endDefaultValueDeclaration;
	}

	@Override
	public DeclarationSourcePosition addDefaultValueEnd(int endDefaultValueDeclaration) {
		// JDT initializes to 0
		// so 0 means nothing interesting
		// we prefer the -1 convention here
		if (endDefaultValueDeclaration == 0) {
			return this;
		}

		try {
			DeclarationSourcePositionImpl newPos = (DeclarationSourcePositionImpl) this.clone();
			newPos.endDefaultValueDeclaration = endDefaultValueDeclaration;
			return newPos;
		} catch (CloneNotSupportedException e) {
			throw new SpoonException(e);
		}
	}

	public DeclarationSourcePositionImpl(CompilationUnit compilationUnit, int nameStart, int nameEnd,
			int modifierSourceStart, int modifierSourceEnd, int declarationSourceStart, int declarationSourceEnd,
			int[] lineSeparatorPositions) {
		super(compilationUnit,
				nameStart, nameEnd, declarationSourceStart, declarationSourceEnd,
				lineSeparatorPositions);
		checkArgsAreAscending(declarationSourceStart, modifierSourceStart, modifierSourceEnd + 1, nameStart, nameEnd + 1, declarationSourceEnd + 1);
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

	@Override
	public int getSourceEnd() {
		if (endDefaultValueDeclaration != -1) {
			return endDefaultValueDeclaration;
		}
		return declarationSourceEnd;
	}


}
