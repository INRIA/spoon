/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
