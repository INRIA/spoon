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

import java.util.List;
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
	 *
	 * @return a List of all methods
	 */
	Set<CtMethod<?>> getAllMethods();

	/**
	 * Gets a method from its return type, name, and parameter types.
	 *
	 * @param <R> the method's return type
	 * @param name the name of the method
	 * @param returnType the return type reference
	 * @param parameterTypes the type references of the parameters
	 * 
	 * @return null if does not exit
	 */
	<R> CtMethod<R> getMethod(CtTypeReference<R> returnType, String name,
			CtTypeReference<?>... parameterTypes);

	/**
	 * Gets a method from its name and parameter types.
	 *
	 * @param <R> the method's return type
	 * @param name the name of the method
	 * @param parameterTypes the types of the parameters
	 * 
	 * @return null if does not exit
	 */
	<R> CtMethod<R> getMethod(String name, CtTypeReference<?>... parameterTypes);

	/**
	 * Returns the methods that are directly declared by this class or
	 * interface.
	 *
	 * @return the Set of methods directly declared by this type
	 */
	Set<CtMethod<?>> getMethods();

	/**
	 * Returns the methods that are directly declared by this class or
	 * interface and annotated with one of the given annotations.
	 *
	 * @param annotationTypes annotations to search for
	 *
	 * @return the Set of methods annotated by the given annotations
	 */
	Set<CtMethod<?>> getMethodsAnnotatedWith(CtTypeReference<?>... annotationTypes);

	/**
	 * Returns the methods that are directly declared by this class or
	 * interface and that have the given name.
	 *
	 * @param name the name to search for
	 *
	 * @return the List of methods matching the given name
	 */
	List<CtMethod<?>> getMethodsByName(String name);
	
	/**
	 * Returns the interface types directly implemented by this class or
	 * extended by this interface.
	 *
	 * @return the Set of super interface type references
	 */
	Set<CtTypeReference<?>> getSuperInterfaces();

	/**
	 * Sets the methods of this type.
	 *
	 * @param methods The Set of methods to set
	 */
	void setMethods(Set<CtMethod<?>> methods);

	/**
	 * Adds a method to this type.
	 *
	 * @param <M> the method's type
	 * @param method the method to add
	 *
	 * @return true if the method has been added
	 */
	<M> boolean addMethod(CtMethod<M> method);

	/**
	 * Removes a method from this type.
	 *
	 * @param <M> the method's type
	 * @param method the method to remove
	 *
	 * @return true of the method has been removed
	 */
	<M> boolean removeMethod(CtMethod<M> method);

	/**
	 * Sets the super interfaces of this type.
	 *
	 * @param interfaces A Set of interface type references to set
	 */
	void setSuperInterfaces(Set<CtTypeReference<?>> interfaces);

	/**
	 * Adds a super interface to this type
	 *
	 * @param <S> the type reference' type
	 * @param interfac the super interface type reference to add
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<S> boolean addSuperInterface(CtTypeReference<S> interfac);

	/**
	 * Removes a super interface from this type.
	 *
	 * @param <S> the type reference' type
	 * @param interfac the super interface type reference to remove
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<S> boolean removeSuperInterface(CtTypeReference<S> interfac);

	/**
	 * Tells if this type is a subtype of the given type.
	 *
	 * @param type the type to check against
	 *
	 * @return true if this type is a sub type of the given type
	 */
	boolean isSubtypeOf(CtTypeReference<?> type);

}