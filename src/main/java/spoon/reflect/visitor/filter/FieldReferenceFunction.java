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

import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;

/**
 * This Query expects a {@link CtField} as input
 * and returns all {@link CtFieldReference}s, which refers this input.
 * <br>
 * Usage:<br>
 * <pre> {@code
 * CtField param = ...;
 * param
 *   .map(new FieldReferenceFunction())
 *   .forEach((CtFieldReference ref)->...process references...);
 * }
 * </pre>
 */
public class FieldReferenceFunction implements CtConsumableFunction<CtField<?>> {

	public FieldReferenceFunction() {
	}

	@Override
	public void apply(CtField<?> field, CtConsumer<Object> outputConsumer) {
		field.getFactory().getModel().getRootPackage()
			.filterChildren(new DirectReferenceFilter<CtFieldReference<?>>(field.getReference()))
			.forEach(outputConsumer);
	}
}
