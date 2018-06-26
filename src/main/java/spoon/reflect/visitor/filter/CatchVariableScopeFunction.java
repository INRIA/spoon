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
