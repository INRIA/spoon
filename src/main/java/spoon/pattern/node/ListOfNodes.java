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

import spoon.pattern.ResultHolder;
import spoon.pattern.matcher.ChainOfMatchersImpl;
import spoon.pattern.matcher.Matchers;
import spoon.pattern.matcher.TobeMatched;
import spoon.pattern.parameter.ParameterInfo;
import spoon.pattern.parameter.ParameterValueProvider;
import spoon.reflect.factory.Factory;

/**
 * List of {@link Node}s. The {@link Node}s are processed in same order like they were inserted in the list
 */
public class ListOfNodes implements Node {
	protected List<Node> nodes;

	public ListOfNodes(List<Node> nodes) {
		super();
		this.nodes = nodes;
	}

	@Override
	public void forEachParameterInfo(BiConsumer<ParameterInfo, Node> consumer) {
		for (Node node : nodes) {
			node.forEachParameterInfo(consumer);
		}
	}

	@Override
	public <T> void generateTargets(Factory factory, ResultHolder<T> result, ParameterValueProvider parameters) {
		for (Node node : nodes) {
			node.generateTargets(factory, result, parameters);
		}
	}

	@Override
	public TobeMatched matchTargets(TobeMatched targets, Matchers nextMatchers) {
		return ChainOfMatchersImpl.create(nodes, nextMatchers).matchAllWith(targets);
	}

	/**
	 * @param oldNode a {@link CtElement} whose {@link Node} we are looking for
	 * @return a {@link NodeContainer} of an {@link ElementNode}, whose {@link ElementNode#getTemplateNode()} == `element`
	 * null if element is not referred by any node of this {@link ListOfNodes}
	 */
	public boolean replaceNode(Node oldNode, Node newNode) {
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
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
	 * @return {@link List} of {@link Node}s of this {@link ListOfNodes}
	 */
	public List<Node> getNodes() {
		return nodes;
	}
}
