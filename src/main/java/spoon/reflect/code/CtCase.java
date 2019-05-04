/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.EXPRESSION;

/**
 * This code element defines a case within a switch-case.
 *
 * Example: <pre>
 * int x = 0;
 * switch(x) {
 *     case 1: // &lt;-- case statement
 *       System.out.println("foo");
 * }</pre>
 *
 * @param <S>
 * 		This type must be assignable from the switch type
 * @see spoon.reflect.code.CtSwitch
 */
public interface CtCase<S> extends CtStatement, CtStatementList {
	/**
	 * Gets the case expression.
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<S> getCaseExpression();

	/**
	 * Sets the case expression.
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtCase<S>> T setCaseExpression(CtExpression<S> caseExpression);

	@Override
	CtCase<S> clone();
}
