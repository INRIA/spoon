/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

/**
 * This code element defines conditional expressions using the ? (ternary expressions).
 *
 * Example:
 * <pre>
 *     System.out.println(
 *        1==0 ? "foo" : "bar" // <-- ternary conditional
 *     );
 * </pre>
 */
public interface CtConditional<T> extends CtExpression<T> {

	/**
	 * Gets the "false" expression.
	 */
	CtExpression<T> getElseExpression();

	/**
	 * Gets the "true" expression.
	 */
	CtExpression<T> getThenExpression();

	/**
	 * Gets the condition expression.
	 */
	CtExpression<Boolean> getCondition();

	/**
	 * Sets the "false" expression.
	 */
	<C extends CtConditional<T>> C setElseExpression(CtExpression<T> elseExpression);

	/**
	 * Sets the "true" expression.
	 */
	<C extends CtConditional<T>> C setThenExpression(CtExpression<T> thenExpression);

	/**
	 * Sets the condition expression.
	 */
	<C extends CtConditional<T>> C setCondition(CtExpression<Boolean> condition);

	@Override
	CtConditional<T> clone();
}
