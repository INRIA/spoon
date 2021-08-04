/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util;

import static spoon.support.util.ModelList.linkToParent;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.support.modelobs.FineModelChangeListener;

/**
 * The implementation of the {@link Set}, which is used by Spoon model objects.
 * It assures:
 * 1) each inserted {@link CtElement} gets assigned correct parent
 * 2) each change is reported in {@link FineModelChangeListener}
 */
public abstract class KeyedModelSet<K, T extends CtElement> implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Map<K, T> map;

	protected KeyedModelSet() {
		this.map = new ConcurrentHashMap<>();
	}

	protected abstract CtElement getOwner();
	protected abstract CtRole getRole();
	protected void onSizeChanged(int newSize) {
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean contains(K key) {
		return map.containsKey(key);
	}

	public Collection<T> values() {
		return map.values();
	}

	public T get(K key) {
		return map.get(key);
	}

	public boolean add(K key, T e) {
		if (e == null) {
			return false;
		}
		CtElement owner = getOwner();
		linkToParent(owner, e);
		getModelChangeListener().onSetAdd(owner, getRole(), new HashSet<>(map.values()), e);

		// we make sure that the element is always the last put in the set
		// for being least suprising for client code
		map.put(key, e);

		return true;
	}

	public T remove(K key) {
		T removed = map.remove(key);

		if(removed == null) {
			return null;
		}

		getModelChangeListener().onSetDelete(
				getOwner(),
				getRole(),
				new HashSet<>(map.values()),
				removed
		);

		return removed;
	}

	public void clear() {
		if (map.isEmpty()) {
			return;
		}
		getModelChangeListener().onSetDeleteAll(
				getOwner(),
				getRole(),
				new HashSet<>(map.values()),
				new LinkedHashSet<>(map.values())
		);
		map.clear();
	}

	@Override
	public boolean equals(Object o) {
		return map.equals(o);
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	public Iterator<T> iterator() {
		return new Itr();
	}

	private class Itr implements Iterator<T> {
		final Iterator<Entry<K, T>> delegate;
		Entry<K, T> lastReturned = null;
		Itr() {
			delegate = map.entrySet().iterator();
		}
		@Override
		public boolean hasNext() {
			return delegate.hasNext();
		}
		@Override
		public T next() {
			lastReturned = delegate.next();
			return lastReturned.getValue();
		}
		@Override
		public void remove() {
			KeyedModelSet.this.remove(lastReturned.getKey());
		}
	}

	private FineModelChangeListener getModelChangeListener() {
		return getOwner().getFactory().getEnvironment().getModelChangeListener();
	}
}
