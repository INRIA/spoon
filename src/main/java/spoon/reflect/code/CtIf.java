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

import static spoon.reflect.path.CtRole.CONDITION;
import static spoon.reflect.path.CtRole.ELSE;
import static spoon.reflect.path.CtRole.THEN;

/**
 * This code element represents an <code>if</code> statement.
 * Example:
 * <pre>
 *     if (1==0) {
 *     	System.out.println("foo");
 *     } else {
 *     	System.out.println("bar");
 *     }
 * </pre>
 */
public interface CtIf extends CtStatement, TemplateParameter<Void> {

	/**
	 * Gets the boolean expression that represents the <code>if</code>'s
	 * condition.
	 */
	@PropertyGetter(role = CONDITION)
	CtExpression<Boolean> getCondition();

	/**
	 * Gets the statement executed when the condition is false.
	 */
	@PropertyGetter(role = ELSE)
	<S extends CtStatement> S getElseStatement();

	/**
	 * Gets the statement executed when the condition is true.
	 */
	@PropertyGetter(role = THEN)
	<S extends CtStatement> S getThenStatement();

	/**
	 * Sets the boolean expression that represents the <code>if</code>'s
	 * condition.
	 */
	@PropertySetter(role = CONDITION)
	<T extends CtIf> T setCondition(CtExpression<Boolean> expression);

	/**
	 * Sets the statement executed when the condition is false.
	 */
	@PropertySetter(role = ELSE)
	<T extends CtIf> T setElseStatement(CtStatement elseStatement);

	/**
	 * Sets the statement executed when the condition is true.
	 */
	@PropertySetter(role = THEN)
	<T extends CtIf> T setThenStatement(CtStatement thenStatement);

	@Override
	CtIf clone();
}
