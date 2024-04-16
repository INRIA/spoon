/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import org.jspecify.annotations.Nullable;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.path.CtRole;

import java.util.List;

import static spoon.reflect.path.CtRole.CASE_KIND;
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
	 * Use {@link #getCaseExpressions()} since Java 12
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<S> getCaseExpression();

	/**
	 * Sets the case expression. If set with null, the CtCase will represent a default label.
	 * Use {@link #setCaseExpressions(List)} since Java 12
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtCase<S>> T setCaseExpression(CtExpression<S> caseExpression);

	/**
	 * Gets the case expressions.
	 * (Multiple case expressions are available as a preview feature since Java 12)
	 */
	@PropertyGetter(role = EXPRESSION)
	List<CtExpression<S>> getCaseExpressions();

	/**
	 * Sets the case expressions. If set with null or an empty list, the CtCase will represent a default label.
	 * (Multiple case expressions are available as a preview feature since Java 12)
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtCase<S>> T setCaseExpressions(List<CtExpression<S>> caseExpressions);

	/**
	 * Adds case expression.
	 * (Multiple case expressions are available as a preview feature since Java 12)
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtCase<S>> T addCaseExpression(CtExpression<S> caseExpression);

	/**
	 * Gets the kind of this case - colon (:) or arrow (-&gt;)
	 * (Arrow syntax is available as a preview feature since Java 12)
	 */
	@PropertyGetter(role = CASE_KIND)
	CaseKind getCaseKind();

	/**
	 * Sets the kind of this case - colon (:) or arrow (-&gt;)
	 * (Arrow syntax is available as a preview feature since Java 12)
	 */
	@PropertySetter(role = CASE_KIND)
	<T extends CtCase<S>> T setCaseKind(CaseKind kind);

	/**
	 * {@return whether this case includes a trailing {@code default}}
	 */
	@PropertyGetter(role = CtRole.DEFAULT_EXPRESSION)
	boolean getIncludesDefault();

	/**
	 * Sets whether this case includes a trailing {@code default}.
	 *
	 * @param includesDefault whether this case includes a {@code default}.
	 * @return this case.
	 */
	@PropertySetter(role = CtRole.DEFAULT_EXPRESSION)
	CtCase<S> setIncludesDefault(boolean includesDefault);

	/**
	 * {@return the guard of this case}
	 * This method returns {@code null} if no guard is present.
	 */
	@PropertyGetter(role = CtRole.CONDITION)
	@Nullable
	CtExpression<?> getGuard();

	/**
	 * Sets the guarding expression for this case.
	 *
	 * @param guard the expression guarding this case. If {@code null}, no guard will be inserted in the code.
	 * @return this case.
	 */
	@PropertySetter(role = CtRole.CONDITION)
	CtCase<S> setGuard(@Nullable CtExpression<?> guard);

	@Override
	CtCase<S> clone();
}
