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
package spoon.support.reflect.cu.position;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.position.BodyHolderSourcePosition;

import java.io.Serializable;

/**
 * This class represents the position of a Java program element in a source
 * file.
 */
public class BodyHolderSourcePositionImpl extends DeclarationSourcePositionImpl
		implements BodyHolderSourcePosition, Serializable {

	private static final long serialVersionUID = 1L;
	private int bodyStart;
	private int bodyEnd;

	public BodyHolderSourcePositionImpl(
			CompilationUnit compilationUnit,
			int sourceStart, int sourceEnd,
			int modifierSourceStart, int modifierSourceEnd,
			int declarationSourceStart, int declarationSourceEnd,
			int bodyStart,
			int bodyEnd,
			int[] lineSeparatorPositions) {
		super(compilationUnit,
				sourceStart, sourceEnd,
				modifierSourceStart, modifierSourceEnd,
				declarationSourceStart, declarationSourceEnd,
				lineSeparatorPositions);
		this.bodyStart = bodyStart;
		this.bodyEnd = bodyEnd;
	}

	@Override
	public int getBodyStart() {
		return bodyStart;
	}

	@Override
	public int getBodyEnd() {
		return bodyEnd;
	}
}
