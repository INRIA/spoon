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
