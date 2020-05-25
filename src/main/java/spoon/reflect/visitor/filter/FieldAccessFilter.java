/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.reference.CtFieldReference;

/**
 * This simple filter matches all the accesses to a given field.
 */
public class FieldAccessFilter extends VariableAccessFilter<CtFieldAccess<?>> {

	/**
	 * Creates a new field access filter.
	 *
	 * @param field
	 * 		the accessed field
	 */
	public FieldAccessFilter(CtFieldReference<?> field) {
		super(field);
	}

}
