/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.legacy;


import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.NamedElementFilter;


/**
 * Filters elements by name (for instance to find a method). Example:
 *
 * <pre>
 * CtMethod&lt;?&gt; normalFor = type.getElements(
 * 		new NameFilter&lt;CtMethod&lt;?&gt;&gt;(&quot;normalFor&quot;)).get(0);
 * </pre>
 *
 * Use {@link NamedElementFilter} instead: the actual NameFilter could return wrongly typed results. NamedElementFilter explicit the use of a type.
 */
public class NameFilter<T extends CtNamedElement> implements Filter<T> {
	private final String name;

	/**
	 * @param name Name of the expected element
	 */
	public NameFilter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
	}

	@Override
	public boolean matches(T element) {
		try {
			return name.equals(element.getSimpleName());
		} catch (UnsupportedOperationException e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Class<T> getType() {
		return (Class<T>) CtNamedElement.class;
	}
}
