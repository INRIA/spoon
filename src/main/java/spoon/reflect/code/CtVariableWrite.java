/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

/**
 * This code element defines a write to a variable.
 * <p>
 * A variable-write is an assignment to a variable.
 * <p>
 * For example:
 * <pre>
 *     String variable = "";
 *     variable = "new value"; // variable write
 * </pre>
 * <pre>
 *     String variable = "";
 *     variable += "";
 * </pre>
 *
 *
 * @param <T> type of the variable
 * @see CtAssignment#getAssigned()
 * @see CtArrayWrite
 */
public interface CtVariableWrite<T> extends CtVariableAccess<T> {
	@Override
	CtVariableWrite<T> clone();
}
