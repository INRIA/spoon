/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

import java.util.List;

/**
 * This interface defines a reference to a
 * {@link spoon.reflect.declaration.CtTypeParameter}.
 */
public interface CtTypeParameterReference extends CtTypeReference<Object> {

	/**
	 * Gets the bounds (aka generics) of the referenced parameter.
	 */
	List<CtTypeReference<?>> getBounds();

	/**
	 * Returns {@code true} if the bounds are upper bounds.
	 */
	boolean isUpper();

	/**
	 * Sets the bounds (aka generics) of the referenced parameter.
	 */
	<T extends CtTypeParameterReference> T setBounds(List<CtTypeReference<?>> bounds);

	/**
	 * Set to {@code true} if the bounds are upper bounds.
	 */
	<T extends CtTypeParameterReference> T setUpper(boolean upper);

	/**
	 * Adds a bound.
	 */
	<T extends CtTypeParameterReference> T addBound(CtTypeReference<?> bound);

	/**
	 * Removes a bound.
	 */
	boolean removeBound(CtTypeReference<?> bound);

}
