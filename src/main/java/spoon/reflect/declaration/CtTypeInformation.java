/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
 * Returns information that can be obtained both at compile-time and run-time.
 *
 * For {@link CtElement}, the compile-time information is given.
 *
 * For {@link CtTypeReference}, the runtime information is given (using the Reflection API)
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
	 * Checks if the referenced type is a primitive type.
	 * <p>
	 * It is a primitive type, if it is one of the following types:
	 * <ul>
	 *     <li>byte</li>
	 *     <li>short</li>
	 *     <li>int</li>
	 *     <li>long</li>
	 *     <li>float</li>
	 *     <li>double</li>
	 *     <li>boolean</li>
	 *     <li>char</li>
	 *     <li>void</li>
	 * </ul>
	 * <p>
	 * For boxed types like {@link Integer} this method returns {@code false}.
	 *
	 * @return {@code true} if the referenced type is a primitive type
	 */
	boolean isPrimitive();

	/**
	 * Return {@code true} if the referenced type is an anonymous type
	 */
	boolean isAnonymous();

	/**
	 * Returns {@code true} if the referenced type is declared in an executable.
	 * e.g. a type declared in a method or a lambda.
	 *
	 * This corresponds to {@code isLocalClass} of {@code java.lang.Class}.
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
	 * Checks if this type is a subtype of the given type.
	 *
	 * @param type the type that might be a parent of this type.
	 * @return {@code true} if the referenced type is a subtype of the given type, otherwise {@code false}.
	 *         If this type is the same as the given type ({@code typeX.isSubtypeOf(typeX)}),
	 *         this method returns {@code true}.
	 */
	boolean isSubtypeOf(CtTypeReference<?> type);

	/**
	 * Returns a reference to the type directly extended by this type.
	 * <p>
	 * To get the {@link CtType} of the super class, use {@link CtTypeReference#getDeclaration()}
	 * or {@link CtTypeReference#getTypeDeclaration()} on the {@link CtTypeReference} returned by this method.
	 *
	 * @return the type explicitly extended by this type, or {@code null} if there
	 *         is none or if the super type is not in the classpath (in noclasspath mode).
	 *         If a class does not explicitly extend another class {@code null} is returned (<b>not</b> {@link Object}).
	 *         For types like enums that implicitly extend a superclass like {@link Enum}, this method returns
	 *         that class.
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
	 * @return a reference to the field with the name or {@code null} if it does not exist
	 */
	CtFieldReference<?> getDeclaredField(String name);

	/**
	 * Gets a field from this type or any super type or any implemented interface by field name.
	 *
	 * @return a reference to the field with the name or {@code null} if it does not exist
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
	 * This method returns a reference to the erased type.
	 * <p>
	 * For example, this will return {@code List} for {@code List<String>},
	 * or {@code Enum} for the type parameter {@code E} in the enum
	 * declaration.
	 *
	 * @return a reference to the erased type
	 * @see <a href="https://docs.oracle.com/javase/specs/jls/se20/html/jls-4.html#jls-4.6">Type Erasure</a>
	 */
	@DerivedProperty
	CtTypeReference<?> getTypeErasure();


	/**
	 * Returns true if this type represents an array like {@code Object[]} or {@code int[]}.
	 *
	 * @return true if this type represents an array like {@code Object[]} or {@code int[]}
	 */
	boolean isArray();
}
