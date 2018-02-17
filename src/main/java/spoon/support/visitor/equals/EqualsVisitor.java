/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

package spoon.support.visitor.equals;


import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtBiScannerDefault;

import java.util.Collection;

/**
 * Used to check equality between an element and another one.
 *
 */
public class EqualsVisitor extends CtBiScannerDefault {
	public static boolean equals(CtElement element, CtElement other) {
		EqualsVisitor equalsVisitor = new EqualsVisitor();
		equalsVisitor.biScan(element, other);

		// double negation is always hard to understand, but this is legacy :-)
		return !equalsVisitor.isNotEqual;
	}

	private final EqualsChecker checker = new EqualsChecker();

	@Override
	protected void enter(CtElement e) {
		super.enter(e);
		checker.setOther(stack.peek());
		checker.scan(e);
		if (checker.isNotEqual()) {
			fail();
		}
	}
	protected boolean isNotEqual = false;

	@Override
	protected void biScan(CtRole role, Collection<? extends CtElement> elements, Collection<? extends CtElement> others) {

		if (isNotEqual) {
			return;
		}
		if (elements == null) {
			if (others != null) {
				fail();
			}
			return;
		} else if (others == null) {
			fail();
			return;
		}
		if ((elements.size()) != (others.size())) {
			fail();
			return;
		}
		super.biScan(role, elements, others);
	}

	@Override
	public void biScan(CtElement element, CtElement other) {
		if (isNotEqual) {
			return;
		}
		if (element == null) {
			if (other != null) {
				fail();
				return;
			}
			return;
		} else if (other == null) {
			fail();
			return;
		}
		if (element == other) {
			return;
		}

		try {
			super.biScan(element, other);
		} catch (java.lang.ClassCastException e) {
			fail();
		}

		return;
	}

	private boolean fail() {
		isNotEqual = true;
		return true;
	}

}

