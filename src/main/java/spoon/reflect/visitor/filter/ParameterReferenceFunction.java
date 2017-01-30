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
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;

/**
 * This Query expects a {@link CtParameter} as input
 * and returns all {@link CtParameterReference}s, which refers this input.
 * <br>
 * Usage:<br>
 * <pre> {@code
 * CtParameter param = ...;
 * param
 *   .map(new ParameterReferenceFunction())
 *   .forEach((CtParameterReference ref)->...process references...);
 * }
 * </pre>
 */
public class ParameterReferenceFunction implements CtConsumableFunction<CtParameter<?>> {

	public ParameterReferenceFunction() {
	}

	@Override
	public void apply(CtParameter<?> parameter, CtConsumer<Object> outputConsumer) {
		parameter
			.map(new ParameterScopeFunction())
			.select(new DirectReferenceFilter<CtParameterReference<?>>(parameter.getReference()))
			.forEach(outputConsumer);
	}
}
