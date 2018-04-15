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
import spoon.pattern.matcher.Quantifier;
import spoon.pattern.parameter.ParameterInfo;
import spoon.reflect.declaration.CtElement;
import spoon.support.util.ParameterValueProvider;

/**
 * Represents pattern model variable
 * Delivers/Matches 0, 1 or more values of defined parameter.
 * The values may have type which extends {@link CtElement} or any other type of some SpoonModel attribute. E.g. String
 */
public class ParameterNode extends AbstractPrimitiveMatcher {
	private final ParameterInfo parameterInfo;

	public ParameterNode(ParameterInfo parameterInfo) {
		super();
		this.parameterInfo = parameterInfo;
	}

	@Override
	public boolean replaceNode(RootNode oldNode, RootNode newNode) {
		return false;
	}

	@Override
	public <T> void generateTargets(Generator generator, ResultHolder<T> result, ParameterValueProvider parameters) {
		generator.getValueAs(parameterInfo, result, parameters);
	}

	@Override
	public ParameterValueProvider matchTarget(Object target, ParameterValueProvider parameters) {
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
	public boolean isMandatory(ParameterValueProvider parameters) {
		return parameterInfo.isMandatory(parameters);
	}

	@Override
	public boolean isTryNextMatch(ParameterValueProvider parameters) {
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
