/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
import spoon.reflect.cu.position.NoSourcePosition;

import java.io.File;

/**
 * This class intends to create a source position containing only a compilation unit.
 */
public class PartialSourcePositionImpl extends NoSourcePosition {

	private static final long serialVersionUID = 1L;

	private CompilationUnit compilationUnit;

	public PartialSourcePositionImpl(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	@Override
	public File getFile() {
		return compilationUnit.getFile();
	}

	@Override
	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}
}
