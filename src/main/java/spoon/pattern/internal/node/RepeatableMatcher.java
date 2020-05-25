/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
