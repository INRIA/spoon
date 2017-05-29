/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.reflect.reference;

import spoon.reflect.declaration.CtExecutable;
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
	@DerivedProperty
	CtTypeReference<?> getDeclaringType();

	/**
	 * For methods, gets the return type of the executable (may be null in noclasspath mode).
	 * For constructors, gets the constructor class (which is also the return type of the contructor calls).
	 */
	CtTypeReference<T> getType();

	/**
	 * Gets parameters of the executable.
	 */
	List<CtTypeReference<?>> getParameters();

	/**
	 * Sets parameters of the executable.
	 */
	<C extends CtExecutableReference<T>> C setParameters(List<CtTypeReference<?>> parameters);

	/**
	 * Returns <code>true</code> if this executable overrides the given
	 * executable.
	 */
	boolean isOverriding(CtExecutableReference<?> executable);

	/**
	 * Returns the executable overridden by this one, if exists (null otherwise).
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
	boolean isStatic();

	/**
	 * Sets the declaring type.
	 */
	<C extends CtExecutableReference<T>> C setDeclaringType(CtTypeReference<?> declaringType);

	/**
	 * Sets this executable reference to be static or not.
	 */
	<C extends CtExecutableReference<T>> C setStatic(boolean b);

	/**
	 * Sets the type of the variable.
	 */
	<C extends CtExecutableReference<T>> C setType(CtTypeReference<T> type);

	/**
	 * Tells if the referenced executable is final.
	 */
	boolean isFinal();

	/**
	 * Gets the signature of this method or constructor as specified by chapter "8.4.2 Method Signature" of the Java specification
	 */
	String getSignature();

	/**
	 * Replaces an executable reference by another one.
	 */
	void replace(CtExecutableReference<?> reference);

	@Override
	CtExecutableReference<T> clone();
}
