/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
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
public class FieldReferenceFunction implements CtConsumableFunction<CtElement> {
	private final CtField<?> field;

	public FieldReferenceFunction() {
		this.field = null;
	}

	public FieldReferenceFunction(CtField<?> field) {
		this.field = field;
	}

	@Override
	public void apply(CtElement fieldOrScope, CtConsumer<Object> outputConsumer) {
		CtElement scope;
		CtField<?> field = this.field;
		if (field == null) {
			if (fieldOrScope instanceof CtField) {
				field = (CtField<?>) fieldOrScope;
			} else {
				throw new SpoonException("The input of FieldReferenceFunction must be a CtField but is " + fieldOrScope.getClass().getSimpleName());
			}
			scope = field.getFactory().getModel().getUnnamedModule();
		} else {
			scope = fieldOrScope;
		}
		scope
			.filterChildren(new DirectReferenceFilter<CtFieldReference<?>>(field.getReference()))
			.forEach(outputConsumer);
	}
}
