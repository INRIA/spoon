/**
 * Copyright (C) 2006-2017 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.reflect.visitor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.util.RtHelper;

/**
 * A simple visitor that takes a filter and returns all the elements that match
 * it.
 * @deprecated use {@link CtQuery#filterChildren(Filter)} instead.
 */
@Deprecated
public class QueryVisitor<T extends CtElement> extends CtScanner {
	private final Filter<T> filter;
	private final Class<T> filteredType;
	private final List<T> result = new ArrayList<>();

	/**
	 * Constructs a query visitor with a given filter.
	 */
	@SuppressWarnings("unchecked")
	public QueryVisitor(Filter<T> filter) {
		super();
		this.filter = filter;
		if (filter instanceof AbstractFilter) {
			filteredType =  ((AbstractFilter<T>) filter).getType();
		} else {
			Method method = RtHelper.getMethod(filter.getClass(), "matches", 1);
			if (method == null) {
				throw new SpoonException("The Filter class " + filter.getClass().getName() + " has no matches method with one parameter.");
			}
			filteredType = (Class<T>) method.getParameterTypes()[0];
		}
	}

	/**
	 * Gets the result (elements matching the filter).
	 */
	public List<T> getResult() {
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void scan(CtElement element) {
		if (element == null) {
			return;
		}
		try {
			if ((filteredType == null || filteredType.isAssignableFrom(element.getClass()))) {
				if (filter.matches((T) element)) {
					result.add((T) element);
				}
			}
		} catch (ClassCastException e) {
			// expected, some elements are not of type T
			// Still need to protect from CCE, if users extend Filter (instead of AbstractFilter) directly,
			// but with concrete type parameter
		}
		super.scan(element);
	}
}
