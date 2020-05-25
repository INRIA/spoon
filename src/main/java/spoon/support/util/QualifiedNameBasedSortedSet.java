/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.stream.Stream;

import spoon.reflect.declaration.CtElement;
import spoon.support.comparator.CtLineElementComparator;
import spoon.support.comparator.QualifiedNameComparator;

/**
 * The set properties of this set are based on the qualified name of the element inserted. Using this set for elements
 * without a qualified name does not work well. See the {@link QualifiedNameComparator} for details.
 *
 * The order of the iterator and stream of this set is based on two properties: qualified name and position. See
 * {@link #stream()} for details.
 *
 * @param <E>
 */
public class QualifiedNameBasedSortedSet<E extends CtElement> extends
		TreeSet<E> {

	private static final long serialVersionUID = 1L;

	public QualifiedNameBasedSortedSet(Collection<E> elements) {
		this();
		addAll(elements);
	}

	public QualifiedNameBasedSortedSet() {
		super(new QualifiedNameComparator());
	}

	/**
	 * The order of elements in this iterator is described in {@link #stream()}.
	 *
	 * @return A sorted iterator with all elements in this set.
	 */
	@Override
	public Iterator<E> iterator() {
		return stream().iterator();
	}

	/**
	 * The elements of this stream is ordered into two partitions: elements without source position and elements with
	 * source position.
	 *
	 * Elements without source position appear first, and are between themselves ordered by their qualified names.
	 * Elements with source position appear last, and are between themselves ordered by their source position.
	 *
	 * The rationale for this ordering is that elements such as types listed in implements clauses, or types
	 * listed in thrown clauses, should not be reordered by qualified name as a result of parsing and printing with
	 * Spoon.
	 *
	 * @return A sorted stream of all elements in this set.
	 */
	@Override
	public Stream<E> stream() {
		// implementation detail: the elements are all ready sorted by the QualifiedNameComparator. Stable sort with
		// the CtLineElementComparator ensures that elements with no source position appear before elements with source
		// position, but the noposition elements' order relative to each other is not changed.
		return super.stream().sorted(new CtLineElementComparator());
	}
}
