/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.reference.CtReference;

/**
 * This simple filter matches all the references of a given type.
 *
 * @param <T>
 * 		the type of the reference to be matched
 *
 */
public class ReferenceTypeFilter<T extends CtReference> extends TypeFilter<T> {

	/**
	 * Creates the filter.
	 *
	 * @param type
	 * 		the type that matches
	 */
	public ReferenceTypeFilter(Class<? super T> type) {
		super(type);
	}
}
