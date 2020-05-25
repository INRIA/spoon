/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.reference;

import spoon.reflect.declaration.CtModule;
import spoon.support.DerivedProperty;

/**
 * Represents a reference to a {@link CtModule}
 */
public interface CtModuleReference extends CtReference {

	@Override
	@DerivedProperty
	CtModule getDeclaration();

	@Override
	CtModuleReference clone();
}
