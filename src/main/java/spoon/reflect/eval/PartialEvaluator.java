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
package spoon.reflect.eval;

import spoon.reflect.code.CtCodeElement;
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
	 * @param parent
	 * 		the parent element of the partially evaluated element
	 * @param element
	 * 		the element to be partially evaluated
	 * @return the result of the partial evaluation
	 */
	<R extends CtCodeElement> R evaluate(CtElement parent, R element);
}
