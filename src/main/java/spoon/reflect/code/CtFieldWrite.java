/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

/**
 * This code element defines a write access to a field.
 *
 * In Java, it is a usage of a field inside an assignment.
 *
 * For example:
 * <pre>
 *     class Foo { int field; }
 *     Foo x = new Foo();
 *     x.field = 0;
 * </pre>
 *
 *
 * @param <T>
 * 		type of the field
 */
public interface CtFieldWrite<T> extends CtFieldAccess<T>, CtVariableWrite<T> {
	@Override
	CtFieldWrite<T> clone();
}
