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
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtAbstractVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;

/**
 * This mapping function expects a {@link CtLocalVariable} as input
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
public class LocalVariableReferenceFunction implements CtConsumableFunction<CtElement> {
	final CtVariable<?> targetVariable;
	final Class<?> variableClass;
	final Class<?> variableReferenceClass;

	public LocalVariableReferenceFunction() {
		this(CtLocalVariable.class, CtLocalVariableReference.class);
	}

	/**
	 * This constructor allows to define input local variable - the one for which this function will search for.
	 * In such case the input of mapping function represents the scope
	 * where this local variable is searched for.
	 * @param localVariable - the local variable declaration which is searched in scope of input element of this mapping function.
	 */
	public LocalVariableReferenceFunction(CtLocalVariable<?> localVariable) {
		this(CtLocalVariable.class, CtLocalVariableReference.class, localVariable);
	}

	LocalVariableReferenceFunction(Class<?> variableClass, Class<?> variableReferenceClass) {
		this.variableClass = variableClass;
		this.variableReferenceClass = variableReferenceClass;
		this.targetVariable = null;
	}

	LocalVariableReferenceFunction(Class<?> variableClass, Class<?> variableReferenceClass, CtVariable<?> variable) {
		this.variableClass = variableClass;
		this.variableReferenceClass = variableReferenceClass;
		this.targetVariable = variable;
	}

	@Override
	public void apply(final CtElement scope, CtConsumer<Object> outputConsumer) {
		CtVariable<?> var = targetVariable;
		if (var == null) {
			if (variableClass.isInstance(scope)) {
				var = (CtVariable<?>) scope;
			} else {
				throw new SpoonException("The input of " + getClass().getSimpleName() + " must be a " + variableClass.getSimpleName() + " but is " + scope.getClass().getSimpleName());
			}
		}
		final CtVariable<?> variable = var;
		final String simpleName = variable.getSimpleName();
		//the context which knows whether we are scanning in scope of local type or not
		final Context context = new Context();
		CtQuery scopeQuery;
		if (scope == variable) {
			//we are starting search from local variable declaration
			scopeQuery = createScopeQuery(variable, scope, context);
		} else {
			//we are starting search later, somewhere deep in scope of variable declaration
			final CtElement variableParent = variable.getParent();
			/*
			 * search in parents of searching scope for the variableParent
			 * 1) to check that scope is a child of variableParent
			 * 2) to detect if there is an local class between variable declaration and scope
			 */
			if (scope.map(new ParentFunction()).select(new Filter<CtElement>() {
				@Override
				public boolean matches(CtElement element) {
					if (element instanceof CtType) {
						//detected that the search scope is in local class declared in visibility scope of variable
						context.nrTypes++;
					}
					return variableParent == element;
				}
			}).first() == null) {
				//the scope is not under children of localVariable
				throw new SpoonException("Cannot search for references of variable in wrong scope.");
			}
			//search in all children of the scope element
			scopeQuery = scope.map(new CtScannerFunction().setListener(context));
		}
		scopeQuery.select(new Filter<CtElement>() {
				@Override
				public boolean matches(CtElement element) {
					if (variableReferenceClass.isInstance(element)) {
						CtVariableReference<?> varRef = (CtVariableReference<?>) element;
						if (simpleName.equals(varRef.getSimpleName())) {
							//we have found a variable reference of required type in visibility scope of targetVariable
							if (context.hasLocalType()) {
								//there exists a local type in visibility scope of this variable declaration
								//another variable declarations in scope of this local class may shadow input localVariable
								//so finally check that found variable reference is really a reference to target variable
								return variable == varRef.getDeclaration();
							}
							//else we can be sure that found reference is reference to variable
							return true;
						}
					}
					return false;
				}
			})
			.forEach(outputConsumer);
	}

	private static class Context implements CtScannerListener {
		int nrTypes = 0;

		@Override
		public ScanningMode enter(CtElement element) {
			if (element instanceof CtType) {
				nrTypes++;
			}
			return ScanningMode.NORMAL;
		}

		@Override
		public void exit(CtElement element) {
			if (element instanceof CtType) {
				nrTypes--;
			}
		}
		boolean hasLocalType() {
			return nrTypes > 0;
		}
	}

	private static final class QueryCreator extends CtAbstractVisitor {
		CtElement scope;
		CtScannerListener listener;
		CtQuery query;

		QueryCreator(CtElement scope, CtScannerListener listener) {
			this.scope = scope;
			this.listener = listener;
		}

		@Override
		public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
			query = scope.map(new LocalVariableScopeFunction(listener));
		}
		@Override
		public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
			query = scope.map(new CatchVariableScopeFunction(listener));
		}
		@Override
		public <T> void visitCtParameter(CtParameter<T> parameter) {
			query = scope.map(new ParameterScopeFunction(listener));
		}
	}

	private CtQuery createScopeQuery(CtVariable<?> variable, CtElement scope, Context context) {
		QueryCreator qc = new QueryCreator(scope, context);
		variable.accept(qc);
		if (qc.query == null) {
			throw new SpoonException("Unexpected type of variable: " + variable.getClass().getName());
		}
		return qc.query;
	}
}
