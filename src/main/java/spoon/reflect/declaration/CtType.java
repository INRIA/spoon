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

package spoon.reflect.declaration;

import java.util.Set;

import spoon.reflect.reference.CtTypeReference;

/**
 * This abstract element defines a super-type for classes and interfaces, which
 * can declare methods.
 */
public interface CtType<T> extends CtSimpleType<T>, CtGenericElement {

	/**
	 * Return all the accessible methods for this type (the recursion stops when
	 * the super-type is not in the model).
	 */
	Set<CtMethod<?>> getAllMethods();

	/**
	 * Gets a method from its return type, name, and parameter types.
	 * 
	 * @return null if does not exit
	 */
	<R> CtMethod<R> getMethod(CtTypeReference<R> returnType, String name,
			CtTypeReference<?>... parameterTypes);

	/**
	 * Gets a method from its name and parameter types.
	 * 
	 * @return null if does not exit
	 */
	CtMethod<?> getMethod(String name, CtTypeReference<?>... parameterTypes);

	/**
	 * Returns the methods that are directly declared by this class or
	 * interface.
	 */
	Set<CtMethod<?>> getMethods();

	/**
	 * Returns the interface types directly implemented by this class or
	 * extended by this interface.
	 */
	Set<CtTypeReference<?>> getSuperInterfaces();

	/**
	 * Sets the methods of this type.
	 */
	void setMethods(Set<CtMethod<?>> methods);

	/**
	 * Sets the super interfaces of this type.
	 */
	void setSuperInterfaces(Set<CtTypeReference<?>> interfaces);

	/**
	 * Tells if this type is a subtype of the given type.
	 */
	boolean isSubtypeOf(CtTypeReference<?> type);

}