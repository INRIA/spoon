/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.generating.scanner;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtAbstractBiScanner;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

/**
 * This visitor implements a deep-search scan on the model for 2 elements.
 *
 * Ensures that all children nodes are visited once, a visit means three method
 * calls, one call to "enter", one call to "exit" and one call to biScan.
 *
 * This class is generated automatically by the processor spoon.generating.CtBiScannerGenerator.
 *
 * Is used by EqualsVisitor.
 */
class CtBiScannerTemplate extends CtAbstractBiScanner {
	protected Deque<CtElement> stack = new ArrayDeque<>();

	protected void enter(CtElement e) {
	}

	protected void exit(CtElement e) {
	}

	public void biScan(CtElement element, CtElement other) {
		if (other == null) {
			return;
		}
		stack.push(other);
		try {
			element.accept(this);
		} finally {
			stack.pop();
		}
	}

	public void biScan(CtRole role, CtElement element, CtElement other) {
		biScan(element, other);
	}

	protected void biScan(CtRole role, Collection<? extends CtElement> elements, Collection<? extends CtElement> others) {
		for (Iterator<? extends CtElement> firstIt = elements.iterator(), secondIt = others.iterator(); (firstIt.hasNext()) && (secondIt.hasNext());) {
			biScan(role, firstIt.next(), secondIt.next());
		}
	}

}
