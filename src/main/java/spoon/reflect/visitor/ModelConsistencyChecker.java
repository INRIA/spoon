/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import java.util.Stack;

import spoon.compiler.Environment;
import spoon.processing.Severity;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;

/**
 * This scanner checks that a program model is consistent with regards to the
 * parent elements (children must have the right parent). This class can be used
 * to validate that a program transformation does not harm the model integrity,
 * and also to automatically fix it when possible.
 */
public class ModelConsistencyChecker extends CtScanner {

	boolean fixInconsistencies = false;
	boolean fixNullParents = false;

	Environment environment;

	Stack<CtElement> stack = new Stack<CtElement>();

	/**
	 * Creates a new model consistency checker.
	 * 
	 * @param environment
	 *            the environment where to report errors
	 * @param fixInconsistencies
	 *            automatically fix the inconsistencies rather than reporting
	 *            warnings (to report warnings, set this to false)
	 * @param fixNullParents
	 *            automatically fix the null parents rather than reporting
	 *            warnings (to report warnings, set this to false)
	 */
	public ModelConsistencyChecker(Environment environment,
			boolean fixInconsistencies, boolean fixNullParents) {
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
			if (!element.isParentInitialized()
					|| element.getParent() != stack.peek()) {
				if ((!element.isParentInitialized() && fixNullParents)
						|| (element.getParent() != stack.peek() && fixInconsistencies)) {
					// System.out.println("fixing inconsistent parent: "
					// + element.getClass() + " - "
					// + element.getPosition() + " - "
					// + stack.peek().getPosition());
					element.setParent(stack.peek());
				} else {
					environment
							.report(null,
									Severity.WARNING,
									(element.isParentInitialized() ? "inconsistent"
											: "null")
											+ " parent for "
											+ element.getClass()
											+ (element instanceof CtNamedElement ? " - "
													+ ((CtNamedElement) element)
															.getSimpleName()
													: "")
											+ " - "
											+ element.getPosition()
											+ " - "
											+ stack.peek().getPosition());
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
		System.out.println("model consistency checker stack:");
		for (CtElement e : stack) {
			System.out.println("    " + e.getClass().getSimpleName() + " "
					+ (e.getPosition() == null ? "(?)" : "" + e.getPosition()));
		}
	}

}
