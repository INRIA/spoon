/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
import spoon.reflect.path.CtPath;

import java.util.ArrayList;
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
	public <T extends CtElement> Collection<T> evaluateOn(Collection<? extends CtElement> startNode) {
		Collection<CtElement> filtered = new ArrayList<>(startNode);
		for (CtPathElement element : elements) {
			filtered = element.getElements(filtered);
		}
		return (Collection<T>) filtered;
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
