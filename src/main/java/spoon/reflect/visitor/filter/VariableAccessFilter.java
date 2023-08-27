/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.Filter;

/**
 * This simple filter matches all the accesses to a given variable.
 */
public class VariableAccessFilter<T extends CtVariableAccess<?>> implements Filter<T> {
	private final CtVariableReference<?> variable;
	private CtVariable<?> variableDeclaration;

	/**
	 * Creates a new variable access filter.
	 *
	 * @param variable the variable to find accesses for, must not be {@code null}
	 */
	public VariableAccessFilter(CtVariableReference<?> variable) {
		if (variable == null) {
			throw new IllegalArgumentException("The parameter variable cannot be null.");
		}
		this.variable = variable;
	}

	@Override
	public boolean matches(T variableAccess) {
		if (this.variable.equals(variableAccess.getVariable())) {
			return true;
		}
		// If this.variable is a reference to a generic field, then the references might not be equal:
		//
		// Given `class A<T> { T t; }`, the reference would be to `T t`, but an access to `t` could look
		// like this:
		// `A<String> a = new A<>(); a.t = "foo";`
		//                           ^^^ reference to `String t`
		// As (String t) != (T t), the references are not equal, even though the same variable is accessed.
		// Therefore, we fall back to comparing the declaration if the references were different.
		if (this.variableDeclaration == null) {
			this.variableDeclaration = this.variable.getDeclaration();
		}

		return this.variableDeclaration.equals(variableAccess.getVariable().getDeclaration());
	}

}
