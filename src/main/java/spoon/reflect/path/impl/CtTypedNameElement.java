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
package spoon.reflect.path.impl;

import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.Collection;

/**
 * spoon.reflect.path.impl.CtPathElement that match on CtNamedElement
 */
public class CtTypedNameElement<P extends CtElement, T extends CtElement> extends AbstractPathElement<P, T> {
	public static final String STRING = "/";
	private final Class<T> type;

	public CtTypedNameElement(Class<T> type) {
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}

	@Override
	public String toString() {
		return STRING + type.getSimpleName() + getParamString();
	}

	@Override
	public Collection<T> getElements(Collection<P> roots) {
		Collection<T> elements = new ArrayList<T>();
		for (CtElement root : roots) {
			for (CtElement child : getChilds(root)) {
				if (match(child)) {
					elements.add((T) child);
				}
			}
		}
		return elements;
	}

	private boolean match(CtElement element) {
		return element != null && type.isAssignableFrom(element.getClass());
	}
}
