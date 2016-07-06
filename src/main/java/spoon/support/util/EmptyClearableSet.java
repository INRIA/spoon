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
package spoon.support.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class EmptyClearableSet<E> extends AbstractSet<E> implements Serializable {
	private static final long serialVersionUID = 0L;

	private static final EmptyClearableSet<Object> EMPTY_SET = new EmptyClearableSet<>();

	public static <T> Set<T> instance() {
		return (Set<T>) EMPTY_SET;
	}

	private EmptyClearableSet() {
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

	// Preserves singleton property
	private Object readResolve() {
		return EMPTY_SET;
	}
}
