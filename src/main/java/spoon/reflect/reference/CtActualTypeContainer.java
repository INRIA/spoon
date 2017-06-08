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

import spoon.support.PropertyGetter;
import spoon.support.PropertySetter;

import java.util.List;

import static spoon.reflect.path.CtRole.TYPE_PARAMETER;

/**
 * This interface defines the capability related to binding generics (aka type parameters).
 */
public interface CtActualTypeContainer {
	/**
	 * Gets the type arguments.
	 */
	@PropertyGetter(role = TYPE_PARAMETER)
	List<CtTypeReference<?>> getActualTypeArguments();

	/**
	 * Sets the type arguments.
	 */
	@PropertySetter(role = TYPE_PARAMETER)
	<T extends CtActualTypeContainer> T setActualTypeArguments(List<? extends CtTypeReference<?>> actualTypeArguments);

	/**
	 * Adds a type argument.
	 */
	@PropertySetter(role = TYPE_PARAMETER)
	<T extends CtActualTypeContainer> T addActualTypeArgument(CtTypeReference<?> actualTypeArgument);

	/**
	 * Removes a type argument.
	 */
	@PropertySetter(role = TYPE_PARAMETER)
	boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument);
}
