/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;

import java.util.ArrayDeque;
import java.util.Iterator;
/**
 * A class to be able to iterate over the children elements in the tree of a given node, in breadth-first order.
 */
public class CtBFSIterator extends CtScanner implements Iterator<CtElement> {


	/**
	 * A deque containing the elements the iterator has seen but not expanded
	 */
	private ArrayDeque<CtElement> deque = new ArrayDeque<>();

	/**
	 * CtIterator constructor, prepares the iterator from the @root node
	 *
	 * @param root the initial node to expand
	 */
	public CtBFSIterator(CtElement root) {
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
			deque.add(element);
		}
	}

	@Override
	public boolean hasNext() {
		return !deque.isEmpty();
	}

	/**
	 * Dereference the "iterator"
	 *
	 * @return CtElement the next element in BFS order without going down the tree
	 */
	@Override
	public CtElement next() {
		if (!hasNext()) {
			throw new java.util.NoSuchElementException();
		}
		CtElement next = deque.poll(); // get the element to expand from the deque
		next.accept(this); // call @scan for each direct child of the node
		return next;
	}
}
