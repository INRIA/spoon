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
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

/**
 * This abstract bi scanner class declares all scan methods useful for CtBiScannerDefault
 */
public abstract class CtAbstractBiScanner implements CtVisitor {
	protected Deque<CtElement> stack = new ArrayDeque<>();

	protected void enter(CtElement e) {
	}

	protected void exit(CtElement e) {
	}

	protected boolean isNotEqual = false;

	public boolean biScan(Collection<? extends CtElement> elements, Collection<? extends CtElement> others) {
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
		for (Iterator<? extends CtElement> firstIt = elements.iterator(), secondIt = others.iterator(); (firstIt.hasNext()) && (secondIt.hasNext());) {
			biScan(firstIt.next(), secondIt.next());
		}
		return isNotEqual;
	}

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

	public boolean fail() {
		isNotEqual = true;
		return true;
	}
}
