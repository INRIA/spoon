/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtRHSReceiver;
import spoon.reflect.reference.CtFieldReference;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

/**
 * This element defines a field declaration.
 */
public interface CtField<T> extends CtVariable<T>, CtTypeMember, CtRHSReceiver<T>, CtShadowable {

	/**
	 * The separator for a string representation of a field.
	 */
	String FIELD_SEPARATOR = "#";

	/*
	 * (non-Javadoc)
	 *
	 * @see spoon.reflect.declaration.CtNamedElement#getReference()
	 */
	@Override
	@DerivedProperty
	CtFieldReference<T> getReference();

	/**
	 * Useful proxy to {@link #getDefaultExpression()}.
	 */
	@Override
	@DerivedProperty
	CtExpression<T> getAssignment();

	@Override
	@UnsettableProperty
	<U extends CtRHSReceiver<T>> U setAssignment(CtExpression<T> assignment);

	@Override
	CtField<T> clone();
}
