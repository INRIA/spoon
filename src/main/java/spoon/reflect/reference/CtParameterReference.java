/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.reference;

import spoon.reflect.declaration.CtParameter;
import spoon.support.DerivedProperty;


/**
 * This interface defines a reference to a
 * {@link spoon.reflect.declaration.CtParameter} of a method.
 */
public interface CtParameterReference<T> extends CtVariableReference<T> {

	/**
	 * Gets the declaring executable of the referenced parameter.
	 */
	@DerivedProperty
	CtExecutableReference<?> getDeclaringExecutable();

	@Override
	@DerivedProperty
	CtParameter<T> getDeclaration();

	@Override
	CtParameterReference<T> clone();
}
