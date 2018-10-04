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
