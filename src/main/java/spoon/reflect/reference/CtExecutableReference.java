/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import spoon.reflect.declaration.CtExecutable;

/**
 * This interface defines a reference to a
 * {@link spoon.reflect.declaration.CtExecutable}. It can be a
 * {@link spoon.reflect.declaration.CtMethod} or a
 * {@link spoon.reflect.declaration.CtConstructor}.
 */
public interface CtExecutableReference<T> extends CtReference,
		CtGenericElementReference, CtModifiableReference {

	String CONSTRUCTOR_NAME = "<init>";
	
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

	CtExecutable<T> getDeclaration();

	/**
	 * Gets the reference to the type that declares this executable.
	 */
	CtTypeReference<?> getDeclaringType();

	/**
	 * Gets the list of the executable's parameter types.
	 */
	List<CtTypeReference<?>> getParameterTypes();

	/**
	 * Gets the type of the executable.
	 */
	CtTypeReference<T> getType();

	/**
	 * Returns <code>true</code> if this executable overrides the given
	 * executable.
	 */
	boolean isOverriding(CtExecutableReference<?> executable);

	/**
	 * Returns the executable overriden by this one, if exists (null
	 * otherwise).
	 */
	CtExecutableReference<?> getOverridingExecutable();

	/**
	 * Gets an overriding executable for this executable from a given subtype,
	 * if exists.
	 * 
	 * @param <S>
	 *            subtype of T
	 * @param subType
	 *            starting bottom type to find an overriding executable
	 *            (subtypes are not tested)
	 * @return the first found (most concrete) executable that overrides this
	 *         executable (null if none found)
	 */
	<S extends T> CtExecutableReference<S> getOverridingExecutable(
			CtTypeReference<?> subType);

	/**
	 * Tells if the referenced executable is static.
	 */
	boolean isStatic();

	/**
	 * Sets the declaring type.
	 */
	void setDeclaringType(CtTypeReference<?> declaringType);

	/**
	 * Sets the list of the executable's parameters types.
	 */
	void setParameterTypes(List<CtTypeReference<?>> parameterTypes);

	/**
	 * Sets this executable reference to be static or not.
	 */
	void setStatic(boolean b);

	/**
	 * Sets the type of the variable.
	 */
	void setType(CtTypeReference<T> type);

	/**
	 * Tells if the referenced executable is final.
	 */
	boolean isFinal();
}
