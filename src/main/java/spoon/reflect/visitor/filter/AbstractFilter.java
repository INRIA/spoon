/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import java.lang.reflect.Method;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import spoon.support.util.RtHelper;

/**
 * Defines an abstract filter based on matching on the element types.
 *
 * Not necessary in simple cases thanks to the use of runtime reflection.
 */
public abstract class AbstractFilter<T extends CtElement> implements Filter<T> {

	private Class<T> type;

	/**
	 * Creates a filter with the type of the potentially matching elements.
	 */
	@SuppressWarnings("unchecked")
	public AbstractFilter(Class<? super T> type) {
		this.type = (Class<T>) type;
	}

	/**
	 * Creates a filter with the type computed by reflection from the matches method parameter
	 */
	@SuppressWarnings("unchecked")
	public AbstractFilter() {
		Method method = RtHelper.getMethod(getClass(), "matches", 1);
		if (method == null) {
			throw new SpoonException("The method matches with one parameter was not found on the class " + getClass().getName());
		}
		this.type = (Class<T>) method.getParameterTypes()[0];
	}

	public Class<T> getType() {
		return type;
	}

	@Override
	public boolean matches(T element) {
		return type.isAssignableFrom(element.getClass());
	}
}
