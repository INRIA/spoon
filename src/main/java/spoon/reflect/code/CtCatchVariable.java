/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.declaration.CtMultiTypedElement;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

/**
 * This code element defines an exception variable in a catch.
 *
 * @param <T>
 * 		type of the variable
 */
public interface CtCatchVariable<T> extends CtVariable<T>, CtMultiTypedElement, CtCodeElement {

	/*
	 * (non-Javadoc)
	 *
	 * @see spoon.reflect.declaration.CtNamedElement#getReference()
	 */
	@Override
	@DerivedProperty
	CtCatchVariableReference<T> getReference();

	@Override
	CtCatchVariable<T> clone();

	@Override
	@UnsettableProperty
	<C extends CtVariable<T>> C setDefaultExpression(CtExpression<T> assignedExpression);

	/**
	 * Returns type reference of the exception variable in a catch.
	 * If type is unknown, or any of the types in a multi-catch is unknown, returns null.
	 */
	@Override
	@DerivedProperty
	CtTypeReference<T> getType();

	@Override
	@UnsettableProperty
	<C extends CtTypedElement> C setType(CtTypeReference<T> type);
}
