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
package spoon.support.comparator;

import java.io.Serializable;
import java.util.Comparator;

import spoon.reflect.declaration.CtElement;
import spoon.support.visitor.DeepRepresentationVisitor;

/**
 * Compares based on a toString representation. Used for backward compatibility.
 */
public class DeepRepresentationComparator implements Comparator<CtElement>, Serializable {

	@Override
	public int compare(CtElement o1, CtElement o2) {
		if (o1.getPosition() == null) {
			return 1;
		}
		if (o2.getPosition() == null) {
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
		DeepRepresentationVisitor prThis = new DeepRepresentationVisitor();
		prThis.scan(elem);
		return prThis.getRepresentation();
	}

}
