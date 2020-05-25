/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.path.impl;

import spoon.reflect.declaration.CtElement;

import java.util.Collection;

/**
 * A single path element from a CtPath.
 * <p>
 * Internal interface, not meant to be used by client code.
 *
 * @param <P> the type of the queried elements
 * @param <T> the type of the returned elements
 */
public interface CtPathElement<P extends CtElement, T extends CtElement> {

	/**
	 * Get elements childs of roots that match with this path.
	 */
	Collection<T> getElements(Collection<P> roots);

	/**
	 * Add a path argument.
	 *
	 * For instance, addArgument("index",3) will select only the third element in an indexed list.
	 */
	<C extends CtPathElement<P, T>> C addArgument(String key, String value);

}
