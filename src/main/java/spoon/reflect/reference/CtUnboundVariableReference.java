/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.reference;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.support.UnsettableProperty;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * This interface defines a reference to an unbound
 * {@link spoon.reflect.declaration.CtVariable}.
 */
public interface CtUnboundVariableReference<T> extends CtVariableReference<T> {
	@Override
	CtUnboundVariableReference<T> clone();

	@Override
	@UnsettableProperty
	<E extends CtElement> E setAnnotations(List<CtAnnotation<? extends Annotation>> annotation);
}
