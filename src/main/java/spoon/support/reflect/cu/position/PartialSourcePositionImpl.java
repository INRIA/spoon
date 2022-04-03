/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.cu.position;

import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtCompilationUnit;

import java.io.File;

/**
 * This class intends to create a source position containing only a compilation unit.
 */
public class PartialSourcePositionImpl extends NoSourcePosition {

	private static final long serialVersionUID = 1L;

	private CtCompilationUnit compilationUnit;

	public PartialSourcePositionImpl(CtCompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	@Override
	public File getFile() {
		return compilationUnit.getFile();
	}

	@Override
	public CtCompilationUnit getCompilationUnit() {
		return compilationUnit;
	}
}
