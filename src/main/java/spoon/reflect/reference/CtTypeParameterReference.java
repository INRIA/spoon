/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.path.CtRole;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import java.util.List;


/**
 * This interface defines a reference to a type parameter (aka generics).
 */
public interface CtTypeParameterReference extends CtTypeReference<Object> {

	/**
	 * Returns {@code true} if the bounds are in <code>extends</code> clause.
	 * {@code false} means a <code>super</code> clause.
	 */
	@PropertyGetter(role = CtRole.IS_UPPER)
	boolean isUpper();

	/**
	 * Sets the bounds (aka generics) of the referenced parameter.
	 *
	 * If you give null or an empty list, it'll clear bounds of the reference.
	 */
	<T extends CtTypeParameterReference> T setBounds(List<CtTypeReference<?>> bounds);

	/**
	 * Set to {@code true} to write <code>extends</code> clause for bounds types.
	 */
	@PropertySetter(role = CtRole.IS_UPPER)
	<T extends CtTypeParameterReference> T setUpper(boolean upper);

	/**
	 * Adds a bound.
	 */
	<T extends CtTypeParameterReference> T addBound(CtTypeReference<?> bound);

	/**
	 * Removes a bound.
	 */
	boolean removeBound(CtTypeReference<?> bound);

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
	CtTypeReference<?> getBoundingType();

	/**
	 * Sets the <code>extends</code> clause of the type parameter.
	 */
	<T extends CtTypeParameterReference> T setBoundingType(CtTypeReference<?> superType);

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
}
