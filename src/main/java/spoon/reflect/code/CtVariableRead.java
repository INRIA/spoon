/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

/**
 * This code element defines an read access to a variable.
 *
 * In Java, it is a usage of a variable outside an assignment. For example,
 * <pre>
 *     String variable = "";
 *     System.out.println(
 *       variable // &lt;-- a variable read
 *     );
 * </pre>
 *
 * @param <T>
 * 		type of the variable
 */
public interface CtVariableRead<T> extends CtVariableAccess<T> {
	@Override
	CtVariableRead<T> clone();
}
