/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
