/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler.jdt;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import spoon.reflect.declaration.CtElement;

public class ASTPair {
	public final CtElement element;

	public final ASTNode node;

	public ASTPair(CtElement element, ASTNode node) {
		this.element = element;
		this.node = node;
	}

	@Override
	public String toString() {
		return element.getClass().getSimpleName() + "-" + node.getClass().getSimpleName();
	}
}
