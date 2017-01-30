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

import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;

/**
 * This Query expects a {@link CtLocalVariable} as input
 * and returns all {@link CtLocalVariableReference}s, which refers this input.
 * <br>
 * Usage:<br>
 * <pre> {@code
 * CtLocalVariable var = ...;
 * var
 *   .map(new LocalVariableReferenceFunction())
 *   .forEach((CtLocalVariableReference ref)->...process references...);
 * }
 * </pre>
 */
public class LocalVariableReferenceFunction implements CtConsumableFunction<CtLocalVariable<?>> {

	public LocalVariableReferenceFunction() {
	}

	@Override
	public void apply(CtLocalVariable<?> localVariable, CtConsumer<Object> outputConsumer) {
		localVariable
			.map(new LocalVariableScopeFunction())
			.select(new DirectReferenceFilter<CtLocalVariableReference<?>>(localVariable.getReference()))
			.forEach(outputConsumer);
	}
}
