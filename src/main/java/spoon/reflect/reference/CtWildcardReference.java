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
import spoon.reflect.path.CtRole;
import spoon.support.UnsettableProperty;

/**
 * Represents a wildcard in generic type annotations, i.e. the "?" (e.g. the "?" in Collection&lt;?&gt; or Collection&lt;? extends List&gt;).
 */
public interface CtWildcardReference extends CtTypeParameterReference {
	@Override
	CtWildcardReference clone();

	@Override
	@UnsettableProperty
	<C extends CtReference> C setSimpleName(String simpleName);

	/**
	 * Returns {@code true} if the bounds are in <code>extends</code> clause.
	 * {@code false} means a <code>super</code> clause.
	 */
	@PropertyGetter(role = CtRole.IS_UPPER)
	boolean isUpper();

	/**
	 * Set to {@code true} to write <code>extends</code> clause for bounds types.
	 */
	@PropertySetter(role = CtRole.IS_UPPER)
	<T extends CtWildcardReference> T setUpper(boolean upper);

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
	@PropertyGetter(role = CtRole.BOUNDING_TYPE)
	@Override
	CtTypeReference<?> getBoundingType();

	/**
	 * Sets the <code>extends</code> clause of the type parameter.
	 */
	@PropertySetter(role = CtRole.BOUNDING_TYPE)
	<T extends CtWildcardReference> T setBoundingType(CtTypeReference<?> superType);
}
