/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.pattern.matcher;

import java.util.List;

import spoon.SpoonException;
import spoon.pattern.node.Node;

/**
 * Chain of {@link Node}s. {@link Node}s are processed in the same order as they were added into chain
 */
public class ChainOfMatchersImpl implements Matchers {
	private final Node firstMatcher;
	private final Matchers next;

	public static Matchers create(List<? extends Node> items, Matchers next) {
		return createFromList(next, items, 0);
	}
	public static Matchers create(Node firstNode, Matchers next) {
		return new ChainOfMatchersImpl(firstNode, next);
	}
	private static Matchers createFromList(Matchers next, List<? extends Node> items, int idx) {
		Node matcher;
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

	private ChainOfMatchersImpl(Node firstMatcher, Matchers next) {
		super();
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
