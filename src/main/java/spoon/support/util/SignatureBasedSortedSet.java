/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util;

import spoon.reflect.declaration.CtExecutable;
import spoon.support.comparator.SignatureComparator;

import java.util.Collection;
import java.util.TreeSet;

/** maintains unicity with method signatures */
public class SignatureBasedSortedSet<E extends CtExecutable<?>> extends TreeSet<E> {

	private static final long serialVersionUID = 1L;

	public SignatureBasedSortedSet(Collection<E> elements) {
		this();
		addAll(elements);
	}

	public SignatureBasedSortedSet() {
		super(new SignatureComparator());
	}

}
