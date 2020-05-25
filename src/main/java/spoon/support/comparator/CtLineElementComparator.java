/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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

	/**
	 * Reurns -1 if o1 is before o2 in the file
	 */
	@Override
	public int compare(CtElement o1, CtElement o2) {
		if (o1.getPosition().isValidPosition() == false) {
			return -1;
		}

		if (o2.getPosition().isValidPosition() == false) {
			// ensures that compare(x,y) = - compare(y,x)
			return 1;
		}

		int pos1 = o1.getPosition().getSourceStart();
		int pos2 = o2.getPosition().getSourceStart();

		if (pos1 == pos2) {
			int pos3 = o1.getPosition().getSourceEnd();
			int pos4 = o2.getPosition().getSourceEnd();
			return (pos3 < pos4) ? -1 : 1;
		}

		return (pos1 < pos2) ? -1 : 1;
	}

}
