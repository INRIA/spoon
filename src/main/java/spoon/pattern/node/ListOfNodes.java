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
package spoon.pattern.node;

import java.util.List;
import java.util.function.BiConsumer;

import spoon.pattern.Generator;
import spoon.pattern.ResultHolder;
import spoon.pattern.matcher.ChainOfMatchersImpl;
import spoon.pattern.matcher.Matchers;
import spoon.pattern.matcher.TobeMatched;
import spoon.pattern.parameter.ParameterInfo;
import spoon.pattern.parameter.ParameterValueProvider;

/**
 * List of {@link RootNode}s. The {@link RootNode}s are processed in same order like they were inserted in the list
 */
public class ListOfNodes extends AbstractNode {
	protected List<RootNode> nodes;

	public ListOfNodes(List<RootNode> nodes) {
		super();
		this.nodes = nodes;
	}

	@Override
	public void forEachParameterInfo(BiConsumer<ParameterInfo, RootNode> consumer) {
		for (RootNode node : nodes) {
			node.forEachParameterInfo(consumer);
		}
	}

	@Override
	public <T> void generateTargets(Generator generator, ResultHolder<T> result, ParameterValueProvider parameters) {
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
