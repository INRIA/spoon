/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

/**
 * This code element defines a write access to an array.
 *
 * In Java, it is a usage of an array inside an assignment.
 *
 * For example:
 * <pre>
 *     Object[] array = new Object[10];
 *     // array write
 *     array[0] = "new value";
 * </pre>
 *
 *
 * @param <T>
 * 		type of the array
 */
public interface CtArrayWrite<T> extends CtArrayAccess<T, CtExpression<?>> {
	@Override
	CtArrayWrite<T> clone();
}
