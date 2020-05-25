/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.processing;

/**
 * This enumeration defines the traversal strategies available for a processor.
 *
 * @see spoon.processing.Processor#getTraversalStrategy()
 */
public enum TraversalStrategy {

	/**
	 * When this strategy is selected, the processor will traverse the parent
	 * elements before the children. This implies that if the processor adds new
	 * elements to a given model sub-tree, these new elements will be processed
	 * too. This strategy can easily lead to complex processing behavior, so it
	 * is to be used with care.
	 */
	PRE_ORDER,

	/**
	 * When this strategy is selected, the processor will traverse the child
	 * elements before the parents. This implies that child elements that are
	 * removed by a processor will still be processed before they are removed.
	 */
	POST_ORDER

}
