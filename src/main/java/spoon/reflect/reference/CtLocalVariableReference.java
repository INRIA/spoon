/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.reference;

import spoon.reflect.code.CtLocalVariable;
import spoon.support.DerivedProperty;

/**
 * This interface defines a reference to
 * {@link spoon.reflect.code.CtLocalVariable}.
 */
public interface CtLocalVariableReference<T> extends CtVariableReference<T> {
	@Override
	@DerivedProperty
	CtLocalVariable<T> getDeclaration();

	@Override
	CtLocalVariableReference<T> clone();
}
