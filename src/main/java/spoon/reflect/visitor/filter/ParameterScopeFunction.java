/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
