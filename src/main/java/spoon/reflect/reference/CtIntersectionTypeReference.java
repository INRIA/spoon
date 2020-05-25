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

import java.util.List;

import static spoon.reflect.path.CtRole.BOUND;

/**
 * This interface defines a reference to an intersection type in generics or in casts.
 */
public interface CtIntersectionTypeReference<T> extends CtTypeReference<T> {
	/**
	 * Gets the bounds of the intersection type. Note that the first bound correspond to the current intersection type.
	 * <pre>
	 *     T extends Interface1 &amp; Interface2 // CtTypeParameterReference#getBoundingType == Interface1 and getBounds().get(0) == Interface1
	 * </pre>
	 */
	@PropertyGetter(role = BOUND)
	List<CtTypeReference<?>> getBounds();

	/**
	 * Sets the bounds of the intersection type.
	 */
	@PropertySetter(role = BOUND)
	<C extends CtIntersectionTypeReference> C setBounds(List<CtTypeReference<?>> bounds);

	/**
	 * Adds a bound.
	 */
	@PropertySetter(role = BOUND)
	<C extends CtIntersectionTypeReference> C addBound(CtTypeReference<?> bound);

	/**
	 * Removes a bound.
	 */
	@PropertySetter(role = BOUND)
	boolean removeBound(CtTypeReference<?> bound);

	@Override
	CtIntersectionTypeReference<T> clone();
}
