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

import static spoon.reflect.path.CtRole.ASSIGNED;


/**
 * This code element defines an assignment.
 *
 * Example:
 * <pre>
 *     int x;
 *     x = 4; // &lt;-- an assignment
 * </pre>
 * @param <T>
 * 		type of assigned expression
 * @param <A>
 * 		type of expression to assign, it should extends &lt;T&gt;
 */
public interface CtAssignment<T, A extends T> extends CtStatement, CtExpression<T>, CtRHSReceiver<A> {
	/**
	 * Returns the assigned expression on the left-hand side (where the value is stored,
	 * e.g. in a variable, in an array, in a field ...).
	 */
	@PropertyGetter(role = ASSIGNED)
	CtExpression<T> getAssigned();

	/**
	 * Sets the assigned expression (left hand side - LHS).
	 */
	@PropertySetter(role = ASSIGNED)
	<C extends CtAssignment<T, A>> C setAssigned(CtExpression<T> assigned);

	@Override
	CtAssignment<T, A> clone();
}
