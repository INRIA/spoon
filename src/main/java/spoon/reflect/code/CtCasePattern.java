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

import static spoon.reflect.path.CtRole.PATTERN;

/**
 * This code element represents a case pattern, introduced in Java 21
 * by <a href=https://openjdk.java.net/jeps/441>JEP 441</a>.
 * <p>
 * Example:
 * <pre>
 * Number num = Math.random() < 0.5 ? Integer.valueOf(1) : Double.valueOf(10.5);
 * switch (num) {
 *    case Integer i -> System.out.println("int: " + i);
 *    case Double d when d > 0.5 -> System.out.println("double: " + d);
 *    case null, default -> System.out.println("other");
 * }
 * </pre>
 */
public interface CtCasePattern extends CtExpression<Void> {

	/**
	 * {@return the pattern of of this case pattern}
	 */
	@PropertyGetter(role = PATTERN)
	CtPattern getPattern();

	/**
	 * Sets the pattern for this case pattern.
	 *
	 * @param pattern the new pattern for this case pattern.
	 * @return this case pattern.
	 */
	@PropertySetter(role = PATTERN)
	CtCasePattern setPattern(CtPattern pattern);

	@Override
	CtCasePattern clone();
}
