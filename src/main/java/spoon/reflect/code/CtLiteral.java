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

import static spoon.reflect.path.CtRole.LITERAL_BASE;
import static spoon.reflect.path.CtRole.VALUE;

/**
 * This code element defines a literal value (an int, a string, etc).
 *
 * <pre>
 *     int x = 4; // 4 is a literal
 * </pre>
 * A null literal, as in s = null", is represented by a CtLiteral whose value is null.
 *
 * @param <T>
 * 		type of literal's value
 */
public interface CtLiteral<T> extends CtExpression<T> {

	/**
	 * Gets the actual value of the literal (statically known).
	 */
	@PropertyGetter(role = VALUE)
	T getValue();

	/**
	 * Sets the actual value of the literal.
	 */
	@PropertySetter(role = VALUE)
	<C extends CtLiteral<T>> C setValue(T value);

	/**
	 * Gets the base ot the numeric literal (2, 8, 10 or 16).
	 */
	@PropertyGetter(role = LITERAL_BASE)
	LiteralBase getBase();

	/**
	 * Sets the base ot the numeric literal.
	 */
	@PropertySetter(role = LITERAL_BASE)
	<C extends CtLiteral<T>> C setBase(LiteralBase base);

	/** Overriding return type, a clone of a CtLiteral returns a CtLiteral */
	@Override
	CtLiteral<T> clone();
}
