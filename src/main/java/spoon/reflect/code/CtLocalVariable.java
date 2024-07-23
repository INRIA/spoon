/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
public interface CtLocalVariable<T> extends CtStatement, CtVariable<T>, CtRHSReceiver<T>, CtResource<T> {

	/**
	 * The {@link #getSimpleName() simple name} of a local variable that is
	 * <a href="https://openjdk.org/jeps/456">unnamed</a>.
	 */
	String UNNAMED_VARIABLE_NAME = "_";

	/*
	 * (non-Javadoc)
	 *
	 * @see spoon.reflect.declaration.CtNamedElement#getReference()
	 */
	/**
	 * {@inheritDoc}
	 * If the variable is <a href="https://openjdk.org/jeps/456">unnamed</a>, {@code null} is returned.
	 */
	@Override
	@DerivedProperty
	CtLocalVariableReference<T> getReference();

	/**
	 * {@return whether this local variable is <a href="https://openjdk.org/jeps/456">unnamed</a>}
	 */
	@DerivedProperty
	boolean isUnnamed();

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
