/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.node;

import spoon.SpoonException;
import spoon.pattern.Quantifier;
import spoon.pattern.internal.DefaultGenerator;
import spoon.pattern.internal.ResultHolder;
import spoon.pattern.internal.matcher.TobeMatched;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.meta.ContainerKind;
import spoon.support.util.ImmutableMap;

import java.util.Map;
import java.util.function.BiConsumer;

import static spoon.pattern.internal.matcher.TobeMatched.getMatchedParameters;

/**
 * Represents a ValueResolver of one Map.Entry
 */
public class MapEntryNode extends AbstractPrimitiveMatcher {
	private RootNode key;
	private RootNode value;

	public MapEntryNode(RootNode key, RootNode value) {
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
		if (key == oldNode) {
			key = newNode;
			return true;
		}
		if (value == oldNode) {
			value = newNode;
			return true;
		}
		if (key.replaceNode(oldNode, newNode)) {
			return true;
		}
		return value.replaceNode(oldNode, newNode);
	}

	@Override
	public void forEachParameterInfo(BiConsumer<ParameterInfo, RootNode> consumer) {
		key.forEachParameterInfo(consumer);
		value.forEachParameterInfo(consumer);
	}

	private static class Entry implements Map.Entry<String, CtElement> {
		private final String key;
		private CtElement value;

		Entry(String key, CtElement value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public CtElement getValue() {
			return value;
		}

		@Override
		public CtElement setValue(CtElement value) {
			CtElement oldV = this.value;
			this.value = value;
			return oldV;
		}
	}

	@Override
	public <T> void generateTargets(DefaultGenerator generator, ResultHolder<T> result, ImmutableMap parameters) {
		String entryKey = generator.generateSingleTarget(key, parameters, String.class);
		CtElement entryValue = generator.generateSingleTarget(value, parameters, CtElement.class);
		if (entryKey != null && entryValue != null) {
			result.addResult((T) new Entry(entryKey, entryValue));
		}
	}
	@Override
	public ImmutableMap matchTarget(Object target, ImmutableMap parameters) {
		if (target instanceof Map.Entry) {
			Map.Entry<String, CtElement> targetEntry = (Map.Entry<String, CtElement>) target;
			parameters = getMatchedParameters(getKey().matchAllWith(TobeMatched.create(parameters, ContainerKind.SINGLE, targetEntry.getKey())));
			if (parameters == null) {
				return null;
			}
			return getMatchedParameters(getValue().matchAllWith(TobeMatched.create(parameters, ContainerKind.SINGLE, targetEntry.getValue())));
		}
		throw new SpoonException("Unexpected target type " + target.getClass().getName());
	}

	@Override
	public Quantifier getMatchingStrategy() {
		if (key instanceof ParameterNode) {
			return ((ParameterNode) key).getMatchingStrategy();
		}
		return Quantifier.POSSESSIVE;
	}

	@Override
	public boolean isTryNextMatch(ImmutableMap parameters) {
		if (key instanceof ParameterNode) {
			return ((ParameterNode) key).isTryNextMatch(parameters);
		}
		//it is not a parameterized node, so it matches only once
		return false;
	}
}
