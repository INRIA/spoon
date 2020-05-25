/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.generating.clone;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;
import spoon.support.visitor.clone.CloneBuilder;
import spoon.support.visitor.equals.CloneHelper;

/**
 * Used to clone a given element.
 *
 * This class is generated automatically by the processor spoon.generating.CloneVisitorGenerator.
 */
class CloneVisitorTemplate extends CtScanner {
	private final CloneHelper cloneHelper;
	private final CloneBuilder builder = new CloneBuilder();
	private CtElement other;

	CloneVisitorTemplate(CloneHelper cloneHelper) {
		this.cloneHelper = cloneHelper;
	}

	public <T extends CtElement> T getClone() {
		return (T) other;
	}
}
