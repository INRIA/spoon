/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;

/**
 * This interface defines a filter for program elements.
 *
 * @param <T>
 * 		the type of the filtered elements (an element belonging to the
 * 		filtered element must be assignable from <code>T</code>).
 */
public interface Filter<T extends CtElement> {
	/**
	 * Tells if the given element matches.
	 * @param element - the element to be checked for a match. Parameter element is never null if {@link Query} is used.
	 */
	boolean matches(T element);
}
