/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

public class SortedList<E> extends LinkedList<E> {

	private static final long serialVersionUID = 1L;

	Comparator<? super E> comparator;

	public SortedList(Comparator<? super E> comparator) {
		this.comparator = comparator;
	}

	@Override
	public boolean add(E o) {
		for (ListIterator<E> iterator = this.listIterator(); iterator.hasNext();) {
			E e = iterator.next();
			if (comparator.compare(o, e) < 0) {
				iterator.previous();
				iterator.add(o);
				return true;
			}
		}
		return super.add(o);
	}

	@Override
	public void add(int index, E element) {
		throw new IllegalArgumentException("cannot force a position with a sorted list that has its own ordering");
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean ret = false;
		for (E e : c) {
			ret |= add(e);
		}
		return ret;
	}

	public Comparator<? super E> getComparator() {
		return comparator;
	}

	public void setComparator(Comparator<? super E> comparator) {
		this.comparator = comparator;
	}

}
