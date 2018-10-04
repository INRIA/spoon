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
package spoon.pattern.internal.node;

import spoon.pattern.internal.PatternPrinter;

/**
 * Represents a parameterized Pattern ValueResolver, which can be used
 * <ul>
 * <li>to generate a zero, one or more copies of model using provided parameters</li>
 * <li>to match zero, one or more instances of model and deliver a matching parameters</li>
 * </ul>
 */
public abstract class AbstractNode implements RootNode {
	private boolean simplifyGenerated = false;
	@Override
	public String toString() {
		return new PatternPrinter().printNode(this);
	}
	@Override
	public boolean isSimplifyGenerated() {
		return simplifyGenerated;
	}
	@Override
	public void setSimplifyGenerated(boolean simplifyGenerated) {
		this.simplifyGenerated = simplifyGenerated;
	}
}
