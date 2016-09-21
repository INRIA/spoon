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
import java.util.TreeSet;

import spoon.reflect.declaration.CtExecutable;
import spoon.support.comparator.SignatureComparator;

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
