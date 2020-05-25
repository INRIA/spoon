/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
		Collection<T> elements = new ArrayList<>();
		for (CtElement root : roots) {
			for (CtElement child : getChildren(root)) {
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
