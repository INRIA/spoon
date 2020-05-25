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

import static spoon.reflect.path.CtRole.CONDITION;
import static spoon.reflect.path.CtRole.ELSE;
import static spoon.reflect.path.CtRole.THEN;


/**
 * This code element defines conditional expressions using the ? (ternary expressions).
 *
 * Example:
 * <pre>
 *     System.out.println(
 *        1==0 ? "foo" : "bar" // &lt;-- ternary conditional
 *     );
 * </pre>
 */
public interface CtConditional<T> extends CtExpression<T> {

	/**
	 * Gets the "false" expression.
	 */
	@PropertyGetter(role = ELSE)
	CtExpression<T> getElseExpression();

	/**
	 * Gets the "true" expression.
	 */
	@PropertyGetter(role = THEN)
	CtExpression<T> getThenExpression();

	/**
	 * Gets the condition expression.
	 */
	@PropertyGetter(role = CONDITION)
	CtExpression<Boolean> getCondition();

	/**
	 * Sets the "false" expression.
	 */
	@PropertySetter(role = ELSE)
	<C extends CtConditional<T>> C setElseExpression(CtExpression<T> elseExpression);

	/**
	 * Sets the "true" expression.
	 */
	@PropertySetter(role = THEN)
	<C extends CtConditional<T>> C setThenExpression(CtExpression<T> thenExpression);

	/**
	 * Sets the condition expression.
	 */
	@PropertySetter(role = CONDITION)
	<C extends CtConditional<T>> C setCondition(CtExpression<Boolean> condition);

	@Override
	CtConditional<T> clone();
}
