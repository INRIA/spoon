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
