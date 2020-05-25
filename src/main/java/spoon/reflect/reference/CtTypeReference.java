/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.reference;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.support.DerivedProperty;
import spoon.support.SpoonClassNotFoundException;

import java.util.Set;

import static spoon.reflect.path.CtRole.PACKAGE_REF;

/**
 * This interface defines a reference to a
 * {@link spoon.reflect.declaration.CtType} or sub-type.
 */
public interface CtTypeReference<T> extends CtReference, CtActualTypeContainer, CtTypeInformation, CtShadowable {

	/**
	 * The name of the null type ("&lt;nulltype&gt;").
	 */
	String NULL_TYPE_NAME = "<nulltype>";

	/**
	 * Special type used as a type argument when actual type arguments can't be inferred.
	 */
	String OMITTED_TYPE_ARG_NAME = "<omitted>";

	/**
	 * Returns the simple (unqualified) name of this element.
	 * Following the compilation convention, if the type is a local type,
	 * the name starts with a numeric prefix (e.g. local class Foo has simple name 1Foo).
	 */
	@Override
	String getSimpleName();

	/**
	 * Gets the Java runtime class of the referenced type.
	 *
	 * This is a low-level feature, it should never been used.
	 * For CtTypeReference, use {@link #getTypeDeclaration()} instead,
	 * in order to only stay in the Spoon world and manipulate CtType instead of java.lang.Class.
	 *
	 * @return the Java class or throws a {@link SpoonClassNotFoundException} if the class is not found.
	 * @throws SpoonClassNotFoundException if the class is not in the classpath
	 * @deprecated (Since Spoon 7.0.0) use {@link #getTypeDeclaration()} instead
	 */
	Class<T> getActualClass();

	/**
	 * Returns the {@link CtType}, that corresponds to the
	 * reference or <code>null</code> if the type declaration is not in the
	 * analyzed source files,
	 *
	 * {@link #getTypeDeclaration()} is a newer and better alternative that never returns null.
	 *
	 * @return the referenced element or <code>null</code> if the type
	 * declaration is not the analyzed source files.
	 */
	@Override
	@DerivedProperty
	CtType<T> getDeclaration();

	/**
	 * Returns the {@link CtType} that corresponds to the reference even if the
	 * type isn't in the Spoon source path  (in this case, the Spoon elements are
	 * built with runtime reflection, and the resulting CtType is called a "shadow" class,
	 * see {@link CtShadowable#isShadow()}).
	 *
	 * @return the type declaration that corresponds to the reference or null if the reference points to a class that is not in the classpath.
	 */
	@DerivedProperty
	CtType<T> getTypeDeclaration();

	/**
	 * Gets the type that declares the referenced type.
	 *
	 * @return the declaring type if this references an inner class; null in
	 * other cases
	 */
	@PropertyGetter(role = CtRole.DECLARING_TYPE)
	CtTypeReference<?> getDeclaringType();

	/**
	 * Gets the package of the referenced type.
	 *
	 * @return the declaring package or null if this if a inner class
	 */
	@PropertyGetter(role = PACKAGE_REF)
	CtPackageReference getPackage();

	/**
	 * Returns the corresponding non-primitive type for a primitive type (the
	 * same type otherwise).
	 */
	CtTypeReference<?> box();

	/**
	 * Returns the primitive type for a boxing type (unchanged if the type does
	 * not correspond to a boxing type).
	 */
	CtTypeReference<?> unbox();

	/**
	 * Sets the reference to the declaring type. Should be set to null if the
	 * referenced type is not a inner type.
	 */
	@PropertySetter(role = CtRole.DECLARING_TYPE)
	<C extends CtTypeReference<T>> C setDeclaringType(CtTypeReference<?> type);

	/**
	 * Sets the reference to the declaring package.
	 */
	@PropertySetter(role = PACKAGE_REF)
	<C extends CtTypeReference<T>> C setPackage(CtPackageReference pack);

	/**
	 * Casts the type reference in {@link CtIntersectionTypeReference}.
	 */
	CtIntersectionTypeReference<T> asCtIntersectionTypeReference();

	@Override
	CtTypeReference<T> clone();

	@Override
	@DerivedProperty
	Set<CtTypeReference<?>> getSuperInterfaces();

	@Override
	@DerivedProperty
	CtTypeReference<?> getSuperclass();

	@Override
	@DerivedProperty
	Set<ModifierKind> getModifiers();

	/**
	 * Checks visibility based on public, protected, package protected and private modifiers of type
	 * @param type
	 * @return true if this can access that type
	 */
	boolean canAccess(CtTypeReference<?> type);

	/**
	 * @return true if this type can access that the `typeMember` in another type based on public, protected, package protected and private modifiers.
	 */
	boolean canAccess(CtTypeMember typeMember);

	/**
	 * Returns this, or top level type of this, if this is an inner type
	 */
	@DerivedProperty
	CtTypeReference<?> getTopLevelType();

	/**
	 * Computes nearest access path parent to this type from the context of this type reference.
	 * The context is defined by this.getParent(CtType.class).
	 *
	 * Normally the declaring type can be used as access path. For example in this class hierarchy
	 * <pre>
	 * class A {
	 *    class B {
	 *       class C {}
	 *    }
	 * }
	 * </pre>
	 *
	 * The C.getAccessParentFrom(null) will return B, because B can be used to access C, using code like <code>B.C</code><br>
	 * But when some class (A or B) on the access path is not visible in type X, then we must found an alternative path.
	 * For example in case like, when A and B are invisible, e.g because of modifier <code>protected</code>:
	 * <pre>
	 * class D extends B {
	 * }
	 * class X extends D {
	 * 	 class F extends C
	 * }
	 * </pre>
	 * The C.getAccessParentFrom(X) will return D, because D can be used to access C in scope of X.
	 *
	 * @return type reference which can be used to access this type in scope of contextType.
	 */
	@DerivedProperty
	CtTypeReference<?> getAccessType();

	/**
	 * If this type reference is used as a type argument (see {@link #getActualTypeArguments()}), returns the type parameter declaration in the target type, returns null otherwise.
	 *
	 * In the following example, getTypeParameterDeclaration of "String" returns the type parameter definition "X".
	 * <pre>
	 * class Dog&lt;X&gt;{}
	 * Dog&lt;String&gt;var = ...;
	 * </pre>
	 **
	 * In this other example, getTypeParameterDeclaration of T in Dog&lt;T&gt; returns the type parameter definition "X" (while {@link #getDeclaration()} returns the "T" of Cat).
	 * <pre>
	 * class Dog&lt;X&gt;{}
	 * class Cat&lt;T&gt; {
	 * Dog&lt;T&gt; dog;
	 * }
	 * </pre>
	 */
	@DerivedProperty
	CtTypeParameter getTypeParameterDeclaration();

	/**
	 * @param isSimplyQualified false then the reference is printed fully qualified name.
	 * 		true then only the type name is printed.
	 */
	@DerivedProperty
	CtTypeReference<T> setSimplyQualified(boolean isSimplyQualified);

	/**
	 * @return false then fully qualified name is printed.
	 * 		true then type simple name is printed.
	 */
	@DerivedProperty
	boolean isSimplyQualified();
}
