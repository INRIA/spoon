/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.eval;

import spoon.reflect.declaration.CtElement;

/**
 * This interface defines a simple partial evaluator on the Spoon Java model. It
 * recursively transforms a meta-model element by partially evaluating it and
 * simplifies it when possible (i.e. when constant values are involved).
 */
public interface PartialEvaluator {

	/**
	 * Partially evaluates an element and all its sub-elements.
	 *
	 * @param <R>
	 * 		the partially evaluated element type
	 * @param element
	 * 		the element to be partially evaluated
	 * @return the result of the partial evaluation
	 */
	<R extends CtElement> R evaluate(R element);
}
