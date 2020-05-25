/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.node;

import spoon.pattern.Quantifier;
import spoon.pattern.internal.DefaultGenerator;
import spoon.pattern.internal.ResultHolder;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.reflect.declaration.CtElement;
import spoon.support.util.ImmutableMap;

import java.util.function.BiConsumer;

/**
 * Represents pattern model variable
 * Delivers/Matches 0, 1 or more values of defined parameter.
 * The values may have type which extends {@link CtElement} or any other type of some SpoonModel attribute. E.g. String
 */
public class ParameterNode extends AbstractPrimitiveMatcher {
	private final ParameterInfo parameterInfo;

	public ParameterNode(ParameterInfo parameterInfo) {
		this.parameterInfo = parameterInfo;
	}

	@Override
	public boolean replaceNode(RootNode oldNode, RootNode newNode) {
		return false;
	}

	@Override
	public <T> void generateTargets(DefaultGenerator generator, ResultHolder<T> result, ImmutableMap parameters) {
		generator.getValueAs(parameterInfo, result, parameters);
	}

	@Override
	public ImmutableMap matchTarget(Object target, ImmutableMap parameters) {
		return parameterInfo.addValueAs(parameters, target);
	}

	public ParameterInfo getParameterInfo() {
		return parameterInfo;
	}

	@Override
	public boolean isRepeatable() {
		return parameterInfo.isRepeatable();
	}

	@Override
	public boolean isMandatory(ImmutableMap parameters) {
		return parameterInfo.isMandatory(parameters);
	}

	@Override
	public boolean isTryNextMatch(ImmutableMap parameters) {
		return parameterInfo.isTryNextMatch(parameters);
	}

	@Override
	public Quantifier getMatchingStrategy() {
		return parameterInfo.getMatchingStrategy();
	}

	@Override
	public void forEachParameterInfo(BiConsumer<ParameterInfo, RootNode> consumer) {
		consumer.accept(parameterInfo, this);
	}

	@Override
	public String toString() {
		return "${" + parameterInfo + "}";
	}
}
