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

import spoon.support.UnsettableProperty;

import java.util.List;

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
	List<CtTypeReference<?>> getBounds();

	/**
	 * Sets the bounds of the intersection type.
	 */
	<C extends CtIntersectionTypeReference> C setBounds(List<CtTypeReference<?>> bounds);

	/**
	 * Adds a bound.
	 */
	<C extends CtIntersectionTypeReference> C addBound(CtTypeReference<?> bound);

	/**
	 * Removes a bound.
	 */
	boolean removeBound(CtTypeReference<?> bound);

	@Override
	CtIntersectionTypeReference<T> clone();

	@Override
	@UnsettableProperty
	<T extends CtActualTypeContainer> T setActualTypeArguments(List<? extends CtTypeReference<?>> actualTypeArguments);
}
