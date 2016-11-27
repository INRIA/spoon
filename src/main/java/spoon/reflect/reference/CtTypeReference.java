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
package spoon.reflect.reference;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.support.DerivedProperty;
import spoon.support.SpoonClassNotFoundException;

import java.util.Set;

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
	 * Use {@link #getTypeDeclaration()} instead,
	 * in order to only stay in the Spoon world and manipulate CtType instead of java.lang.Class
	 *
	 * @return the Java class or null if the class is not found (not in
	 * classpath)
	 * @throws SpoonClassNotFoundException
	 * 		if the class is not in the classpath
	 */
	Class<T> getActualClass();

	/**
	 * Returns the {@link CtElement}, a {@link CtType}, that corresponds to the
	 * reference or <code>null</code> if the type declaration is not in the
	 * analyzed source files,
	 * {@link #getTypeDeclaration()} is a newer and better alternative that never returns null.
	 *
	 * @return the referenced element or <code>null</code> if the type
	 * declaration is not the analyzed source files.
	 */
	@DerivedProperty
	CtType<T> getDeclaration();

	/**
	 * Returns the {@link CtType} that corresponds to the reference even if the
	 * type isn't in the Spoon source path  (in this case, the Spoon elements are
	 * built with runtime reflection)
	 *
	 * @return the type declaration that corresponds to the reference.
	 */
	@DerivedProperty
	CtType<T> getTypeDeclaration();

	/**
	 * Gets the type that declares the referenced type.
	 *
	 * @return the declaring type if this references an inner class; null in
	 * other cases
	 */
	CtTypeReference<?> getDeclaringType();

	/**
	 * Gets the package of the referenced type.
	 *
	 * @return the declaring package or null if this if a inner class
	 */
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
	<C extends CtTypeReference<T>> C setDeclaringType(CtTypeReference<?> type);

	/**
	 * Sets the reference to the declaring package.
	 */
	<C extends CtTypeReference<T>> C setPackage(CtPackageReference pack);

	/**
	 * Replaces a type reference by another one.
	 */
	void replace(CtTypeReference<?> reference);

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

	/**
	 * Checks visibility based on public, protected, package protected and private modifiers of type
	 * @param type
	 * @return true if this can access that type
	 */
	boolean canAccess(CtTypeReference<?> type);

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
}
