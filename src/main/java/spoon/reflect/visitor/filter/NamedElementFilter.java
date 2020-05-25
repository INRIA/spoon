/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.visitor.Filter;

/**
 * Filters elements by name and by type (for instance to find a method). Example:
 *
 * <pre>
 * CtMethod&lt;?&gt; normalFor = type.getElements(
 * 		new NamedElementFilter&lt;CtMethod&lt;?&gt;&gt;(CtMethod.class, &quot;normalFor&quot;)).get(0);
 * </pre>
 */
public class NamedElementFilter<T extends CtNamedElement> implements Filter<T> {
	private final String name;
	private Class<T> acceptedClass;

	/**
	 *
	 * @param name Name of the expected element
	 * @param acceptedClass Expected class of the results
	 */
	public NamedElementFilter(Class<T> acceptedClass, String name) {
		if (name == null || acceptedClass == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.acceptedClass = acceptedClass;
	}

	@Override
	public boolean matches(T element) {
		try {
			return acceptedClass.isAssignableFrom(element.getClass()) && name.equals(element.getSimpleName());
		} catch (UnsupportedOperationException e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Class<T> getType() {
		return acceptedClass;
	}
}
