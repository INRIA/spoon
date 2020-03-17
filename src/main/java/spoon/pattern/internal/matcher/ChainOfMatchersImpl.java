/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.matcher;

import java.util.List;

import spoon.SpoonException;
import spoon.pattern.internal.node.RootNode;

/**
 * Chain of {@link RootNode}s. {@link RootNode}s are processed in the same order as they were added into chain
 */
public class ChainOfMatchersImpl implements Matchers {
	private final RootNode firstMatcher;
	private final Matchers next;

	/**
	 * @param items
	 * @param next
	 * @return new {@link ChainOfMatchersImpl} which starts with items nodes and continues with `next` {@link Matchers}
	 */
	public static Matchers create(List<? extends RootNode> items, Matchers next) {
		return createFromList(next, items, 0);
	}
	private static Matchers createFromList(Matchers next, List<? extends RootNode> items, int idx) {
		RootNode matcher;
		while (true) {
			if (idx >= items.size()) {
				return next;
			}
			matcher = items.get(idx);
			if (matcher != null) {
				break;
			}
			idx++;
		}
		return new ChainOfMatchersImpl(matcher, createFromList(next, items, idx + 1));
	}

	private ChainOfMatchersImpl(RootNode firstMatcher, Matchers next) {
		if (firstMatcher == null) {
			throw new SpoonException("The firstMatcher Node MUST NOT be null");
		}
		this.firstMatcher = firstMatcher;
		if (next == null) {
			throw new SpoonException("The next Node MUST NOT be null");
		}
		this.next = next;
	}

	@Override
	public TobeMatched matchAllWith(TobeMatched targets) {
		return firstMatcher.matchTargets(targets, next);
	}
}
