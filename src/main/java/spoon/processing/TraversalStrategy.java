/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
