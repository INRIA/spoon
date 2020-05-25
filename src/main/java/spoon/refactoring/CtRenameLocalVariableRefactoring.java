/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring;

import java.util.Collection;
import java.util.regex.Pattern;

import spoon.SpoonException;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQueryable;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;
import spoon.reflect.visitor.filter.LocalVariableReferenceFunction;
import spoon.reflect.visitor.filter.LocalVariableScopeFunction;
import spoon.reflect.visitor.filter.PotentialVariableDeclarationFunction;
import spoon.reflect.visitor.filter.SiblingsFunction;
import spoon.reflect.visitor.filter.SiblingsFunction.Mode;
import spoon.reflect.visitor.filter.VariableReferenceFunction;

/**
 * Spoon model refactoring function which renames `target` local variable to `newName`<br>
 * This refactoring will throw {@link RefactoringException} if the model would be not consistent after rename to new name.
 * The exception is thrown before the model modificatons are started.
 * <pre>
 * CtLocalVariable anLocalVariable = ...
 * RenameLocalVariableRefactor refactor = new RenameLocalVariableRefactor();
 * refactor.setTarget(anLocalVariable);
 * refactor.setNewName("someNewName");
 * try {
 *   refactor.refactor();
 * } catch (RefactoringException e) {
 *   //handle name conflict or name shadowing problem
 * }
 * </pre>
 */
public class CtRenameLocalVariableRefactoring extends AbstractRenameRefactoring<CtLocalVariable<?>> {

	public static final Pattern validVariableNameRE = javaIdentifierRE;

	public CtRenameLocalVariableRefactoring() {
		super(validVariableNameRE);
	}

	@Override
	protected void refactorNoCheck() {
		getTarget().map(new VariableReferenceFunction()).forEach(new CtConsumer<CtReference>() {
			@Override
			public void accept(CtReference t) {
				t.setSimpleName(newName);
			}
		});
		target.setSimpleName(newName);
	}

	private static class QueryDriver implements CtScannerListener {
		int nrOfNestedLocalClasses = 0;
		CtElement ignoredParent;

		@Override
		public ScanningMode enter(CtElement element) {
			if (ignoredParent != null && element != null && element.hasParent(ignoredParent)) {
				return ScanningMode.SKIP_ALL;
			}
			if (element instanceof CtType) {
				nrOfNestedLocalClasses++;
			}
			return ScanningMode.NORMAL;
		}

		@Override
		public void exit(CtElement element) {
			if (ignoredParent == element) {
				//we are living scope of ignored parent. We can stop checking it
				ignoredParent = null;
			}
			if (element instanceof CtType) {
				nrOfNestedLocalClasses--;
			}
		}

		public void ignoreChildrenOf(CtElement element) {
			if (ignoredParent != null) {
				throw new SpoonException("Unexpected state. The ignoredParent is already set");
			}
			ignoredParent = element;
		}

		public boolean isInContextOfLocalClass() {
			return nrOfNestedLocalClasses > 0;
		}
	}

	@Override
	protected void detectNameConflicts() {
		/*
		 * There can be these conflicts
		 * 1) target variable would shadow before declared variable (parameter, localVariable, catchVariable)
		 * --------------------------------------------------------------------------------------------------
		 */
		PotentialVariableDeclarationFunction potentialDeclarationFnc = new PotentialVariableDeclarationFunction(newName);
		CtVariable<?> var = getTarget().map(potentialDeclarationFnc).first();
		if (var != null) {
			if (var instanceof CtField) {
				/*
				 * we have found a field of same name.
				 * It is not problem, because variables can hide field declaration.
				 * Do nothing - OK
				 */
			} else if (potentialDeclarationFnc.isTypeOnTheWay()) {
				/*
				 * There is a local class declaration between future variable reference and variable declaration `var`.
				 * The found variable declaration `var` can be hidden by target variable with newName
				 * as long as there is no reference to `var` in visibility scope of the target variable.
				 * So search for such `var` reference now
				 */
				CtVariableReference<?> shadowedVar = target
						.map(new SiblingsFunction().includingSelf(true).mode(Mode.NEXT))
						.map(new VariableReferenceFunction(var)).first();
				if (shadowedVar != null) {
					//found variable reference, which would be shadowed by variable after rename.
					createNameConflictIssue(var, shadowedVar);
				} else {
					/*
					 * there is no local variable reference, which would be shadowed by variable after rename.
					 * OK
					 */
				}
			} else {
				/*
				 * the found variable is in conflict with target variable with newName
				 */
				createNameConflictIssue(var);
			}
		}
		/*
		 * 2) target variable is shadowed by later declared variable
		 * ---------------------------------------------------------
		 */
		final QueryDriver queryDriver = new QueryDriver();
		getTarget().map(new LocalVariableScopeFunction(queryDriver)).select(new Filter<CtElement>() {
			/**
			 * return true for all CtVariables, which are in conflict
			 */
			@Override
			public boolean matches(CtElement element) {
				if (element instanceof CtType<?>) {
					CtType<?> localClass = (CtType<?>) element;
					//TODO use faster hasField, implemented using map(new AllFieldsFunction()).select(new NameFilter(newName)).first()!=null
					Collection<CtFieldReference<?>> fields = localClass.getAllFields();
					for (CtFieldReference<?> fieldRef : fields) {
						if (newName.equals(fieldRef.getSimpleName())) {
							/*
							 * we have found a local class field, which will shadow input local variable if it's reference is in visibility scope of that field.
							 * Search for target variable reference in visibility scope of this field.
							 * If found than we cannot rename target variable to newName, because that reference would be shadowed
							 */
							queryDriver.ignoreChildrenOf(element);
							CtLocalVariableReference<?> shadowedVar = element.map(new LocalVariableReferenceFunction(target)).first();
							if (shadowedVar != null) {
								createNameConflictIssue(fieldRef.getFieldDeclaration(), shadowedVar);
								return true;
							}
							return false;
						}
					}
					return false;
				}
				if (element instanceof CtVariable<?>) {
					CtVariable<?> variable = (CtVariable<?>) element;
					if (newName.equals(variable.getSimpleName()) == false) {
						//the variable with different name. Ignore it
						return false;
					}
					//we have found a variable with new name
					if (variable instanceof CtField) {
						throw new SpoonException("This should not happen. The children of local class which contains a field with new name should be skipped!");
					}
					if (variable instanceof CtCatchVariable || variable instanceof CtLocalVariable || variable instanceof CtParameter) {
						/*
						 * we have found a catch variable or local variable or parameter with new name.
						 */
						if (queryDriver.isInContextOfLocalClass()) {
							/*
							 * We are in context of local class.
							 * This variable would shadow input local variable after rename
							 * so we cannot rename if there exist a local variable reference in variable visibility scope.
							 */
							queryDriver.ignoreChildrenOf(variable.getParent());
							CtQueryable searchScope;
							if (variable instanceof CtLocalVariable) {
								searchScope = variable.map(new SiblingsFunction().includingSelf(true).mode(Mode.NEXT));
							} else {
								searchScope = variable.getParent();
							}

							CtLocalVariableReference<?> shadowedVar = searchScope.map(new LocalVariableReferenceFunction(target)).first();
							if (shadowedVar != null) {
								//found local variable reference, which would be shadowed by variable after rename.
								createNameConflictIssue(variable, shadowedVar);
								return true;
							}
							//there is no local variable reference, which would be shadowed by variable after rename.
							return false;
						} else {
							/*
							 * We are not in context of local class.
							 * So this variable is in conflict. Return it
							 */
							createNameConflictIssue(variable);
							return true;
						}
					} else {
						//CtField should not be there, because the children of local class which contains a field with new name should be skipped!
						//Any new variable type???
						throw new SpoonException("Unexpected variable " + variable.getClass().getName());
					}
				}
				return false;
			}
		}).first();
	}

	/**
	 * Override this method to get access to details about this refactoring issue
	 * @param conflictVar - variable which would be in conflict with the `targetVariable` after it's rename to new name
	 */
	protected void createNameConflictIssue(CtVariable<?> conflictVar) {
		throw new RefactoringException(conflictVar.getClass().getSimpleName() + " with name " + conflictVar.getSimpleName() + " is in conflict.");
	}
	/**
	 * Override this method to get access to details about this refactoring issue
	 * @param conflictVar - variable which would shadow reference to `targetVariable` after it's rename to new name
	 * @param shadowedVarRef - the reference to `targetVariable`, which would be shadowed by `conflictVar`
	 */
	protected void createNameConflictIssue(CtVariable<?> conflictVar, CtVariableReference<?> shadowedVarRef) {
		throw new RefactoringException(conflictVar.getClass().getSimpleName() + " with name " + conflictVar.getSimpleName() + " would shadow local variable reference.");
	}
}
