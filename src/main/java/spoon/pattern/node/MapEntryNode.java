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

import java.util.function.BiConsumer;

import spoon.pattern.Generator;
import spoon.pattern.ResultHolder;
import spoon.pattern.matcher.Matchers;
import spoon.pattern.matcher.TobeMatched;
import spoon.pattern.parameter.ParameterInfo;
import spoon.pattern.parameter.ParameterValueProvider;

/**
 * Represents a ValueResolver of one Map.Entry
 */
public class MapEntryNode implements Node {
	private final Node key;
	private final Node value;

	public MapEntryNode(Node key, Node value) {
		super();
		this.key = key;
		this.value = value;
	}

	public Node getKey() {
		return key;
	}
	public Node getValue() {
		return value;
	}

	@Override
	public boolean replaceNode(Node oldNode, Node newNode) {
		//TODO
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public TobeMatched matchTargets(TobeMatched targets, Matchers nextMatchers) {
		//TODO
		throw new UnsupportedOperationException("TODO");
	}
	@Override
	public void forEachParameterInfo(BiConsumer<ParameterInfo, Node> consumer) {
		// TODO
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public <T> void generateTargets(Generator generator, ResultHolder<T> result, ParameterValueProvider parameters) {
		// TODO
		throw new UnsupportedOperationException("TODO");
	}
}
