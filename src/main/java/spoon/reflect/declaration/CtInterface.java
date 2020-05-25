/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeReference;
import spoon.support.UnsettableProperty;

/**
 * This element defines an interface declaration.
 */
public interface CtInterface<T> extends CtType<T> {
	@Override
	CtInterface<T> clone();

	@Override
	@UnsettableProperty
	<C extends CtType<T>> C setSuperclass(CtTypeReference<?> superClass);
}
