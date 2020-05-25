/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.reference;

import spoon.reflect.declaration.CtTypeParameter;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import java.util.List;


/**
 * This interface defines a reference to a type parameter (aka generics).
 */
public interface CtTypeParameterReference extends CtTypeReference<Object> {

	/**
	 * A type parameter can have an <code>extends</code> clause which declare
	 * one ({@link CtTypeReference} or more ({@link CtIntersectionTypeReference} references.
	 * <pre>
	 *     // Extends with generics.
	 *     T extends Interface1
	 *     // Intersection type with generics.
	 *     T extends Interface1 &amp; Interface2
	 * </pre>
	 */
	@DerivedProperty
	CtTypeReference<?> getBoundingType();

	/**
	 * Returns the {@link CtTypeParameter}, a {@link CtTypeParameter}, that declares the type parameter
	 * referenced or <code>null</code> if the reference is not in a context where such type parameter is declared.
	 * See also {@link #getTypeParameterDeclaration()} which has a different semantic.
	 */
	@Override
	@DerivedProperty
	CtTypeParameter getDeclaration();

	// overriding the return type
	@Override
	CtTypeParameterReference clone();

	@Override
	@UnsettableProperty
	<T extends CtActualTypeContainer> T setActualTypeArguments(List<? extends CtTypeReference<?>> actualTypeArguments);

	/**
	 * Returns true if this has the default bounding type that is java.lang.Object (which basically means that there is no bound)
	 */
	@DerivedProperty
	boolean isDefaultBoundingType();
}
