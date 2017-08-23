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
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.visitor.Filter;

/**
 * Filters elements by name and by type (for instance to find a method). Example:
 *
 * <pre>
 * CtMethod&lt;?&gt; normalFor = type.getElements(
 * 		new NamedElementFilter(&quot;normalFor&quot;, CtMethod.class)).get(0);
 * </pre>
 */
public class NamedElementFilter implements Filter<CtNamedElement> {
	private final String name;
	private Class<? extends CtNamedElement> acceptedClass;

	/**
	 *
	 * @param name Name of the expected element
	 */
	public NamedElementFilter(String name) {
		this(name, CtNamedElement.class);
	}

	/**
	 *
	 * @param name Name of the expected element
	 * @param acceptedClass Expected class of the results
	 */
	public NamedElementFilter(String name, Class<? extends CtNamedElement> acceptedClass) {
		if (name == null || acceptedClass == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.acceptedClass = acceptedClass;
	}

	public boolean matches(CtNamedElement element) {
		try {
			return acceptedClass.isAssignableFrom(element.getClass()) && name.equals(element.getSimpleName());
		} catch (UnsupportedOperationException e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Class<? extends CtNamedElement> getType() {
		return acceptedClass;
	}
}
