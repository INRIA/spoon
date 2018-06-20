/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
