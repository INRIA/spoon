/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.path.impl;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Default implementation for a CtPath
 */
public class CtPathImpl implements CtPath {

	private LinkedList<CtPathElement> elements = new LinkedList<>();

	public List<CtPathElement> getElements() {
		return elements;
	}

	@Override
	public <T extends CtElement> List<T> evaluateOn(CtElement... startNode) {
		Collection<CtElement> filtered = Arrays.asList(startNode);
		for (CtPathElement element : elements) {
			filtered = element.getElements(filtered);
		}
		return (List<T>) filtered;
	}

	@Override
	public CtPath relativePath(CtElement parent) {
		List<CtElement> roots = new ArrayList<>();
		roots.add(parent);

		int index = 0;
		for (CtPathElement pathEl : getElements()) {
			if (pathEl.getElements(roots).size() > 0) {
				break;
			}
			index++;
		}
		CtPathImpl result = new CtPathImpl();
		result.elements = new LinkedList<>(elements.subList(index, elements.size()));
		return result;
	}

	public CtPathImpl addFirst(CtPathElement element) {
		elements.addFirst(element);
		return this;
	}

	public CtPathImpl addLast(CtPathElement element) {
		elements.addLast(element);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (CtPathElement element : elements) {
			str.append(element.toString());
		}
		return str.toString();
	}
}
