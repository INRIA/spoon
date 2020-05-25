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
import static spoon.reflect.path.CtRole.EXPRESSION;


/**
 * This code element defines an assert clause.
 * Example: <pre>assert 1+1==2</pre>
 */
public interface CtAssert<T> extends CtStatement {
	/**
	 * Gets the assert expression.
	 */
	@PropertyGetter(role = CONDITION)
	CtExpression<Boolean> getAssertExpression();

	/**
	 * Sets the assert expression.
	 */
	@PropertySetter(role = CONDITION)
	<A extends CtAssert<T>> A setAssertExpression(CtExpression<Boolean> asserted);

	/**
	 * Gets the expression of the assertion if defined.
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<T> getExpression();

	/**
	 * Sets the expression of the assertion.
	 *
	 * For instance assert.setExpression(factory.createCodeSnippetExpression("param != null"))
	 */
	@PropertySetter(role = EXPRESSION)
	<A extends CtAssert<T>> A setExpression(CtExpression<T> expression);

	@Override
	CtAssert<T> clone();
}
