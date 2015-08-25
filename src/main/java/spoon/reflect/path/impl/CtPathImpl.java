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

	private LinkedList<CtPathElement> elements = new LinkedList<CtPathElement>();

	public List<CtPathElement> getElements() {
		return elements;
	}

	@Override
	public <T extends CtElement> Collection<T> evaluateOn(Collection<? extends CtElement> startNode) {
		Collection<CtElement> filtered = new ArrayList<CtElement>(startNode);
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
