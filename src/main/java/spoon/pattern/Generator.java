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

import java.util.List;

import spoon.pattern.node.RootNode;
import spoon.pattern.parameter.ParameterInfo;
import spoon.pattern.parameter.ParameterValueProvider;
import spoon.reflect.factory.Factory;

/**
 * Drives generation process
 */
public interface Generator {
	/**
	 * @return a {@link Factory}, which has to be used to generate instances
	 */
	Factory getFactory();
	/**
	 * Generates zero, one or more target depending on kind of this {@link RootNode}, expected `result` and input `parameters`
	 * @param factory TODO
	 */
	<T> void generateTargets(RootNode node, ResultHolder<T> result, ParameterValueProvider parameters);

	/**
	 * Returns zero, one or more values into `result`. The value comes from `parameters` from the location defined by `parameterInfo`
	 * @param parameterInfo
	 * @param result
	 * @param parameters
	 */
	<T> void getValueAs(ParameterInfo parameterInfo, ResultHolder<T> result, ParameterValueProvider parameters);

	/**
	 * Generates one target depending on kind of this {@link RootNode}, expected `expectedType` and input `parameters`
	 * @param factory TODO
	 * @param parameters {@link ParameterValueProvider}
	 * @param expectedType defines {@link Class} of returned value
	 *
	 * @return a generate value or null
	 */
	default <T> T generateTarget(RootNode node, ParameterValueProvider parameters, Class<T> expectedType) {
		ResultHolder.Single<T> result = new ResultHolder.Single<>(expectedType);
		generateTargets(node, result, parameters);
		return result.getResult();
	}

	/**
	 * Generates zero, one or more targets depending on kind of this {@link RootNode}, expected `expectedType` and input `parameters`
	 * @param factory TODO
	 * @param parameters {@link ParameterValueProvider}
	 * @param expectedType defines {@link Class} of returned value
	 *
	 * @return a {@link List} of generated targets
	 */
	default <T> List<T> generateTargets(RootNode node, ParameterValueProvider parameters, Class<T> expectedType) {
		ResultHolder.Multiple<T> result = new ResultHolder.Multiple<>(expectedType);
		generateTargets(node, result, parameters);
		return result.getResult();
	}
}
