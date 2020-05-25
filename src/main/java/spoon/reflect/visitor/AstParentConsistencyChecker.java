/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtReference;

public class AstParentConsistencyChecker extends CtScanner {

	private CtElement parent;
	@Override
	public void scan(CtElement element) {
		// We allow to share references across the AST
		// that is a leaf reference can be the same
		// at different places
		if (element == null || element instanceof CtReference) {
			return;
		}
		if (parent != null
				&& element.isParentInitialized() // this is the fix of #1747
				&& element.getParent() != parent) {
			throw new IllegalStateException(toDebugString(element) // better debug
					+ " is set as child of\n" + toDebugString(element.getParent())
					+ "however it is visited as a child of\n" + toDebugString(parent));
		}
		CtElement parent = this.parent;
		this.parent = element;
		super.scan(element);
		this.parent = parent;
	}

	private static String toDebugString(CtElement e) {
		return "Element: " + e + "\nSignature: " + e.getShortRepresentation() + "\nClass: " + e.getClass() + "\nposition: " + e.getPosition() + "\n";
	}
}
