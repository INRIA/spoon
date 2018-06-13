/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.EXPRESSION;

/**
 * This code element defines a one-dimensional array access. When
 * multi-dimensional, array accesses are applied to other one-dimensional array
 * accesses. The target of {@link spoon.reflect.code.CtTargetedExpression}
 * defines the expression that represents the accessed array.
 *
 * @param <T>
 * 		"Return" type of this access (not a array type)
 * @param <E>
 * 		Type of the target expression
 */
public interface CtArrayAccess<T, E extends CtExpression<?>> extends CtTargetedExpression<T, E> {
	/**
	 * Sets the expression that defines the index.
	 */
	@PropertySetter(role = EXPRESSION)
	<C extends CtArrayAccess<T, E>> C setIndexExpression(CtExpression<Integer> expression);

	/**
	 * Returns the expression that defines the index.
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<Integer> getIndexExpression();

	@Override
	CtArrayAccess<T, E> clone();
}
