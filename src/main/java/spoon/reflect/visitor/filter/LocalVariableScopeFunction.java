/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtScannerListener;

/**
 * This Query expects a {@link CtLocalVariable} as input
 * and returns all CtElements,
 * which are in visibility scope of that local variable.
 * In other words, it returns all elements,
 * which might be reference to that local variable.
 * <br>
 * It can be used to search for variable declarations or
 * variable references which might be in name conflict with input local variable.
 * <br>
 * Usage:<br>
 * <pre> {@code
 * CtLocalVariable var = ...;
 * var.map(new LocalVariableScopeFunction()).forEach(...process result...);
 * }
 * </pre>
 */
public class LocalVariableScopeFunction implements CtConsumableFunction<CtLocalVariable<?>> {
	private final CtScannerListener listener;

	public LocalVariableScopeFunction() {
		this.listener = null;
	}

	public LocalVariableScopeFunction(CtScannerListener queryListener) {
		this.listener = queryListener;
	}

	@Override
	public void apply(final CtLocalVariable<?> localVariable, CtConsumer<Object> outputConsumer) {
		localVariable
			.map(new SiblingsFunction().mode(SiblingsFunction.Mode.NEXT).includingSelf(true))
			.map(new CtScannerFunction().setListener(this.listener))
			.select(new Filter<CtElement>() {
				@Override
				public boolean matches(CtElement element) {
					//ignore localVariable itself
					return localVariable != element;
				}
			})
			.forEach(outputConsumer);
	}
}
