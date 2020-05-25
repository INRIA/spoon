/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

/**
 * This class defines a composite filter, which can compose several filters
 * together by using {@link spoon.reflect.visitor.filter.FilteringOperator}.
 *
 * @author Renaud Pawlak
 */
public class CompositeFilter<T extends CtElement> implements Filter<T> {

	/**
	 * Defines the matching using
	 * {@link spoon.reflect.visitor.filter.FilteringOperator}.
	 */
	@Override
	public boolean matches(T element) {
		switch (operator) {
		case INTERSECTION:
			for (Filter<T> f : filters) {
				if (!hasMatch(f, element)) {
					return false;
				}
			}
			return true;
		case UNION:
			for (Filter<T> f : filters) {
				if (hasMatch(f, element)) {
					return true;
				}
			}
			return false;
		case SUBSTRACTION:
			if (filters.length == 0) {
				return false;
			}
			if (!filters[0].matches(element)) {
				return false;
			}
			for (int i = 1; i < filters.length; i++) {
				if (filters[i].matches(element)) {
					return false;
				}
			}
			return true;
		default:
			return false;
		}
	}

	Filter<T>[] filters;

	FilteringOperator operator;

	/**
	 * Creates a new composite filter.
	 *
	 * @param operator
	 * 		the operator used to compose the filters together
	 * @param filters
	 * 		the filters to be composed
	 */
	public CompositeFilter(FilteringOperator operator, Filter<T>... filters) {
		this.filters = filters;
		this.operator = operator;
	}

	private boolean hasMatch(Filter<T> filter, T element) {
		try {
			return filter.matches(element);
		} catch (ClassCastException e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Class<T> getType() {
		return (Class<T>) CtElement.class;
	}

}
