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
 * This code element defines a <code>while</code> loop.
 *
 * Example:
 * <pre>
 *     int x = 0;
 *     while (x!=10) {
 *         x=x+1;
 *     };
 * </pre>
 *
 */
public interface CtWhile extends CtLoop {
	/**
	 * Gets the looping boolean test expression.
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<Boolean> getLoopingExpression();

	/**
	 * Sets the looping boolean test expression.
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtWhile> T setLoopingExpression(CtExpression<Boolean> expression);

	@Override
	CtWhile clone();
}
