/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.processing;

import spoon.reflect.factory.Factory;

/**
 * This interface represents an object that can access the meta-model factory.
 */
public interface FactoryAccessor {

	/**
	 * Gets the factory of this object.
	 */
	Factory getFactory();

	/**
	 * Sets the factory object.
	 */
	void setFactory(Factory factory);

}
