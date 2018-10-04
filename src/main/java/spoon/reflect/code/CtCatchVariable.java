/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
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
