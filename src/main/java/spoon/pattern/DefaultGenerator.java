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

import spoon.pattern.node.RootNode;
import spoon.pattern.parameter.ParameterInfo;
import spoon.pattern.parameter.ParameterValueProvider;
import spoon.reflect.factory.Factory;

/**
 * Drives generation process
 */
public class DefaultGenerator implements Generator {
	private final Factory factory;

	public DefaultGenerator(Factory factory) {
		super();
		this.factory = factory;
	}

	@Override
	public <T> void generateTargets(RootNode node, ResultHolder<T> result, ParameterValueProvider parameters) {
		node.generateTargets(this, result, parameters);
	}

	@Override
	public <T> void getValueAs(ParameterInfo parameterInfo, ResultHolder<T> result, ParameterValueProvider parameters) {
		parameterInfo.getValueAs(factory, result, parameters);
	}

	@Override
	public Factory getFactory() {
		return factory;
	}

}
