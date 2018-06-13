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
