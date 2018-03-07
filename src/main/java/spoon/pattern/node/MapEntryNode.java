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
public class MapEntryNode extends AbstractNode {
	private final RootNode key;
	private final RootNode value;

	public MapEntryNode(RootNode key, RootNode value) {
		super();
		this.key = key;
		this.value = value;
	}

	public RootNode getKey() {
		return key;
	}
	public RootNode getValue() {
		return value;
	}

	@Override
	public boolean replaceNode(RootNode oldNode, RootNode newNode) {
		//TODO
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public TobeMatched matchTargets(TobeMatched targets, Matchers nextMatchers) {
		//TODO
		throw new UnsupportedOperationException("TODO");
	}
	@Override
	public void forEachParameterInfo(BiConsumer<ParameterInfo, RootNode> consumer) {
		// TODO
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public <T> void generateTargets(Generator generator, ResultHolder<T> result, ParameterValueProvider parameters) {
		// TODO
		throw new UnsupportedOperationException("TODO");
	}
}
