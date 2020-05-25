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
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.reference.CtReference;

/** compares based on names (with a preference for qualified names if available) */
public class QualifiedNameComparator implements Comparator<CtElement>, Serializable {
	private static final long serialVersionUID = 1L;

	public static final QualifiedNameComparator INSTANCE = new QualifiedNameComparator();

	@Override
	public int compare(CtElement o1, CtElement o2) {
		try {
			// qualified names if available
			// note: there is no common interface between
			// CtPackage.getQualifiedName and CtTypeInformation.getQualifiedName
			if (o1 instanceof CtTypeInformation && o2 instanceof CtTypeInformation) {
				return ((CtTypeInformation) o1).getQualifiedName().compareTo(((CtTypeInformation) o2).getQualifiedName());
			}
			if (o1 instanceof CtPackage && o2 instanceof CtPackage) {
				return ((CtPackage) o1).getQualifiedName().compareTo(((CtPackage) o2).getQualifiedName());
			}

			// otherwise names
			// note: there is no common interface between
			// CtReference.getSimpleName and CtTNamedElement.getSimpleName
			if (o1 instanceof CtReference && o2 instanceof CtReference) {
				return ((CtReference) o1).getSimpleName().compareTo(((CtReference) o2).getSimpleName());
			}
			if (o1 instanceof CtNamedElement && o2 instanceof CtNamedElement) {
				return ((CtNamedElement) o1).getSimpleName().compareTo(((CtNamedElement) o2).getSimpleName());
			}
			throw new IllegalArgumentException();
		} catch (NullPointerException e) {
			// when o1 or o2 is null, or no name are available
			return -1;
		}
	}
}

