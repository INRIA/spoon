/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
	 */
	List<CtField<?>> getFields();

	/**
	 * Returns the anonymous blocks of this class.
	 */
	List<CtAnonymousExecutable> getAnonymousExecutables();

	/**
	 * Returns the constructor of the class that takes the given argument types.
	 */
	CtConstructor<T> getConstructor(CtTypeReference<?>... parameterTypes);

	/**
	 * Returns the constructors of this class. This includes the default
	 * constructor if this class has no constructors explicitly declared.
	 */
	Set<CtConstructor<T>> getConstructors();

	/**
	 * Sets the anonymous blocks of this class.
	 */
	<C extends CtClass<T>> C setAnonymousExecutables(List<CtAnonymousExecutable> e);

	/**
	 * Add an anonymous block to this class.
	 *
	 * @param e
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<C extends CtClass<T>> C addAnonymousExecutable(CtAnonymousExecutable e);

	/**
	 * Remove an anonymous block to this class.
	 *
	 * @param e
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean removeAnonymousExecutable(CtAnonymousExecutable e);

	/**
	 * Sets the constructors for this class.
	 */
	<C extends CtClass<T>> C setConstructors(Set<CtConstructor<T>> constructors);

	/**
	 * Adds a constructor to this class.
	 */
	<C extends CtClass<T>> C addConstructor(CtConstructor<T> constructor);

	/**
	 * Removes a constructor from this class.
	 */
	void removeConstructor(CtConstructor<T> constructor);

	/**
	 * Sets the superclass type.
	 */
	<C extends CtClass<T>> C setSuperclass(CtTypeReference<?> classType);

	/**
	 * Return {@code true} if the referenced type is a anonymous type
	 */
	boolean isAnonymous();
}
