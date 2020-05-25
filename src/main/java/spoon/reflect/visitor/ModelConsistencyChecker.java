/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import org.apache.logging.log4j.Level;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This scanner checks that a program model is consistent with regards to the
 * parent elements (children must have the right parent). This class can be used
 * to validate that a program transformation does not harm the model integrity,
 * and also to automatically fix it when possible.
 */
public class ModelConsistencyChecker extends CtScanner {

	boolean fixInconsistencies;
	boolean fixNullParents;

	Environment environment;

	Deque<CtElement> stack = new ArrayDeque<>();

	/**
	 * Creates a new model consistency checker.
	 *
	 * @param environment
	 * 		the environment where to report errors
	 * @param fixInconsistencies
	 * 		automatically fix the inconsistencies rather than reporting
	 * 		warnings (to report warnings, set this to false)
	 * @param fixNullParents
	 * 		automatically fix the null parents rather than reporting
	 * 		warnings (to report warnings, set this to false)
	 */
	public ModelConsistencyChecker(Environment environment, boolean fixInconsistencies, boolean fixNullParents) {
		this.environment = environment;
		this.fixInconsistencies = fixInconsistencies;
		this.fixNullParents = fixNullParents;
	}

	/**
	 * Enters an element.
	 */
	@Override
	public void enter(CtElement element) {
		if (!stack.isEmpty()) {
			if (!element.isParentInitialized() || element.getParent() != stack.peek()) {
				if ((!element.isParentInitialized() && fixNullParents) || (element.getParent() != stack.peek() && fixInconsistencies)) {
					element.setParent(stack.peek());
				} else {
					final String name = element instanceof CtNamedElement ? " - " + ((CtNamedElement) element).getSimpleName() : "";
					environment.report(null, Level.WARN,
							(element.isParentInitialized() ? "inconsistent" : "null") + " parent for " + element.getClass() + name + " - " + element.getPosition() + " - " + stack.peek()
									.getPosition());
					dumpStack();
				}
			}
		}
		stack.push(element);
	}

	/**
	 * Exits an element.
	 */
	@Override
	protected void exit(CtElement e) {
		stack.pop();
	}

	private void dumpStack() {
		environment.debugMessage("model consistency checker stack:");
		for (CtElement e : stack) {
			environment.debugMessage("    " + e.getClass().getSimpleName() + " " + (e.getPosition().isValidPosition() ? String.valueOf(e.getPosition()) : "(?)"));
		}
	}

}
