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

import spoon.processing.Environment;
import spoon.processing.Severity;
import spoon.reflect.declaration.CtElement;

/**
 * This scanner checks that a program model is consistent whith regards to the
 * parent elements (children must have the right parent). This class can be used
 * to validate that a program transformation does not harm the model integrity,
 * and also to automatically fix it when possible.
 */
public class ModelConsistencyChecker extends CtScanner {

	boolean fixInconsistencies = false;

	Environment environment;

	Stack<CtElement> stack = new Stack<CtElement>();

	/**
	 * Creates a new model consistency checker.
	 * 
	 * @param environment
	 *            the environment where to report errors
	 * @param fixInconsistencies
	 *            automatically fix the inconsitencies rather than reporting
	 *            warnings (to report warnings, set this to false)
	 */
	public ModelConsistencyChecker(Environment environment,
			boolean fixInconsistencies) {
		this.fixInconsistencies = fixInconsistencies;
		this.environment = environment;
	}

	/**
	 * Enters an element.
	 */
	@Override
	public void enter(CtElement element) {
		if (!stack.isEmpty()) {
			if (element.getParent() != stack.peek()) {
				if (fixInconsistencies) {
					element.setParent(stack.peek());
				} else {
					environment.report(null, Severity.WARNING,
							"inconsistent parent for " + element);
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

}
