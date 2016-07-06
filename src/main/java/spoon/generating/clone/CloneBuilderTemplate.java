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
package spoon.generating.clone;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtInheritanceScanner;


/**
 * Used to set all data in the cloned element.
 *
 * This class is generated automatically by the processor {@link spoon.generating.CloneVisitorGenerator}.
 */
class CloneBuilderTemplate extends CtInheritanceScanner {
	public static <T extends CtElement> T build(CtElement element, CtElement other) {
		return build(new CloneBuilderTemplate(), element, other);
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
