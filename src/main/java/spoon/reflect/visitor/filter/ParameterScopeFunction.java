/**
 * Copyright (C) 2006-2018 INRIA and contributors
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

import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtScannerListener;

/**
 * This Query expects a {@link CtParameter} as input
 * and returns all CtElements,
 * which are in visibility scope of that parameter.
 * In other words, it returns all elements,
 * which might be reference to that parameter.
 * <br>
 * It can be used to search for variable declarations or
 * variable references which might be in name conflict with input parameter.
 * <br>
 * Usage:<br>
 * <pre> {@code
 * CtParameter param = ...;
 * param.map(new ParameterScopeFunction()).forEach(...process result...);
 * }
 * </pre>
 */
public class ParameterScopeFunction implements CtConsumableFunction<CtParameter<?>> {
	private final CtScannerListener listener;

	public ParameterScopeFunction() {
		this.listener = null;
	}
	public ParameterScopeFunction(CtScannerListener queryListener) {
		this.listener = queryListener;
	}

	@Override
	public void apply(CtParameter<?> parameter, CtConsumer<Object> outputConsumer) {
		CtExecutable<?> exec = parameter.getParent(CtExecutable.class);
		if (exec == null) {
			//cannot search for parameter references of parameter which has no executable
			return;
		}
		exec
			.map(new CtScannerFunction().setListener(this.listener))
			.forEach(outputConsumer);
	}
}
