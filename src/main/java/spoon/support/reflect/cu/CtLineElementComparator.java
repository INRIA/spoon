/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.support.reflect.cu;

import java.util.Comparator;

import spoon.reflect.declaration.CtElement;

/**
 * Comparator of compile-time elements. Elements are sorted by position in
 * source files.
 */
public class CtLineElementComparator implements Comparator<CtElement> {

	/**
	 * Compares two program elements.
	 */
	public int compare(CtElement o1, CtElement o2) {
		if (o1.getPosition() == null) {
			return 1;
		}
		if (o2.getPosition() == null) {
			return -1;
		}
		if (o1.getPosition().getLine() == o2.getPosition().getLine()) {
			return ((Integer) o1.getPosition().getColumn()).compareTo(o2.getPosition().getColumn());
		}
		return ((Integer) o1.getPosition().getLine()).compareTo(o2.getPosition().getLine());
	}

}
