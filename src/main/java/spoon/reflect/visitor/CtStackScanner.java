/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

import java.util.Stack;

/**
 * This class defines a scanner that maintains a scanning stack for contextual
 * awareness.
 */
public class CtStackScanner extends CtScanner {

	/**
	 * Default constructor.
	 */
	public CtStackScanner() {
	}

	/**
	 * The stack of elements.
	 */
	protected Stack<CtElement> elementStack = new Stack<CtElement>();

	/**
	 * Pops the element.
	 */
	protected void exit(CtElement e) {
		CtElement ret = elementStack.pop();
		if (ret != e) {
			throw new RuntimeException("Unconsitant Stack");
		}
		super.exit(e);
	}

	/**
	 * Pushes the element.
	 */
	protected void enter(CtElement e) {
		elementStack.push(e);
		super.enter(e);
	}
}
