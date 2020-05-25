/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.generating.clone;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtInheritanceScanner;


/**
 * Used to set all data in the cloned element.
 *
 * This class is generated automatically by the processor spoon.generating.CloneVisitorGenerator.
 */
class CloneBuilderTemplate extends CtInheritanceScanner {
	public void copy(spoon.reflect.declaration.CtElement element, spoon.reflect.declaration.CtElement other) {
		this.setOther(other);
		this.scan(element);
	}

	public static <T extends CtElement> T build(CloneBuilderTemplate builder, CtElement element, CtElement other) {
		builder.setOther(other);
		builder.scan(element);
		return (T) builder.other;
	}

	private CtElement other;

	public void setOther(CtElement other) {
		this.other = other;
	}
}
