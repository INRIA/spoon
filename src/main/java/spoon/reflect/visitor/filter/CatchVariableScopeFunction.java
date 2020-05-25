/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtScannerListener;

/**
 * This Query expects a {@link CtCatchVariable} as input
 * and returns all CtElements,
 * which are in visibility scope of that catch variable.
 * In other words, it returns all elements,
 * which might be reference to that catch variable.
 * <br>
 * It can be used to search for variable declarations or
 * variable references which might be in name conflict with input catch variable.
 * <br>
 * Usage:<br>
 * <pre> {@code
 * CtCatchVariable var = ...;
 * var.map(new CatchVariableScopeFunction()).forEach(...process result...);
 * }
 * </pre>
 */
public class CatchVariableScopeFunction implements CtConsumableFunction<CtCatchVariable<?>> {
	private final CtScannerListener listener;

	public CatchVariableScopeFunction() {
		this.listener = null;
	}
	public CatchVariableScopeFunction(CtScannerListener queryListener) {
		this.listener = queryListener;
	}

	@Override
	public void apply(CtCatchVariable<?> catchVariable, CtConsumer<Object> outputConsumer) {
		catchVariable
			.getParent(CtCatch.class).getBody()
			.map(new CtScannerFunction().setListener(this.listener))
			.forEach(outputConsumer);
	}
}
