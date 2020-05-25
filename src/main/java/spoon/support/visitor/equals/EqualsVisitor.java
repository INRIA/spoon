/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
		return new EqualsVisitor().checkEquals(element, other);
	}

	protected final EqualsChecker checker;

	private CtRole lastRole = null;

	public EqualsVisitor() {
		this(new EqualsChecker());
	}

	public EqualsVisitor(EqualsChecker checker) {
		this.checker = checker;
	}

	@Override
	protected void enter(CtElement e) {
		super.enter(e);
		CtElement other = stack.peek();
		checker.setOther(other);
		try {
			checker.scan(e);
		} catch (NotEqualException ex) {
			fail(checker.getNotEqualRole() == null ? lastRole : checker.getNotEqualRole(), e, other);
		}
	}
	protected boolean isNotEqual = false;
	protected CtRole notEqualRole;
	protected Object notEqualElement;
	protected Object notEqualOther;

	@Override
	protected void biScan(CtRole role, Collection<? extends CtElement> elements, Collection<? extends CtElement> others) {

		if (isNotEqual) {
			return;
		}
		if (elements == null) {
			if (others != null) {
				fail(role, elements, others);
			}
			return;
		} else if (others == null) {
			fail(role, elements, others);
			return;
		}
		if ((elements.size()) != (others.size())) {
			fail(role, elements, others);
			return;
		}
		super.biScan(role, elements, others);
	}

	@Override
	public void biScan(CtElement element, CtElement other) {
		biScan(null, element, other);
	}

	@Override
	public void biScan(CtRole role, CtElement element, CtElement other) {
		if (isNotEqual) {
			return;
		}
		if (element == null) {
			if (other != null) {
				fail(role, element, other);
				return;
			}
			return;
		} else if (other == null) {
			fail(role, element, other);
			return;
		}
		if (element == other) {
			return;
		}

		try {
			lastRole = role;
			super.biScan(element, other);
		} catch (java.lang.ClassCastException e) {
			fail(role, element, other);
		} finally {
			lastRole = null;
		}
	}

	protected boolean fail(CtRole role, Object element, Object other) {
		isNotEqual = true;
		notEqualRole = role;
		notEqualElement = element;
		notEqualOther = other;
		return true;
	}

	/**
	 * @param element first to be compared element
	 * @param other second to be compared element
	 * @return true if `element` and `other` are equal. If false then see
	 * {@link #getNotEqualElement()}, {@link #getNotEqualOther()} and {@link #getNotEqualRole()} for details
	 */
	public boolean checkEquals(CtElement element, CtElement other) {
		biScan(element, other);
		return !isNotEqual;
	}

	/**
	 * @return true if {@link #checkEquals(CtElement, CtElement)} are equal. If false then see
	 * {@link #getNotEqualElement()}, {@link #getNotEqualOther()} and {@link #getNotEqualRole()} for details
	 */
	public boolean isEqual() {
		return !isNotEqual;
	}

	/**
	 * @return role on which the element and other element were not equal
	 */
	public CtRole getNotEqualRole() {
		return notEqualRole;
	}

	/**
	 * @return element or collection which was not equal
	 */
	public Object getNotEqualElement() {
		return notEqualElement;
	}

	/**
	 * @return other element or collection which was not equal
	 */
	public Object getNotEqualOther() {
		return notEqualOther;
	}
}
