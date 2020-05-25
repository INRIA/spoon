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
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;

import spoon.SpoonException;
import spoon.support.modelobs.FineModelChangeListener;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.support.reflect.declaration.CtElementImpl;

/**
 * The implementation of the {@link List}, which is used by Spoon model objects.
 * It assures:
 * 1) each inserted {@link CtElement} gets assigned correct parent
 * 2) each change is reported in {@link FineModelChangeListener}
 */
public abstract class ModelList<T extends CtElement> extends AbstractList<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<T> list = CtElementImpl.emptyList();

	protected ModelList() {
	}

	protected abstract CtElement getOwner();
	protected abstract CtRole getRole();
	protected abstract int getDefaultCapacity();
	protected void onSizeChanged(int newSize) {
	}

	@Override
	public T get(int index) {
		return list.get(index);
	}

	/**
	 * sets the new content of this List
	 * @param elements new content of this list
	 */
	public void set(Collection<T> elements) {
		//TODO the best would be to detect added/removed statements and to fire modifications only for them
		this.clear();
		if (elements != null && elements.isEmpty() == false) {
			this.addAll(elements);
		}
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public T set(int index, T element) {
		T oldElement = list.get(index);
		if (oldElement == element) {
			//no change
			return oldElement;
		}
		CtElement owner = getOwner();
		ensureModifiableList();
		getModelChangeListener().onListDelete(owner, getRole(), list, index, oldElement);
		linkToParent(owner, element);
		getModelChangeListener().onListAdd(owner, getRole(), list, index, element);
		list.set(index, element);
		updateModCount();
		return oldElement;
	}

	static void linkToParent(CtElement owner, CtElement element) {
		if (owner.getFactory().getEnvironment().checksAreSkipped() == false && element.isParentInitialized() && element.getParent() != owner) {
			//the `e` already has an different parent. Check if it is still linked to that parent
			if (element.getRoleInParent() != null) {
				throw new SpoonException("The default behavior has changed, a new check has been added! Don't worry, you can disable this check\n"
							+ "with one of the following options:\n"
							+ " - by configuring Spoon with getEnvironment().setSelfChecks(true)\n"							+ " - by removing the node from its previous parent (element.delete())\n"
							+ " - by cloning the node before adding it here (element.clone())\n"
							);
			}
		}
		element.setParent(owner);
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}
	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean add(T e) {
		if (e == null) {
			return false;
		}
		CtElement owner = getOwner();
		ensureModifiableList();
		linkToParent(owner, e);
		getModelChangeListener().onListAdd(owner, getRole(), list, e);
		boolean result = list.add(e);
		updateModCount();
		onSizeChanged(list.size());
		return result;
	}

	@Override
	public boolean remove(Object o) {
		if (list.isEmpty()) {
			return false;
		}
		int size = list.size();
		for (int i = 0; i < size; i++) {
			//first do not use equals, but same
			if (list.get(i) == o) {
				remove(i);
				return true;
			}
		}
		int idx = list.indexOf(o);
		if (idx >= 0) {
			remove(idx);
			return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public void clear() {
		getModelChangeListener().onListDeleteAll(getOwner(), getRole(), list, new ArrayList<>(list));
		list = CtElementImpl.emptyList();
		modCount++;
		onSizeChanged(list.size());
	}

	@Override
	public boolean equals(Object o) {
		return list.equals(o);
	}

	@Override
	public int hashCode() {
		return list.hashCode();
	}

	@Override
	public void add(int index, T element) {
		if (element == null) {
			return;
		}
		CtElement owner = getOwner();
		ensureModifiableList();
		linkToParent(owner, element);
		getModelChangeListener().onListAdd(owner, getRole(), list, index, element);
		list.add(index, element);
		updateModCount();
		onSizeChanged(list.size());
	}

	@Override
	public T remove(int index) {
		T oldElement = list.get(index);
		getModelChangeListener().onListDelete(getOwner(), getRole(), list, index, oldElement);
		list.remove(index);
		updateModCount();
		onSizeChanged(list.size());
		return oldElement;
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	/**
	 * This ArrayList wrapper is needed to get access to protected ArrayList#modCount
	 * To be able to read modCount from `list` and to copy it into this.modCount
	 * To manage the {@link ConcurrentModificationException}.
	 * See https://docs.oracle.com/javase/7/docs/api/java/util/AbstractList.html#modCount
	 */
	private static class InternalList<T> extends ArrayList<T> {
		InternalList(int initialCapacity) {
			super(initialCapacity);
		}

		int getModCount() {
			return modCount;
		}
	}

	protected void updateModCount() {
		if (list instanceof InternalList) {
			modCount = ((InternalList) list).getModCount();
		}
	}

	private void ensureModifiableList() {
		if (list == CtElementImpl.<T>emptyList()) {
			list = new InternalList<>(getDefaultCapacity());
		}
	}

	private FineModelChangeListener getModelChangeListener() {
		return getOwner().getFactory().getEnvironment().getModelChangeListener();
	}
}
