/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmptyIterator<E> implements Iterator<E> {
	private static final EmptyIterator<Object> EMPTY_ITERATOR = new EmptyIterator<>();

	public static <T> Iterator<T> instance() {
		return (Iterator<T>) EMPTY_ITERATOR;
	}

	EmptyIterator() {
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public E next() {
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		throw new IllegalStateException();
	}
}
