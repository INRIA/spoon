/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.reference.CtReference;

/**
 * A filter for {@link CtReference}s that compare equal to the reference provided in the constructor.
 * Note that this does <strong>not</strong> cover all references to the pointed-to element.
 * If the reference to search for refers to a field, for example, type information is part of the
 * {@link spoon.reflect.reference.CtVariableReference}. This will cause searches for generic fields
 * to turn up short, as references with a known type are <em>different</em>.
 */
public class DirectReferenceFilter<T extends CtReference> extends AbstractFilter<T> {
	private final CtReference reference;

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
		if (!super.matches(reference)) {
			return false;
		}
		return this.reference.equals(reference);
	}
}
