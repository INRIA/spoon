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
import static spoon.reflect.path.CtRole.OPERATOR_KIND;


/**
 * This code element represents a unary operator.
 * For example :
 * <pre>
 *     int x=3;
 *     --x; // &lt;-- unary --
 * </pre>
 *
 * @param <T>
 * 		"Return" type of this expression
 */
public interface CtUnaryOperator<T> extends CtExpression<T>, CtStatement {

	/**
	 * Gets the expression to which the operator is applied.
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<T> getOperand();

	/**
	 * Sets the expression to which the operator is applied.
	 */
	@PropertySetter(role = EXPRESSION)
	<C extends CtUnaryOperator> C setOperand(CtExpression<T> expression);

	/**
	 * Sets the kind of this operator.
	 */
	@PropertySetter(role = OPERATOR_KIND)
	<C extends CtUnaryOperator> C setKind(UnaryOperatorKind kind);

	/**
	 * Gets the kind of this operator.
	 */
	@PropertyGetter(role = OPERATOR_KIND)
	UnaryOperatorKind getKind();

	@Override
	CtUnaryOperator<T> clone();
}
