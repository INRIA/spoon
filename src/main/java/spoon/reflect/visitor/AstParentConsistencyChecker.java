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
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;

public class AstParentConsistencyChecker extends CtScanner {

	private CtElement parent;
	@Override
	public void scan(CtElement element) {
		if (element == null) {
			return;
		}
		if (parent != null && element.getParent() != parent) {
			throw new IllegalStateException(toDebugString(element)
					+ "is set as child of\n" + toDebugString(element.getParent())
					+ "however it is visited as a child of\n" + toDebugString(parent));
		}
		CtElement parent = this.parent;
		this.parent = element;
		super.scan(element);
		this.parent = parent;
	}

	private static String toDebugString(CtElement e) {
		return "Element: " + e + "\nSignature: " + e.getShortRepresentation() + "\nClass: " + e.getClass() + "\n";
	}
}
