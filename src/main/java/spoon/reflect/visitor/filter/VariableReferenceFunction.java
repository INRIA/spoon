/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;


import spoon.SpoonException;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;

/**
 * The mapping function, accepting {@link CtVariable}
 * <ul>
 * <li>CtLocalVariable - local variable declared in body
 * <li>CtField - member field of an type
 * <li>CtParameter - method parameter
 * <li>CtCatchVariable - try - catch variable
 * </ul>
 * and returning all the {@link CtVariableReference}, which refers this variable
 */
public class VariableReferenceFunction implements CtConsumableFunction<CtElement> {

	protected final Visitor visitor = new Visitor();
	private final CtVariable<?> variable;
	protected CtConsumer<Object> outputConsumer;
	protected CtElement scope;

	public VariableReferenceFunction() {
		this.variable = null;
	}

	public VariableReferenceFunction(CtVariable<?> variable) {
		this.variable = variable;
	}

	@Override
	public void apply(CtElement variableOrScope, CtConsumer<Object> outputConsumer) {
		scope = variableOrScope;
		CtVariable<?> var = this.variable;
		if (var == null) {
			if (variableOrScope instanceof CtVariable<?>) {
				var = (CtVariable<?>) variableOrScope;
			} else {
				throw new SpoonException("The input of VariableReferenceFunction must be a CtVariable but is a " + variableOrScope.getClass().getSimpleName());
			}
		}
		this.outputConsumer = outputConsumer;
		var.accept(visitor);
	}

	protected class Visitor extends CtScanner {
		@Override
		protected void enter(CtElement e) {
			throw new SpoonException("Unsupported variable of type " + e.getClass().getName());
		}
		/**
		 * calls outputConsumer for each reference of the field
		 */
		@Override
		public <T> void visitCtField(CtField<T> field) {
			new FieldReferenceFunction((CtField<?>) variable).apply(scope, outputConsumer);
		}

		/**
		 * calls outputConsumer for each reference of the local variable
		 */
		@Override
		public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
			new LocalVariableReferenceFunction((CtLocalVariable<?>) variable).apply(scope, outputConsumer);
		}

		/**
		 * calls outputConsumer for each reference of the parameter
		 */
		@Override
		public <T> void visitCtParameter(CtParameter<T> parameter) {
			new ParameterReferenceFunction((CtParameter<?>) variable).apply(scope, outputConsumer);
		}

		/**
		 * calls outputConsumer for each reference of the catch variable
		 */
		@Override
		public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
			new CatchVariableReferenceFunction((CtCatchVariable<?>) variable).apply(scope, outputConsumer);
		}
	}
}
