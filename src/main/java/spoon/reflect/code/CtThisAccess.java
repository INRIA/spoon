/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

/**
 * This code element defines an access to this.
 *
 * Example:
 * <pre>
 *     class Foo {
 *     int value = 42;
 *     int foo() {
 *       return this.value; // &lt;-- access to this
 *     }
 *     };

 * </pre>
 * @param <T>
 * 		Type of this
 */
public interface CtThisAccess<T> extends CtTargetedExpression<T, CtExpression<?>> {
	@Override
	CtThisAccess<T> clone();
}
