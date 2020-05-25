/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;

/**
 * This class is the superclass for all the sub-factories of
 * {@link spoon.reflect.factory.Factory}.
 */
public abstract class SubFactory {

	protected Factory factory;

	/**
	 * The sub-factory constructor takes an instance of the parent factory.
	 */
	public SubFactory(Factory factory) {
		this.factory = factory;
	}
}

