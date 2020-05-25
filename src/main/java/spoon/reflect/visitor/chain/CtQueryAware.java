/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.chain;

/**
 * Expert-only capability interface so as to write advanced {@link CtFunction} and {@link spoon.reflect.visitor.Filter}
 * that need to access the state of the top-level {@link CtQuery} instance
 * containing the function to be evaluated.
 *
 * Not meant to be implemented directly, only in conjunction with
 * {@link CtConsumableFunction}, {@link CtFunction} or {@link spoon.reflect.visitor.Filter}.
 */
public interface CtQueryAware {
	/**
	 * This method is called when the filter/function is added as a step to a {@link CtQuery} by the query engine ({@link CtQueryImpl}).
	 * @param query an instance registering this function/filter.
	 */
	void setQuery(CtQuery query);
}
