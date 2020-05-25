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

import static spoon.reflect.path.CtRole.TARGET;


/**
 * This abstract code element defines an expression which contains a target
 * expression. In Java, it is generally of the form:
 * <code>targetExpr.targetedExpr</code>.
 *
 * @param <T>
 * 		"Return" type of this expression
 * @param <E>
 * 		Type of the target
 */
public interface CtTargetedExpression<T, E extends CtExpression<?>> extends CtExpression<T> {
	/**
	 * Gets the target expression. The target is a `CtTypeAccess` for static methods and a sub type of `CtExpression` for everything else.
	 */
	@PropertyGetter(role = TARGET)
	E getTarget();

	/**
	 * Sets the target expression.
	 */
	@PropertySetter(role = TARGET)
	<C extends CtTargetedExpression<T, E>> C setTarget(E target);

	@Override
	CtTargetedExpression<T, E> clone();
}
