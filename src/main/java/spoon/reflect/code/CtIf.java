/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.template.TemplateParameter;

import static spoon.reflect.path.CtRole.CONDITION;
import static spoon.reflect.path.CtRole.ELSE;
import static spoon.reflect.path.CtRole.THEN;

/**
 * This code element represents an {@code if} statement.
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
	 * Gets the boolean expression that represents the {@code if}'s
	 * condition.
	 */
	@PropertyGetter(role = CONDITION)
	CtExpression<Boolean> getCondition();

	/**
	 * Gets the statement executed when the condition is false.
	 * <p>
	 * An {@code else if} like
	 * <pre>
	 *     if (a) {
	 *         doA();
	 *     } else if (b) {
	 *         doB();
	 *     } else {
	 *         doC();
	 *     }
	 * </pre>
	 * will be represented as
	 * <pre>
	 *     if (a) {
	 *         doA();
	 *     } else {
	 *         if (b) {
	 *             doB();
	 *         } else {
	 *             doC();
	 *         }
	 *     }
	 * </pre>
	 * To differentiate between an {@code else} Block with an {@code if} and an {@code else if},
	 * {@link CtBlock#isImplicit()} is set to {@code true}.
	 *
	 * @return the statement of the {@code else} or {@code null} if no else is specified.
	 */
	@PropertyGetter(role = ELSE)
	<S extends CtStatement> S getElseStatement();

	/**
	 * Gets the statement executed when the condition is true.
	 * <p>
	 * This method will return {@code null} for {@code if (condition);}.
	 *
	 * @return the statement of the {@code if}, in most cases this is a {@link CtBlock}.
	 */
	@PropertyGetter(role = THEN)
	<S extends CtStatement> S getThenStatement();

	/**
	 * Sets the boolean expression that represents the {@code if}'s
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
