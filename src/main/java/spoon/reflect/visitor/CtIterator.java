/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A class to be able to iterate over the children elements in the tree of a given node, in depth-first order.
 */
public class CtIterator extends CtScanner implements Iterator<CtElement> {
	/**
	 * A deque containing the elements the iterator has seen but not expanded
	 */
	private Deque<CtElement> deque = new ArrayDeque<CtElement>() {
		/**
		 * add a collection of elements with addFirst instead of default add() which defaults to addLast()
		 * @param c Collection of CtElements
		 * @return true if this deque has changed, in accordance with original method
		 */
		@Override
		public boolean addAll(Collection<? extends CtElement> c) {
			for (CtElement aC : c) {
				this.addFirst(aC);
			}
			return !c.isEmpty();
		}
	};

	/**
	 * A deque to be used when scanning an element so that @deque preserves the elements in dfs without complete expansion
	 */
	private ArrayDeque<CtElement> currentChildren = new ArrayDeque<>();

	/**
	 * CtIterator constructor, prepares the iterator from the @root node
	 *
	 * @param root the initial node to expand
	 */
	public CtIterator(CtElement root) {
		if (root != null) {
			deque.add(root);
		}
	}

	/**
	 * prevent scanner from going down the tree, instead save with other CtElement children of the current node
	 *
	 * @param element the next direct child of the current node being expanded
	 */
	@Override
	public void scan(CtElement element) {
		if (element != null) {
			currentChildren.addFirst(element);
		}
	}

	@Override
	public boolean hasNext() {
		return !deque.isEmpty();
	}

	/**
	 * Dereference the "iterator"
	 *
	 * @return CtElement the next element in DFS order without going down the tree
	 */
	@Override
	public CtElement next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		CtElement next = deque.pollFirst(); // get the element to expand from the deque
		currentChildren.clear(); // clear for this scan
		next.accept(this); // call @scan for each direct child of the node
		deque.addAll(currentChildren); // overridden method to add all to first
		return next;
	}
}
