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
		if (e == null || set.contains(e)) {
			return false;
		}
		CtElement owner = getOwner();
		linkToParent(owner, e);
		getModelChangeListener().onSetAdd(owner, getRole(), set, e);
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
