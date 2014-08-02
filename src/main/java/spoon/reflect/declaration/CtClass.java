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

import spoon.reflect.code.CtStatement;
import spoon.reflect.reference.CtTypeReference;

/**
 * This element represents a class declaration.
 * 
 * @author Renaud Pawlak
 */
public interface CtClass<T extends Object> extends CtType<T>, CtStatement {

	/**
	 * Gets the fields defined by this class.
	 *
	 * @return A List of all fields
	 */
	List<CtField<?>> getFields();

	/**
	 * Returns the anonymous blocks of this class.
	 *
	 * @return a List of anonymous executables
	 */
	List<CtAnonymousExecutable> getAnonymousExecutables();

	/**
	 * Returns the constructor of the class that takes the given argument types.
	 *
	 * @param parameterTypes the type references of the constructor parameters
	 *
	 * @return The constructor with a matching signature
	 */
	CtConstructor<T> getConstructor(CtTypeReference<?>... parameterTypes);

	/**
	 * Returns the constructors of this class. This includes the default
	 * constructor if this class has no constructors explicitly declared.
	 *
	 * @return a Set of constructors
	 */
	Set<CtConstructor<T>> getConstructors();

	/**
	 * Returns the class type directly extended by this class.
	 * 
	 * @return the class type directly extended by this class, or null if there
	 *         is none
	 */
	CtTypeReference<?> getSuperclass();

	/**
	 * Sets the anonymous blocks of this class.
	 *
	 * @param e the Set of  anonymous executables to set
	 */
	void setAnonymousExecutables(List<CtAnonymousExecutable> e);

	/**
	 * Add an anonymous block to this class.
	 * 
	 * @param e an anonymous executable to add
	 *
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean addAnonymousExecutable(CtAnonymousExecutable e);

	/**
	 * Remove an anonymous block to this class.
	 * 
	 * @param e an anonymous executable to remove
	 *
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean removeAnonymousExecutable(CtAnonymousExecutable e);

	/**
	 * Sets the constructors for this class.
	 *
	 * @param constructors the Set of constructors for this class
	 */
	void setConstructors(Set<CtConstructor<T>> constructors);

	/**
	 * Adds a constructor to this class.
	 *
	 * @param constructor the constructor to add
	 */
	void addConstructor(CtConstructor<T> constructor);

	/**
	 * Removes a constructor from this class.
	 *
	 * @param constructor the constructor to remove
	 */
	void removeConstructor(CtConstructor<T> constructor);

	/**
	 * Sets the superclass type.
	 *
	 * @param classType the type reference to set
	 */
	void setSuperclass(CtTypeReference<?> classType);

}
