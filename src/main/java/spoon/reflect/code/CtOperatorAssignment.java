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

import static spoon.reflect.path.CtRole.OPERATOR_KIND;


/**
 * This code element defines an self-operated assignment such as += or *=.
 *
 * Example:
 * <pre>
 *     int x = 0;
 *     x *= 3; // &lt;-- a CtOperatorAssignment
 * </pre>
 *
 */
public interface CtOperatorAssignment<T, A extends T> extends CtAssignment<T, A> {
	/**
	 * Sets the operator kind.
	 */
	@PropertySetter(role = OPERATOR_KIND)
	<C extends CtOperatorAssignment<T, A>> C setKind(BinaryOperatorKind kind);

	/**
	 * Gets the operator kind.
	 */
	@PropertyGetter(role = OPERATOR_KIND)
	BinaryOperatorKind getKind();

	@Override
	CtOperatorAssignment<T, A> clone();
}
