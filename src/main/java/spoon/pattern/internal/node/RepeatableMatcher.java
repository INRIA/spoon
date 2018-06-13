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
package spoon.pattern.internal.node;

import spoon.pattern.Quantifier;
import spoon.support.util.ImmutableMap;

/**
 * Defines API of a repeatable matcher.
 * It is kind of {@link RootNode}, where one {@link RootNode} may match 0, 1 or more `target` nodes.
 */
public interface RepeatableMatcher extends RootNode {
	/**
	 * If two {@link RepeatableMatcher}s in a list are matching the same element,
	 * then returned {@link Quantifier} defines how resolve this conflict
	 * @return {@link Quantifier}
	 */
	Quantifier getMatchingStrategy();

	/**
	 * @return true if this matcher can be applied more then once in the same container of targets
	 * Note: even if false, it may be applied again to another container and to match EQUAL value
	 */
	default boolean isRepeatable() {
		return false;
	}
	/**
	 * @param parameters matching parameters
	 * @return true if this ValueResolver MUST match with next target in the state defined by current `parameters`.
	 * false if match is optional
	 */
	default boolean isMandatory(ImmutableMap parameters) {
		return true;
	}
	/**
	 * @param parameters matching parameters
	 * @return true if this ValueResolver should be processed again to match next target in the state defined by current `parameters`.
	 */
	boolean isTryNextMatch(ImmutableMap parameters);
}
