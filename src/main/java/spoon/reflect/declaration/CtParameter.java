/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.code.CtExpression;
import spoon.reflect.reference.CtParameterReference;
import spoon.support.DerivedProperty;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.support.UnsettableProperty;

import static spoon.reflect.path.CtRole.IS_INFERRED;
import static spoon.reflect.path.CtRole.IS_VARARGS;

/**
 * This element defines an executable parameter declaration.
 *
 * @see CtExecutable
 */
public interface CtParameter<T> extends CtVariable<T>, CtShadowable {

	/**
	 * Gets the executable that is the parent declaration of this parameter
	 * declaration.
	 *
	 * (Overriding the return type)
	 */
	@Override
	@DerivedProperty
	CtExecutable<?> getParent();

	/**
	 * Returns <tt>true</tt> if this parameter accepts a variable number of
	 * arguments (must be the last parameter of
	 * {@link CtExecutable#getParameters()}).
	 */
	@PropertyGetter(role = IS_VARARGS)
	boolean isVarArgs();

	/**
	 * Sets this parameter to have varargs.
	 */
	@PropertySetter(role = IS_VARARGS)
	<C extends CtParameter<T>> C setVarArgs(boolean varArgs);

	/** overriding the return type */
	@Override
	@DerivedProperty
	CtParameterReference<T> getReference();

	@Override
	CtParameter<T> clone();

	@Override
	@UnsettableProperty
	<C extends CtVariable<T>> C setDefaultExpression(CtExpression<T> assignedExpression);

	/**
	 * {@return whether this parameter is <a href="https://openjdk.org/jeps/456">unnamed</a>}
	 * Unnamed parameters are always lambda parameters.
	 */
	@DerivedProperty
	boolean isUnnamed();

	/**
	 * Returns true if this parameter is a lambda parameter with type defined using the `var` keyword (since Java 11).
	 */
	@PropertyGetter(role = IS_INFERRED)
	boolean isInferred();

	/**
	 * Set to true if this parameter is a lambda parameter with type defined using the `var` keyword.
	 * Warning: this method should only be used if compliance level is set to 11 or more.
	 */
	@PropertySetter(role = IS_INFERRED)
	<U extends CtParameter<T>> U setInferred(boolean inferred);

}
