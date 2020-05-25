/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This class defines a scanner that maintains a scanning stack for contextual
 * awareness.
 */
public class CtDequeScanner extends CtScanner {
	/**
	 * Default constructor.
	 */
	public CtDequeScanner() {
	}

	/**
	 * The stack of elements.
	 */
	protected Deque<CtElement> elementsDeque = new ArrayDeque<>();

	/**
	 * Pops the element.
	 */
	@Override
	protected void exit(CtElement e) {
		CtElement ret = elementsDeque.pop();
		if (ret != e) {
			throw new RuntimeException("Inconsistent Stack");
		}
		super.exit(e);
	}

	/**
	 * Pushes the element.
	 */
	@Override
	protected void enter(CtElement e) {
		elementsDeque.push(e);
		super.enter(e);
	}
}
