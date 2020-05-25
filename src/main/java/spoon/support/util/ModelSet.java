/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import spoon.SpoonException;
import spoon.support.modelobs.FineModelChangeListener;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;

import static spoon.support.util.ModelList.linkToParent;

/**
 * The implementation of the {@link Set}, which is used by Spoon model objects.
 * It assures:
 * 1) each inserted {@link CtElement} gets assigned correct parent
 * 2) each change is reported in {@link FineModelChangeListener}
 */
public abstract class ModelSet<T extends CtElement> extends AbstractSet<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Set<T> set;

	protected ModelSet(Comparator<? super CtElement> comparator) {
		set = new TreeSet<>(comparator);
	}

	protected abstract CtElement getOwner();
	protected abstract CtRole getRole();
	protected void onSizeChanged(int newSize) {
	}

	@Override
	public int size() {
		return set.size();
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return set.contains(o);
	}

	@Override
	public Object[] toArray() {
		return set.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return set.toArray(a);
	}

	@Override
	public boolean add(T e) {
		if (e == null) {
			return false;
		}
		CtElement owner = getOwner();
		linkToParent(owner, e);
		getModelChangeListener().onSetAdd(owner, getRole(), set, e);

		// we make sure that the element is always the last put in the set
		// for being least suprising for client code
		if (set.contains(e)) {
			set.remove(e);
		}

		set.add(e);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		if (set.contains(o) == false) {
			return false;
		}
		@SuppressWarnings("unchecked")
		T e = (T) o;
		getModelChangeListener().onSetDelete(getOwner(), getRole(), set, e);
		if (set.remove(o) == false) {
			throw new SpoonException("Element was contained in the Set, but Set#remove returned false. Not removed??");
		}
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
	}

	@Override
	public void clear() {
		if (set.isEmpty()) {
			return;
		}
		getModelChangeListener().onSetDeleteAll(getOwner(), getRole(), set, new LinkedHashSet<>(set));
		set.clear();
	}

	@Override
	public boolean equals(Object o) {
		return set.equals(o);
	}

	@Override
	public int hashCode() {
		return set.hashCode();
	}

	@Override
	public Iterator<T> iterator() {
		return new Itr();
	}

	private class Itr implements Iterator<T> {
		final Iterator<T> delegate;
		T lastReturned = null;
		Itr() {
			delegate = set.iterator();
		}
		@Override
		public boolean hasNext() {
			return delegate.hasNext();
		}
		@Override
		public T next() {
			lastReturned = delegate.next();
			return lastReturned;
		}
		@Override
		public void remove() {
			ModelSet.this.remove(lastReturned);
		}
	}

	private FineModelChangeListener getModelChangeListener() {
		return getOwner().getFactory().getEnvironment().getModelChangeListener();
	}

	public void set(Collection<T> elements) {
		//TODO the best would be to detect added/removed statements and to fire modifications only for them
		this.clear();
		if (elements != null && elements.isEmpty() == false) {
			this.addAll(elements);
		}
	}
}
