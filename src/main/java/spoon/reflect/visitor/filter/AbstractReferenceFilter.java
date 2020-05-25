/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.Filter;

/**
 * This class defines an abstract reference filter that needs to be subclassed
 * in order to define the matching criteria.
 *
 * @param <T>
 * 		the type of the reference to be matched
 */
public abstract class AbstractReferenceFilter<T extends CtReference> extends AbstractFilter<T> implements Filter<T> {

	/**
	 * Creates a reference filter with the type of the potentially matching
	 * references.
	 */
	@SuppressWarnings("unchecked")
	public AbstractReferenceFilter(Class<? super T> type) {
		super(type);
	}

	/**
	 * Creates a filter with the type computed by reflection from the matches method parameter
	 */
	public AbstractReferenceFilter() {
	}
}
