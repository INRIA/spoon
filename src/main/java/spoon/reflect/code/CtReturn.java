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
import spoon.template.TemplateParameter;

import static spoon.reflect.path.CtRole.EXPRESSION;

/**
 * This code element represents a <code>return</code> statement.
 *
 * Example:
 * <pre>
 *    Runnable r = new Runnable() {
 *     	&#64;Override
 *     	public void run() {
 *     	  return; // &lt;-- CtReturn statement
 *     	}
 *    };
 * </pre>

 */
public interface CtReturn<R> extends CtCFlowBreak, TemplateParameter<Void> {

	/**
	 * Gets the returned expression.
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<R> getReturnedExpression();

	/**
	 * Sets the returned expression.
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtReturn<R>> T setReturnedExpression(CtExpression<R> returnedExpression);

	@Override
	CtReturn<R> clone();
}
