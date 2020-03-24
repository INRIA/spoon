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
import spoon.reflect.declaration.CtElement;

import java.util.List;

import static spoon.reflect.path.CtRole.CASE;
import static spoon.reflect.path.CtRole.EXPRESSION;

/**
 * This code element defines an abstract switch
 * (either switch statement or switch expression).
 *
 * @param <S>
 * 		the type of the selector expression
 */
public interface CtAbstractSwitch<S> extends CtElement {
	/**
	 * Gets the selector. The type of the Expression must be <code>char</code>,
	 * <code>byte</code>, <code>short</code>, <code>int</code>,
	 * <code>Character</code>, <code>Byte</code>, <code>Short</code>,
	 * <code>Integer</code>, or an <code>enum</code> type
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<S> getSelector();

	/**
	 * Sets the selector. The type of the Expression must be <code>char</code>,
	 * <code>byte</code>, <code>short</code>, <code>int</code>,
	 * <code>Character</code>, <code>Byte</code>, <code>Short</code>,
	 * <code>Integer</code>, or an <code>enum</code> type
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtAbstractSwitch<S>> T setSelector(CtExpression<S> selector);

	/**
	 * Gets the list of cases defined for this switch.
	 */
	@PropertyGetter(role = CASE)
	List<CtCase<? super S>> getCases();

	/**
	 * Sets the list of cases defined for this switch.
	 */
	@PropertySetter(role = CASE)
	<T extends CtAbstractSwitch<S>> T setCases(List<CtCase<? super S>> cases);

	/**
	 * Adds a case;
	 */
	@PropertySetter(role = CASE)
	<T extends CtAbstractSwitch<S>> T addCase(CtCase<? super S> c);

	/**
	 * Removes a case;
	 */
	@PropertySetter(role = CASE)
	boolean removeCase(CtCase<? super S> c);
}
