/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
