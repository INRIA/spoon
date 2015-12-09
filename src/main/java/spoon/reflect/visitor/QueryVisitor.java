/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.declaration.CtElement;

/**
 * A simple visitor that takes a filter and returns all the elements that match
 * it.
 */
public class QueryVisitor<T extends CtElement> extends CtScanner {
	Filter<T> filter;

	List<T> result = new ArrayList<T>();

	/**
	 * Constructs a query visitor with a given filter.
	 */
	public QueryVisitor(Filter<T> filter) {
		super();
		this.filter = filter;
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
			if (filter.matches((T) element)) {
				result.add((T) element);
			}
		} catch (ClassCastException e) {
			// expected, some elements are not of type T
		}
		super.scan(element);
	}
}
