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
package spoon.pattern;

import java.util.function.BiConsumer;

import spoon.reflect.factory.Factory;

/**
 * Generates/Matches a copy of single template object
 */
public class ConstantNode<T> extends AbstractPrimitiveMatcher {
	protected final T template;

	public ConstantNode(T template) {
		super();
		this.template = template;
	}

	public T getTemplateNode() {
		return template;
	}

	@Override
	public boolean replaceNode(Node oldNode, Node newNode) {
		return false;
	}

	@Override
	public void forEachParameterInfo(BiConsumer<ParameterInfo, Node> consumer) {
		//it has no parameters
	}

	@Override
	public <U> void generateTargets(Factory factory, ResultHolder<U> result, ParameterValueProvider parameters) {
		result.addResult((U) template);
	}

	@Override
	public ParameterValueProvider matchTarget(Object target, ParameterValueProvider parameters) {
		if (target == null && template == null) {
			return parameters;
		}
		if (target == null || template == null) {
			return null;
		}
		if (target.getClass() != template.getClass()) {
			return null;
		}
		return target.equals(template) ? parameters : null;
	}
}
