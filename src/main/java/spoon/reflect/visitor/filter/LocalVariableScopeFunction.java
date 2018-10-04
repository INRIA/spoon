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
