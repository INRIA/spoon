/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
		CtElement next = deque.poll(); // get the element to expand from the deque
		next.accept(this); // call @scan for each direct child of the node
		return next;
	}
}
