/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.code.CtStatement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.UnsettableProperty;

/**
 * This element defines an interface declaration.
 *
 * <pre>
 *     // an interface definition
 *     interface Foo {
 *        void bar();
 *     }
 * </pre>
 */
public interface CtInterface<T> extends CtType<T>, CtStatement {
	@Override
	CtInterface<T> clone();

	@Override
	@UnsettableProperty
	<C extends CtType<T>> C setSuperclass(CtTypeReference<?> superClass);

	@Override
	@UnsettableProperty
	String getLabel();
}
