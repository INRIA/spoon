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
import static spoon.reflect.path.CtRole.FOREACH_VARIABLE;


/**
 * This code element defines a foreach statement.
 * Example:
 * <pre>
 *     java.util.List l = new java.util.ArrayList();
 *     for(Object o : l) { // &lt;-- foreach loop
 *     	System.out.println(o);
 *     }
 * </pre>
 */
public interface CtForEach extends CtLoop {
	/**
	 * Gets the iterated expression (an iterable of an array).
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<?> getExpression();

	/**
	 * Gets the variable that references the currently iterated element.
	 */
	@PropertyGetter(role = FOREACH_VARIABLE)
	CtLocalVariable<?> getVariable();

	/**
	 * Sets the iterated expression (an iterable of an array).
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtForEach> T setExpression(CtExpression<?> expression);

	/**
	 * Sets the variable that references the currently iterated element.
	 */
	@PropertySetter(role = FOREACH_VARIABLE)
	<T extends CtForEach> T setVariable(CtLocalVariable<?> variable);

	@Override
	CtForEach clone();
}
