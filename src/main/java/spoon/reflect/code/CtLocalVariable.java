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

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import static spoon.reflect.path.CtRole.IS_INFERRED;

/**
 * This code element defines a local variable definition (within an executable
 * body).
 *
 * Example:
 * <pre>
 *     // defines a local variable x
 *     int x = 0;
 * </pre>
 *
 * With Java 10, the local variable inference is now authorized, then the following code is valid too in a block scope:
 *
 * <pre>
 *     // local variable in Java 10
 *     var x = 0;
 * </pre>
 *
 * @param <T>
 * 		type of the variable
 * @see spoon.reflect.declaration.CtExecutable
 */
public interface CtLocalVariable<T> extends CtStatement, CtVariable<T>, CtRHSReceiver<T> {
	/*
	 * (non-Javadoc)
	 *
	 * @see spoon.reflect.declaration.CtNamedElement#getReference()
	 */
	@Override
	@DerivedProperty
	CtLocalVariableReference<T> getReference();

	/**
	 * Useful proxy to {@link #getDefaultExpression()}.
	 */
	@Override
	@DerivedProperty
	CtExpression<T> getAssignment();

	@Override
	CtLocalVariable<T> clone();

	@Override
	@UnsettableProperty
	<U extends CtRHSReceiver<T>> U setAssignment(CtExpression<T> assignment);

	/**
	 * Return true if this variable's type is not explicitely defined in the source code, but was using the `var` keyword of Java 10.
	 */
	@PropertyGetter(role = IS_INFERRED)
	boolean isInferred();

	/**
	 * Set true if the variable must be inferred.
	 * Warning: this method should only be used if compliance level is set to 10 or more.
	 */
	@PropertySetter(role = IS_INFERRED)
	<U extends CtLocalVariable<T>> U setInferred(boolean inferred);

}
