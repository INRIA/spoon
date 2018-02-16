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
		return !new EqualsVisitor().biScan(element, other);
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
	protected boolean biScan(CtRole role, Collection<? extends CtElement> elements, Collection<? extends CtElement> others) {
		if (isNotEqual) {
			return isNotEqual;
		}
		if (elements == null) {
			if (others != null) {
				return fail();
			}
			return isNotEqual;
		} else if (others == null) {
			return fail();
		}
		if ((elements.size()) != (others.size())) {
			return fail();
		}
		super.biScan(role, elements, others);
		return isNotEqual;
	}

	@Override
	public boolean biScan(CtElement element, CtElement other) {
		if (isNotEqual) {
			return isNotEqual;
		}
		if (element == null) {
			if (other != null) {
				return fail();
			}
			return isNotEqual;
		} else if (other == null) {
			return fail();
		}
		if (element == other) {
			return isNotEqual;
		}

		stack.push(other);
		try {
			element.accept(this);
		} catch (java.lang.ClassCastException e) {
			return fail();
		} finally {
			stack.pop();
		}
		return isNotEqual;
	}

	private boolean fail() {
		isNotEqual = true;
		return true;
	}

}

