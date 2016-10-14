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
package spoon.support.compiler.jdt;

import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;

import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

import java.util.ArrayList;
import java.util.List;

class CompilationUnitWrapper extends CompilationUnit {

	private CtType type;

	CompilationUnitWrapper(CtType type) {
		// char[] contents, String fileName, String encoding, String destinationPath, boolean ignoreOptionalProblems
		super(null,
				type.getSimpleName() + ".java",
				"UTF-8",
				type.getFactory().getEnvironment().getBinaryOutputDirectory(),
				false);
		this.type = type;
	}

	@Override
	public char[] getContents() {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(type.getFactory().getEnvironment());
		List<CtType<?>> types = new ArrayList<>();
		types.add(type);
		printer.calculate(type.getPosition().getCompilationUnit(), types);

		String result = printer.getResult();
		char[] content = result.toCharArray();
		return content;
	}


}
