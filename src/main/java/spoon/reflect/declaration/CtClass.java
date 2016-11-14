/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import spoon.reflect.code.CtStatement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;

import java.util.List;
import java.util.Set;

/**
 * This element represents a class declaration.
 *
 * <pre>
 *     // a class definition
 *     class Foo {
 *        int x;
 *     }
 * </pre>
 * @author Renaud Pawlak
 */
public interface CtClass<T extends Object> extends CtType<T>, CtStatement {
	/**
	 * Returns the anonymous blocks of this class.
	 * Derived from {@link #getTypeMembers()}
	 */
	@DerivedProperty
	List<CtAnonymousExecutable> getAnonymousExecutables();

	/**
	 * Returns the constructor of the class that takes the given argument types.
	 */
	CtConstructor<T> getConstructor(CtTypeReference<?>... parameterTypes);

	/**
	 * Returns the constructors of this class. This includes the default
	 * constructor if this class has no constructors explicitly declared.
	 *
	 * Derived from {@link #getTypeMembers()}
	 */
	@DerivedProperty
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
	 * Return {@code true} if the referenced type is a anonymous type
	 */
	boolean isAnonymous();

	@Override
	CtClass<T> clone();

	/**
	 * Creates an instance of this class.
	 *
	 * Requirements:
	 * - the class must have a default constructor.
	 * - All dependencies (superclass, super-interfaces, imports) must be in the classpath,
	 * because the code is actually compiled (otherwise an exception is thrown)
	 *
	 * If the class has super-interfaces, the object can be cast to one of them.
	 * Otherwise, if the class has no super-interfaces, the methods can only be called with reflection.
	 *
	 * This instance is meant to be used for quick-testing, it uses a throwable classloader that
	 * will be garbage-collected with the instance.
	 */
	T newInstance();
}
