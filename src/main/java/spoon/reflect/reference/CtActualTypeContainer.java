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

import java.util.List;


/**
 * This interface defines the capability related to binding generics (aka type parameters).
 */
public interface CtActualTypeContainer {
	/**
	 * Gets the type arguments.
	 */
	@PropertyGetter(role = CtRole.TYPE_ARGUMENT)
	List<CtTypeReference<?>> getActualTypeArguments();

	/**
	 * Sets the type arguments.
	 */
	@PropertySetter(role = CtRole.TYPE_ARGUMENT)
	<T extends CtActualTypeContainer> T setActualTypeArguments(List<? extends CtTypeReference<?>> actualTypeArguments);

	/**
	 * Adds a type argument.
	 */
	<T extends CtActualTypeContainer> T addActualTypeArgument(CtTypeReference<?> actualTypeArgument);

	/**
	 * Removes a type argument.
	 */
	boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument);
}
