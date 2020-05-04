/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import spoon.Launcher;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.reference.CtReference;
import spoon.support.comparator.QualifiedNameComparator;

public class QualifiedNameBasedSortedSet<E extends CtElement> implements Set<E>, Serializable {
	private static final long serialVersionUID = 1L;

	private final LinkedHashSet<QualifiedNameHashEqualsWrapper> set;

	public QualifiedNameBasedSortedSet(Collection<E> elements) {
		this();
		addAll(elements);
	}

	public QualifiedNameBasedSortedSet() {
		set = new LinkedHashSet<>();
	}

	private static String getQualifiedName(CtElement element) {
		if (element instanceof CtTypeInformation) {
			return ((CtTypeInformation) element).getQualifiedName();
		} else if (element instanceof CtPackage) {
			return ((CtPackage) element).getQualifiedName();
		} else if (element instanceof CtReference) {
			return ((CtReference) element).getSimpleName();
		} else if (element instanceof CtNamedElement) {
			return ((CtNamedElement) element).getSimpleName();
		}

		Launcher.LOGGER.warn(QualifiedNameBasedSortedSet.class.getName() + " used for element without name: " + element.getClass().getName());
		return "";
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
		return o instanceof CtElement && set.contains(new QualifiedNameHashEqualsWrapper((CtElement) o));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterator<E> iterator() {
		return set.stream().map(e -> (E) e.element).iterator();
	}

	@Override
	public Object[] toArray() {
		return set.stream().map(e -> e.element).toArray();
	}

	@Override
	public <T> T[] toArray(T[] ts) {
		return set.stream().map(e -> e.element).collect(Collectors.toList()).toArray(ts);
	}

	@Override
	public boolean add(E e) {
		return set.add(new QualifiedNameHashEqualsWrapper(e));
	}

	@Override
	public boolean remove(Object o) {
		return o instanceof CtElement &&
				set.removeIf(e -> getQualifiedName(e.element).equals(getQualifiedName((CtElement) o)));
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		return collection.stream().allMatch(this::contains);
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		return collection.stream().anyMatch(this::add);
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		set.clear();
	}

	/**
	 * A small wrapper around a CtElement that provides hashCode and equals methods based on the element's qualified
	 * name.
	 */
	private static final class QualifiedNameHashEqualsWrapper implements Serializable {
		private static final long serialVersionUID = 1L;
		final CtElement element;

		QualifiedNameHashEqualsWrapper(CtElement element) {
			this.element = element;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			QualifiedNameHashEqualsWrapper that = (QualifiedNameHashEqualsWrapper) o;
			return QualifiedNameComparator.INSTANCE.compare(element, that.element) == 0;
		}

		@Override
		public int hashCode() {
			return Objects.hash(getQualifiedName(element));
		}
	}

}
