/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
				type.getFactory().getEnvironment().getEncoding().displayName(),
				type.getFactory().getEnvironment().getBinaryOutputDirectory(),
				false, null);
		this.type = type;
	}

	@Override
	public char[] getContents() {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(type.getFactory().getEnvironment());
		List<CtType<?>> types = new ArrayList<>();
		types.add(type);
		printer.calculate(type.getPosition().getCompilationUnit(), types);

		return printer.getResult().toCharArray();
	}
}
