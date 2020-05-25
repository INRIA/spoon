/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.chain;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

/**
 * Represents an object on which one can make queries.
 * It is implemented
 * <ol>
 * <li> by {@link CtElement} to allow creation of a new query on
 * children of an element.
 * <li> by {@link CtQuery} to allow reusable queries and chaining query steps.
 * </ol>
 *
 * The main methods are documented in CtQuery
 */
public interface CtQueryable {

	/**
	 * @see CtQuery#filterChildren(Filter)
	 */
	<R extends CtElement> CtQuery filterChildren(Filter<R> filter);

	/**
	 * @see CtQuery#map(CtFunction)
	 */
	<I, R> CtQuery map(CtFunction<I, R> function);

	/**
	 * @see CtQuery#map(CtConsumableFunction)
	 */
	<I> CtQuery map(CtConsumableFunction<I> queryStep);

}
