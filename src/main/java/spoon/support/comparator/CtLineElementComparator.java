/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.comparator;

import java.io.Serializable;
import java.util.Comparator;

import spoon.reflect.declaration.CtElement;

/**
 * Comparator of compile-time elements. Elements are sorted by position in
 * source files.
 */
public class CtLineElementComparator implements Comparator<CtElement>, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Returns 0 if o1 has the same position as o2, or both positions are invalid and o1.equals(o2).
	 * Returns -1 if o1 is before o2 in the file, or o1 has no valid position or both positions are invalid !o1.equals(o2).
	 * Returns 1 if o2 is after o1 in the file, or o2 has no valid position.
	 */
	@Override
	public int compare(CtElement o1, CtElement o2) {
		if (!o1.getPosition().isValidPosition() && !o2.getPosition().isValidPosition()) {
			return o1.equals(o2) ? 0 : ((o1.hashCode() < o2.hashCode()) ? -1 : 1);
		}
		if (!o1.getPosition().isValidPosition()) {
			return -1;
		}

		if (!o2.getPosition().isValidPosition()) {
			// ensures that compare(x,y) = - compare(y,x)
			return 1;
		}

		int pos1 = o1.getPosition().getSourceStart();
		int pos2 = o2.getPosition().getSourceStart();

		if (pos1 == pos2) {
			int pos3 = o1.getPosition().getSourceEnd();
			int pos4 = o2.getPosition().getSourceEnd();
			if (pos3 == pos4) {
				return 0;
			}
			return (pos3 < pos4) ? -1 : 1;
		}

		return (pos1 < pos2) ? -1 : 1;
	}

}
