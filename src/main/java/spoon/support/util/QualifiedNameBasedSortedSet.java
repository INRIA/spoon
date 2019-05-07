/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util;

import java.util.Collection;
import java.util.TreeSet;

import spoon.reflect.declaration.CtElement;
import spoon.support.comparator.QualifiedNameComparator;

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

}
