/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
