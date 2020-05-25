/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.comparator;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

import java.util.Comparator;

public class FixedOrderBasedOnFileNameCompilationUnitComparator implements Comparator<CompilationUnitDeclaration> {
	@Override
	public int compare(CompilationUnitDeclaration o1, CompilationUnitDeclaration o2) {
		String s1 = new String(o1.getFileName());
		String s2 = new String(o2.getFileName());
		return s1.compareTo(s2);
	}
}
