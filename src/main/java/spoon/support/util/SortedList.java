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

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

public class SortedList<E> extends LinkedList<E> {

	private static final long serialVersionUID = 1L;

	Comparator<? super E> comparator;

	public SortedList(Comparator<? super E> comparator) {
		super();
		this.comparator = comparator;
	}

	@Override
	public boolean add(E o) {
		for (ListIterator<E> iterator = this.listIterator(); iterator.hasNext();) {
			E e = iterator.next();
			if (comparator.compare(o, e) < 0) {
				iterator.previous();
				iterator.add(o);
				return true;
			}
		}
		return super.add(o);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean ret = true;
		for (E e : c) {
			ret &= add(e);
		}
		return ret;
	}

	public Comparator<? super E> getComparator() {
		return comparator;
	}

	public void setComparator(Comparator<? super E> comparator) {
		this.comparator = comparator;
	}

}
