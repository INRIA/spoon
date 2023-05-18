/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

/** Finds the element given in parameter, useful for checking if an element is in an ancestor.
 * Here "same" refers to the Junit meaning: same object memory, equals with ==
*/
public class SameFilter implements Filter<CtElement> {
	private final CtElement argument2;

	public SameFilter(CtElement argument2) {
		this.argument2 = argument2;
	}

	@Override
	public boolean matches(CtElement element) {
		return element == argument2;
	}
}
