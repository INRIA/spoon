/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.comparator;

import spoon.reflect.declaration.CtElement;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares based on a toString representation.
 */
public class DeepRepresentationComparator implements Comparator<CtElement>, Serializable {

	@Override
	public int compare(CtElement o1, CtElement o2) {
		if (o1.getPosition().isValidPosition() == false) {
			return 1;
		}
		if (o2.getPosition().isValidPosition() == false) {
			return -1;
		}
		String current = getDeepRepresentation(o1);
		String other = getDeepRepresentation(o2);
		if (current.length() <= 0 || other.length() <= 0) {
			throw new ClassCastException("Unable to compare elements");
		}
		return current.compareTo(other);

	}

	private String getDeepRepresentation(CtElement elem) {
		return elem.toString();
	}

}
