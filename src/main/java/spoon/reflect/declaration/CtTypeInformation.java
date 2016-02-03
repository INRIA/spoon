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

import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;
import java.util.Set;

/**
 * Returns information that can be obtained both at compile-time and run-time
 *
 * For CtElement, the compile-time information is given
 *
 * For CtTypeReference, the runtime information is given (using the Reflection API)
 *
 */
public interface CtTypeInformation {
	/**
	 * Returns the interface types directly implemented by this class or
	 * extended by this interface.
	 */
	Set<CtTypeReference<?>> getSuperInterfaces();

	/**
	 * Returns the fully qualified name of this type declaration.
	 */
	String getQualifiedName();

	/**
	 * Gets modifiers of this type.
	 */
	Set<ModifierKind> getModifiers();

	/**
	 * Return {@code true} if the referenced type is a primitive type (int,
	 * double, boolean...).
	 */
	boolean isPrimitive();

	/**
	 * Return {@code true} if the referenced type is a anonymous type
	 */
	boolean isAnonymous();

	/**
	 * Return {@code true} if the referenced type is declared in an executable.
	 * e.g. a type declared in a method or a lambda.
	 *
	 * This corresponds to <code>isLocalClass</code> of <code>java.lang.Class</code>.
	 *
	 * <pre>
	 *     // Type declared in a method.
	 *     public void make() {
	 *         class Cook {
	 *         }
	 *     }
	 *     // Type declared in a lambda.
	 *     s -&gt; {
	 *         class Cook {
	 *         }
	 *     }
	 * </pre>
	 */
	boolean isLocalType();

	/**
	 * Returns true if this type is an interface.
	 */
	boolean isInterface();

	/**
	 * Returns true if the referenced type is a sub-type of the given type.
	 */
	boolean isSubtypeOf(CtTypeReference<?> type);

	/**
	 * Returns <code>true</code> if this referenced type is assignable from an
	 * instance of the given type.
	 */
	boolean isAssignableFrom(CtTypeReference<?> type);

	/**
	 * Returns the class type directly extended by this class.
	 *
	 * @return the class type directly extended by this class, or null if there
	 *         is none
	 */
	CtTypeReference<?> getSuperclass();

	/**
	 * Gets the fields declared by this type.
	 */
	Collection<CtFieldReference<?>> getDeclaredFields();

	/**
	 * Gets the fields declared by this type and by all its supertypes if
	 * applicable.
	 */
	Collection<CtFieldReference<?>> getAllFields();

	/**
	 * Gets the executables declared by this type if applicable.
	 */
	Collection<CtExecutableReference<?>> getDeclaredExecutables();

	/**
	 * Gets the executables declared by this type and by all its supertypes if
	 * applicable.
	 */
	Collection<CtExecutableReference<?>> getAllExecutables();

}
