/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.equals;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtScanner;
import spoon.support.util.EmptyClearableList;
import spoon.support.util.EmptyClearableSet;
import spoon.support.visitor.clone.CloneVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link CloneHelper} is responsible for creating clones of {@link CtElement} AST nodes including the whole subtree.
 *
 * By default, the same instance of {@link CloneHelper} is used for whole clonning process.
 *
 * However, by subclassing this class and overriding method {@link #clone(CtElement)},
 * one can extend and/or modify the cloning behavior.
 *
 * For instance, one can listen to each call to clone and get each pair of `clone source` and `clone target`.
 */
public class CloneHelper {
	public static final CloneHelper INSTANCE = new CloneHelper();

	public <T extends CtElement> T clone(T element) {
		final CloneVisitor cloneVisitor = new CloneVisitor(this);
		cloneVisitor.scan(element);
		return cloneVisitor.getClone();
	}

	public <T extends CtElement> Collection<T> clone(Collection<T> elements) {
		if (elements == null || elements.isEmpty()) {
			return new ArrayList<>();
		}
		Collection<T> others = new ArrayList<>();
		for (T element : elements) {
			addClone(others, element);
		}
		return others;
	}

	public <T extends CtElement> List<T> clone(List<T> elements) {
		if (elements instanceof EmptyClearableList) {
			return elements;
		}
		if (elements == null || elements.isEmpty()) {
			return new ArrayList<>();
		}
		List<T> others = new ArrayList<>();
		for (T element : elements) {
			addClone(others, element);
		}
		return others;
	}

	/**
	 * clones a Set of elements
	 * @param <T> the Set of elements to be cloned
	 * @return others Set of cloned elements
	 */
	public <T extends CtElement> Set<T> clone(Set<T> elements) {
		if (elements instanceof EmptyClearableSet) {
			return elements;
		}
		if (elements == null || elements.isEmpty()) {
			return EmptyClearableSet.instance();
		}
		Set<T> others = new HashSet<>(elements.size());
		for (T element : elements) {
			addClone(others, element);
		}
		return others;
	}

	public <T extends CtElement> Map<String, T> clone(Map<String, T> elements) {
		if (elements == null || elements.isEmpty()) {
			return new HashMap<>();
		}
		Map<String, T> others = new HashMap<>();
		for (Map.Entry<String, T> tEntry : elements.entrySet()) {
			addClone(others, tEntry.getKey(), tEntry.getValue());
		}
		return others;
	}

	/**
	 * clones an element and adds it's clone as value into targetCollection
	 * @param targetCollection - the collection which will receive a clone of element
	 * @param element to be cloned element
	 */
	protected <T extends CtElement> void addClone(Collection<T> targetCollection, T element) {
		targetCollection.add(clone(element));
	}

	/**
	 * clones a value and adds it's clone as value into targetMap under key
	 * @param targetMap - the Map which will receive a clone of value
	 * @param key the target key, which has to be used to add cloned value into targetMap
	 * @param value to be cloned element
	 */
	protected <T extends CtElement> void addClone(Map<String, T> targetMap, String key, T value) {
		targetMap.put(key, clone(value));
	}


	/** Is called by {@link CloneVisitor} at the end of the cloning for each element. */
	public void tailor(final spoon.reflect.declaration.CtElement topLevelElement, final spoon.reflect.declaration.CtElement topLevelClone) {
		// this scanner visit certain nodes to done some additional work after cloning
		new CtScanner() {
			@Override
			public <T> void visitCtExecutableReference(CtExecutableReference<T> clone) {
				// for instance, here we can do additional things
				// after cloning an executable reference
				// we have access here to "topLevelElement" and "topLevelClone"
				// if we want to analyze them as well.

				// super must be called to visit the subelements
				super.visitCtExecutableReference(clone);
			}
		}.scan(topLevelClone);
	}

}
