/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public final class EmptyClearableList<E> extends AbstractList<E> implements RandomAccess, Serializable {
	private static final long serialVersionUID = 0L;

	private static final EmptyClearableList<Object> EMPTY_LIST = new EmptyClearableList<>();

	public static <T> List<T> instance() {
		return (List<T>) EMPTY_LIST;
	}

	private EmptyClearableList() {
	}

	@Override
	public void clear() {
		// do nothing
	}

	@Override
	public Iterator<E> iterator() {
		return EmptyIterator.instance();
	}

	@Override
	public ListIterator<E> listIterator() {
		return (ListIterator<E>) EmptyListIterator.EMPTY_LIST_ITERATOR;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean contains(Object obj) {
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return c.isEmpty();
	}

	@Override
	public Object[] toArray() {
		return new Object[0];
	}

	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length > 0) {
			a[0] = null;
		}
		return a;
	}

	@Override
	public E get(int index) {
		throw new IndexOutOfBoundsException("Index: " + index);
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof List) && ((List<?>) o).isEmpty();
	}

	@Override
	public int hashCode() {
		return 1;
	}

	// Preserves singleton property
	private Object readResolve() {
		return EMPTY_LIST;
	}

	private static final class EmptyListIterator<E> extends EmptyIterator<E> implements ListIterator<E> {
		static final EmptyListIterator<Object> EMPTY_LIST_ITERATOR = new EmptyListIterator<>();

		@Override
		public boolean hasPrevious() {
			return false;
		}

		@Override
		public E previous() {
			throw new NoSuchElementException();
		}

		@Override
		public int nextIndex() {
			return 0;
		}

		@Override
		public int previousIndex() {
			return -1;
		}

		@Override
		public void set(E e) {
			throw new IllegalStateException();
		}

		@Override
		public void add(E e) {
			throw new UnsupportedOperationException();
		}
	}
}
