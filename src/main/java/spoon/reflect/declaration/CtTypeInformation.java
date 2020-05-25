/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;
import spoon.reflect.annotations.PropertyGetter;

import java.util.Collection;
import java.util.Set;

import static spoon.reflect.path.CtRole.INTERFACE;
import static spoon.reflect.path.CtRole.MODIFIER;
import static spoon.reflect.path.CtRole.SUPER_TYPE;

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
	@PropertyGetter(role = INTERFACE)
	Set<CtTypeReference<?>> getSuperInterfaces();

	/**
	 * Returns the fully qualified name of this type declaration.
	 */
	@DerivedProperty
	String getQualifiedName();

	/**
	 * Gets modifiers of this type.
	 */
	@PropertyGetter(role = MODIFIER)
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
	 * Returns true if this type is a class. Returns false for others (enum, interface, generics, annotation).
	 */
	boolean isClass();

	/**
	 * Returns true if this type is an interface.
	 */
	boolean isInterface();

	/**
	 * Returns true if this type is an enum.
	 */
	boolean isEnum();

	/**
	 * Returns true if this type is an annotation type.
	 */
	boolean isAnnotationType();

	/**
	 * Returns true if it is not a concrete, resolvable class, it if refers to a type parameter directly or indirectly.
	 * Direct: "T foo" isGenerics returns true.
	 * Indirect: List&lt;T&gt;, or Set&lt;List&lt;T&gt;&gt; isGenerics returns true
	 */
	@DerivedProperty
	boolean isGenerics();

	/**
	 * Returns true if it has any type parameter (generic or not).
	 */
	@DerivedProperty
	boolean isParameterized();

	/**
	 * Returns true if the referenced type is a sub-type of the given type.
	 * Returns true is type is self, it means: typeX.isSubtypeOf(typeX) is true too
	 */
	boolean isSubtypeOf(CtTypeReference<?> type);

	/**
	 * Returns the class type directly extended by this class.
	 *
	 * getSuperClass().getDeclaration()/getTypeDeclaration() returns the corresponding CtType (if in the source folder of Spoon).
	 *
	 * @return the class type directly extended by this class, or null if there
	 *         is none or if the super class is not in the classpath (in noclasspath mode)
	 */
	@PropertyGetter(role = SUPER_TYPE)
	CtTypeReference<?> getSuperclass();

	/**
	 * Gets the fields declared by this type.
	 */
	@DerivedProperty
	Collection<CtFieldReference<?>> getDeclaredFields();

	/**
	 * Gets the fields declared by this type and by all its supertypes if
	 * applicable.
	 */
	@DerivedProperty
	Collection<CtFieldReference<?>> getAllFields();

	/**
	 * Gets a field from its name.
	 *
	 * @return null if does not exit
	 */
	CtFieldReference<?> getDeclaredField(String name);

	/**
	 * Gets a field from this type or any super type or any implemented interface by field name.
	 *
	 * @return null if does not exit
	 */
	CtFieldReference<?> getDeclaredOrInheritedField(String fieldName);

	/**
	 * Gets the executables declared by this type if applicable.
	 */
	@DerivedProperty
	Collection<CtExecutableReference<?>> getDeclaredExecutables();

	/**
	 * Gets the executables declared by this type and by all its supertypes (static/instance methods, constructors, anonymous static blocks) if
	 * applicable. This method returns:
	 *
	 * <ul>
	 *     <li>static, instance and default methods</li>
	 *     <li>constructors</li>
	 * </ul>
	 *
	 * If a method is overridden twice in the hierarchy, it counts for two different elements.
	 * The method can be abstract.
	 */
	@DerivedProperty
	Collection<CtExecutableReference<?>> getAllExecutables();

	/**
	 * @return the type erasure, which is computed by the java compiler to ensure that no new classes are created for parametrized types so that generics incur no runtime overhead.
	 * See https://docs.oracle.com/javase/tutorial/java/generics/erasure.html
	 */
	@DerivedProperty
	CtTypeReference<?> getTypeErasure();


	/**
	 * @return true if this represents an array e.g. Object[] or int[]
	 */
	boolean isArray();
}
