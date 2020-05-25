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
 * This simple filter matches all the references to a given element by using
 * reference equality.
 */
public class DirectReferenceFilter<T extends CtReference> extends AbstractFilter<T> {
	CtReference reference;

	/**
	 * Creates the filter.
	 *
	 * @param reference
	 *            the matching reference
	 */
	@SuppressWarnings("unchecked")
	public DirectReferenceFilter(CtReference reference) {
		super((Class<T>) reference.getClass());
		this.reference = reference;
	}

	@Override
	public boolean matches(T reference) {
		if (super.matches(reference) == false) {
			return false;
		}
		return this.reference.equals(reference);
	}
}
