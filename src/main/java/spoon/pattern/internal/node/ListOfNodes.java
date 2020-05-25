/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.node;

import spoon.pattern.internal.DefaultGenerator;
import spoon.pattern.internal.ResultHolder;
import spoon.pattern.internal.matcher.ChainOfMatchersImpl;
import spoon.pattern.internal.matcher.Matchers;
import spoon.pattern.internal.matcher.TobeMatched;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.support.util.ImmutableMap;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * List of {@link RootNode}s. The {@link RootNode}s are processed in same order like they were inserted in the list
 */
public class ListOfNodes extends AbstractNode {
	protected List<RootNode> nodes;

	public ListOfNodes(List<RootNode> nodes) {
		this.nodes = nodes;
	}

	@Override
	public void forEachParameterInfo(BiConsumer<ParameterInfo, RootNode> consumer) {
		for (RootNode node : nodes) {
			node.forEachParameterInfo(consumer);
		}
	}

	@Override
	public <T> void generateTargets(DefaultGenerator generator, ResultHolder<T> result, ImmutableMap parameters) {
		for (RootNode node : nodes) {
			generator.generateTargets(node, result, parameters);
		}
	}

	@Override
	public TobeMatched matchTargets(TobeMatched targets, Matchers nextMatchers) {
		return ChainOfMatchersImpl.create(nodes, nextMatchers).matchAllWith(targets);
	}

	@Override
	public boolean replaceNode(RootNode oldNode, RootNode newNode) {
		for (int i = 0; i < nodes.size(); i++) {
			RootNode node = nodes.get(i);
			if (node == oldNode) {
				nodes.set(i, newNode);
				return true;
			}
			if (node.replaceNode(oldNode, newNode)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return {@link List} of {@link RootNode}s of this {@link ListOfNodes}
	 */
	public List<RootNode> getNodes() {
		return nodes;
	}
}
