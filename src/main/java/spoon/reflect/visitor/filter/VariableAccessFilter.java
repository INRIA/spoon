/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.Filter;

/**
 * This simple filter matches all the accesses to a given field.
 */
public class VariableAccessFilter<T extends CtVariableAccess<?>> implements Filter<T> {
	CtVariableReference<?> variable;

	/**
	 * Creates a new field access filter.
	 *
	 * @param variable
	 * 		the accessed variable
	 */
	public VariableAccessFilter(CtVariableReference<?> variable) {
		if (variable == null) {
			throw new IllegalArgumentException("The parameter variable cannot be null.");
		}
		this.variable = variable;
	}

	@Override
	public boolean matches(T variableAccess) {
		return variable.equals(variableAccess.getVariable());
	}

}
