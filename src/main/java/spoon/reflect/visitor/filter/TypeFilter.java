/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtElement;

/**
 * This simple filter matches all the elements of a given type.
 */
public class TypeFilter<T extends CtElement> extends AbstractFilter<T> {

	/**
	 * Creates the filter.
	 *
	 * @param type
	 * 		the type that matches
	 */
	public TypeFilter(Class<? super T> type) {
		super(type);
	}

}
