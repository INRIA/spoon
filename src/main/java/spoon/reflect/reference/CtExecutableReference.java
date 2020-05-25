/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.reference;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.path.CtRole;
import spoon.support.DerivedProperty;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;


/**
 * This interface defines a reference to a
 * {@link spoon.reflect.declaration.CtExecutable}. It can be a
 * {@link spoon.reflect.declaration.CtMethod} or a
 * {@link spoon.reflect.declaration.CtConstructor}.
 */
public interface CtExecutableReference<T> extends CtReference, CtActualTypeContainer {

	String CONSTRUCTOR_NAME = "<init>";

	String LAMBDA_NAME_PREFIX = "lambda$";

	String UNKNOWN_TYPE = "<unknown>";

	/**
	 * Tells if this is a reference to a constructor.
	 */
	boolean isConstructor();

	/**
	 * Gets the runtime method that corresponds to an executable reference if
	 * any.
	 *
	 * @return the method (null if not found)
	 */
	Method getActualMethod();

	/**
	 * Gets the runtime constructor that corresponds to an executable reference
	 * if any.
	 *
	 * @return the constructor (null if not found)
	 */
	Constructor<?> getActualConstructor();

	@Override
	@DerivedProperty
	CtExecutable<T> getDeclaration();

	/**
	 * Returns a subtype {@link CtExecutable} that corresponds to the reference
	 * even if its declaring type isn't in the Spoon source path  (in this case,
	 * the Spoon elements are built with runtime reflection).
	 *
	 * @return the executable declaration that corresponds to the reference.
	 */
	@DerivedProperty
	CtExecutable<T> getExecutableDeclaration();

	/**
	 * Gets the reference to the type that declares this executable.
	 */
	@PropertyGetter(role = CtRole.DECLARING_TYPE)
	CtTypeReference<?> getDeclaringType();

	/**
	 * For methods, gets the return type of the executable (may be null in noclasspath mode).
	 * For constructors, gets the constructor class (which is also the return type of the contructor calls).
	 */
	@PropertyGetter(role = CtRole.TYPE)
	CtTypeReference<T> getType();

	/**
	 * Gets parameters of the executable.
	 */
	@PropertyGetter(role = CtRole.ARGUMENT_TYPE)
	List<CtTypeReference<?>> getParameters();

	/**
	 * Sets parameters of the executable.
	 */
	@PropertySetter(role = CtRole.ARGUMENT_TYPE)
	<C extends CtExecutableReference<T>> C setParameters(List<CtTypeReference<?>> parameters);

	/**
	 * Returns <code>true</code> if this executable overrides the given
	 * executable.
	 */
	boolean isOverriding(CtExecutableReference<?> executable);

	/**
	 * Returns the method overridden by this one, if exists (null otherwise).
	 * The returned method is searched in the superclass hierarchy
	 * (and not in the super-interfaces).
	 * The returned method can be an abstract method from an abstract class, a super implementation, or even a method from Object.
	 */
	@DerivedProperty
	CtExecutableReference<?> getOverridingExecutable();

	/**
	 * Gets an overriding executable for this executable from a given subtype,
	 * if exists.
	 *
	 * @param <S>
	 * 		subtype of T
	 * @param subType
	 * 		starting bottom type to find an overriding executable
	 * 		(subtypes are not tested)
	 * @return the first found (most concrete) executable that overrides this
	 * executable (null if none found)
	 */
	<S extends T> CtExecutableReference<S> getOverridingExecutable(CtTypeReference<?> subType);

	/**
	 * Tells if the referenced executable is static.
	 */
	@PropertyGetter(role = CtRole.IS_STATIC)
	boolean isStatic();

	/**
	 * Sets the declaring type.
	 */
	@PropertySetter(role = CtRole.DECLARING_TYPE)
	<C extends CtExecutableReference<T>> C setDeclaringType(CtTypeReference<?> declaringType);

	/**
	 * Sets this executable reference to be static or not.
	 */
	@PropertySetter(role = CtRole.IS_STATIC)
	<C extends CtExecutableReference<T>> C setStatic(boolean b);

	/**
	 * Sets the type of the variable.
	 */
	@PropertySetter(role = CtRole.TYPE)
	<C extends CtExecutableReference<T>> C setType(CtTypeReference<T> type);

	/**
	 * Tells if the referenced executable is final.
	 */
	boolean isFinal();

	/**
	 * Gets the signature of this method or constructor, as explained in {@link spoon.reflect.declaration.CtMethod#getSignature()}.
	 */
	String getSignature();

	@Override
	CtExecutableReference<T> clone();
}
