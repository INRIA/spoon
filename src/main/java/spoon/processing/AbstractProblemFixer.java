/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.processing;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;

/**
 * To be subclassed when defining a new problem fixer.
 */
public abstract class AbstractProblemFixer<T extends CtElement>
		implements ProblemFixer<T> {
	Factory factory;

	/**
	 * Default constructor.
	 */
	public AbstractProblemFixer() {
	}

	@Override
	public Factory getFactory() {
		return factory;
	}

	@Override
	public void setFactory(Factory factory) {
		this.factory = factory;
	}

}
