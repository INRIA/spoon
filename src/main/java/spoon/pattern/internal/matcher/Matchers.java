/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.matcher;

import spoon.pattern.internal.node.RootNode;

/**
 * A container of {@link RootNode}s.
 */
public interface Matchers {

	/**
	 * Matches all matchers of this {@link Matchers} instance with `targets`
	 * @param targets to be matched target nodes and input parameters
	 * @return {@link TobeMatched} with targets which remained after all {@link RootNode}s were matched + matched parameters
	 */
	TobeMatched matchAllWith(TobeMatched targets);
}
