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
import spoon.reflect.reference.CtTypeReference;
import spoon.support.UnsettableProperty;

import java.util.List;

import static spoon.reflect.path.CtRole.VARIABLE;

/**
 * This code element defines a type pattern, introduced in Java 16
 * by <a href=https://openjdk.java.net/jeps/394>JEP 394</a>.
 * <p>
 * Example:
 * <pre>
 *     Object obj = null;
 *     boolean longerThanTwo = false;
 *     // String s is the type pattern, declaring a local variable
 *     if (obj instanceof String s) {
 *         longerThanTwo = s.length() > 2;
 *     }
 * </pre>
 */
public interface CtTypePattern extends CtPattern, CtExpression<Void> {

	/**
	 * Returns the local variable declared by this type pattern.
	 *
	 * @return the local variable.
	 */
	@PropertyGetter(role = VARIABLE)
	CtLocalVariable<?> getVariable();

	/**
	 * Sets the local variable for this type pattern.
	 *
	 * @param variable the variable to set for this type pattern.
	 * @return this pattern.
	 */
	@PropertySetter(role = VARIABLE)
	CtTypePattern setVariable(CtLocalVariable<?> variable);

	@Override
	CtTypePattern clone();

	@Override
	@UnsettableProperty
	List<CtTypeReference<?>> getTypeCasts();

	@Override
	@UnsettableProperty
	<C extends CtExpression<Void>> C setTypeCasts(List<CtTypeReference<?>> types);

	@Override
	@UnsettableProperty
	<C extends CtExpression<Void>> C addTypeCast(CtTypeReference<?> type);
}
