/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal;

import spoon.reflect.factory.Factory;

/**
 * Converts the individual parameter values to required type after substitution
 * Converts the matching model values to parameter values during matching process
 */
public interface ValueConvertor {
	<T> T getValueAs(Factory factory, String parameterName, Object value, Class<T> valueClass);
}
