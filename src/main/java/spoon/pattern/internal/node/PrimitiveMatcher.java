/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.node;

import spoon.support.util.ImmutableMap;

/**
 * Defines API of a primitive matcher - matcher for single target object
 */
public interface PrimitiveMatcher extends RepeatableMatcher {

	/**
	 * @param target - to be matched element
	 * @param parameters will receive the matching parameter values
	 * @return true if `element` matches with pattern of this matcher
	 */
	ImmutableMap matchTarget(Object target, ImmutableMap parameters);
}
