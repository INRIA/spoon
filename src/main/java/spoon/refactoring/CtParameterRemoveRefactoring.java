/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.refactoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import spoon.SpoonException;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.visitor.CtAbstractVisitor;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.AllMethodsSameSignatureFunction;
import spoon.reflect.visitor.filter.ExecutableReferenceFilter;
import spoon.reflect.visitor.filter.ParameterReferenceFunction;

/**
 * Removes target {@link CtParameter} from the parent target {@link CtExecutable}
 * and from all overriding/overridden methods of related type hierarchies
 * and from all lambda expressions (if any) implementing the modified interface.
 * It removes arguments from all invocations of refactored executables too.<br>
 *
 * Before the refactoring is started it checks that:
 * <ul>
 * <li>to be removed parameter is NOT used in any refactored implementation
 * <li>to be removed argument contains read only expression, which can be safely removed
 * </ul>
 * If one of the validation constraints fails, then {@link RefactoringException} is thrown and nothing is changed.
 * You can override `#create*Issue(...)` methods to handle such exceptions individually.
 * <br>
 */
public class CtParameterRemoveRefactoring implements CtRefactoring {

	private CtParameter<?> target;
	private int parameterIndex;
	/**
	 * List of all {@link CtExecutable}s whose parameter has to be removed
	 */
	private List<CtExecutable<?>> targetExecutables;
	/**
	 * List of all {@link CtInvocation}s whose argument has to be removed
	 */
	private List<CtInvocation<?>> targetInvocations;

	public CtParameterRemoveRefactoring() {
	}

	/**
	 * @return the {@link CtParameter} which has to be removed by this refactoring function
	 */
	public CtParameter<?> getTarget() {
		return target;
	}

	/**
	 * @param target the {@link CtParameter} which has to be removed by this refactoring function
	 * @return this to support fluent API
	 */
	public CtParameterRemoveRefactoring setTarget(CtParameter<?> target) {
		if (this.target == target) {
			return this;
		}
		this.target = target;
		this.parameterIndex = target.getParent().getParameters().indexOf(target);
		targetExecutables = null;
		targetInvocations = null;
		return this;
	}

	/**
	 * @return computes and returns all executables, which will be modified by this refactoring
	 */
	public List<CtExecutable<?>> getTargetExecutables() {
		if (targetExecutables == null) {
			computeAllExecutables();
		}
		return targetExecutables;
	}

	/**
	 * @return computes and returns all invocations, which will be modified by this refactoring
	 */
	public List<CtInvocation<?>> getTargetInvocations() {
		if (targetInvocations == null) {
			computeAllInvocations();
		}
		return targetInvocations;
	}

	@Override
	public void refactor() {
		if (getTarget() == null) {
			throw new SpoonException("The target of refactoring is not defined");
		}
		detectIssues();
		refactorNoCheck();
	}

	/**
	 * validates whether this refactoring can be done without changing behavior of the refactored code.
	 */
	protected void detectIssues() {
		checkAllExecutables();
		checkAllInvocations();
	}

	/**
	 * search for all methods and lambdas which has to be refactored together with target method
	 */
	private void computeAllExecutables() {
		if (getTarget() == null) {
			throw new SpoonException("The target of refactoring is not defined");
		}
		final List<CtExecutable<?>> executables = new ArrayList<>();
		CtExecutable<?> targetExecutable = target.getParent();
		//all the executables, which belongs to same inheritance tree
		executables.add(targetExecutable);
		targetExecutable.map(new AllMethodsSameSignatureFunction()).forEach(new CtConsumer<CtExecutable<?>>() {
			@Override
			public void accept(CtExecutable<?> executable) {
				executables.add(executable);
			}
		});
		targetExecutables = Collections.unmodifiableList(executables);
	}

	/**
	 * search for all methods and lambdas which has to be refactored together with target method
	 */
	private void computeAllInvocations() {
		ExecutableReferenceFilter execRefFilter = new ExecutableReferenceFilter();
		for (CtExecutable<?> exec : getTargetExecutables()) {
			execRefFilter.addExecutable(exec);
		}
		//all the invocations, which belongs to same inheritance tree
		final List<CtInvocation<?>> invocations = new ArrayList<>();
		target.getFactory().getModel().filterChildren(execRefFilter).forEach(new CtConsumer<CtExecutableReference<?>>() {
			@Override
			public void accept(CtExecutableReference<?> t) {
				CtElement parent = t.getParent();
				if (parent instanceof CtInvocation<?>) {
					invocations.add((CtInvocation<?>) parent);
				} //else ignore other hits, which are not in context of invocation
			}
		});
		targetInvocations = Collections.unmodifiableList(invocations);
	}

	private void checkAllExecutables() {
		for (CtExecutable<?> executable : getTargetExecutables()) {
			checkExecutable(executable);
		}
	}

	private void checkExecutable(CtExecutable<?> executable) {
		final CtParameter<?> toBeRemovedParam = executable.getParameters().get(this.parameterIndex);
		toBeRemovedParam.map(new ParameterReferenceFunction()).forEach(new CtConsumer<CtParameterReference<?>>() {
			@Override
			public void accept(CtParameterReference<?> paramRef) {
				//some parameter uses are acceptable
				//e.g. parameter in invocation of super of method, which is going to be removed too.
				if (isAllowedParameterUsage(paramRef)) {
					return;
				}
				createParameterUsedIssue(toBeRemovedParam, paramRef);
			}
		});
	}

	private void checkAllInvocations() {
		for (CtInvocation<?> invocation : getTargetInvocations()) {
			checkInvocation(invocation);
		}
	}

	private void checkInvocation(CtInvocation<?> invocation) {
		final CtExpression<?> toBeRemovedExpression = invocation.getArguments().get(this.parameterIndex);
		if (canRemoveExpression(toBeRemovedExpression) == false) {
			createExpressionCannotBeRemovedIssue(invocation, toBeRemovedExpression);
		}
	}

	/**
	 * Detects whether found usage of removed parameter is acceptable
	 * @param paramRef the found reference to
	 * @return true if it is allowed parameter use
	 */
	protected boolean isAllowedParameterUsage(CtParameterReference<?> paramRef) {
		return isRemovedParamOfRefactoredInvocation(paramRef);
	}

	/**
	 * Detects whether `toBeRemovedExpression` can be safely removed during the refactoring
	 *
	 * @param toBeRemovedExpression the {@link CtExpression}, which will be removed by this refactoring
	 * @return true if the expression used to deliver argument of removed parameter can be removed
	 * false if cannot be removed and this refactoring has to be avoided.
	 */
	protected boolean canRemoveExpression(CtExpression<?> toBeRemovedExpression) {
		class Context {
			boolean canBeRemoved = false;
		}
		final Context context = new Context();
		toBeRemovedExpression.accept(new CtAbstractVisitor() {
			@Override
			public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
				context.canBeRemoved = true;
			}
			@Override
			public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {
				context.canBeRemoved = true;
			}
			@Override
			public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
				context.canBeRemoved = true;
			}
			@Override
			public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
				context.canBeRemoved = true;
			}
			@Override
			public <T> void visitCtLiteral(CtLiteral<T> literal) {
				context.canBeRemoved = true;
			}
			@Override
			public <T> void visitCtNewArray(CtNewArray<T> newArray) {
				context.canBeRemoved = true;
			}
			@Override
			public <T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess<T> annotationFieldAccess) {
				context.canBeRemoved = true;
			}
			@Override
			public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
				context.canBeRemoved = true;
			}
			//There are more expression which is save to remove. Including tree of unary/binary operators, conditional, etc.
			//It would be good to have a Filter, which matches read only expressions
		});
		return context.canBeRemoved;
	}

	protected boolean isRemovedParamOfRefactoredInvocation(CtParameterReference<?> paramRef) {
		CtInvocation<?> invocation = paramRef.getParent(CtInvocation.class);
		if (invocation == null) {
			return false;
		}
		return getTargetInvocations().contains(invocation);
	}

	/**
	 * Override this method to get access to details about this refactoring issue
	 * @param usedParameter to be removed parameter, which is used by `parameterUsage`
	 * @param parameterUsage the usage of parameter, which avoids it's remove
	 */
	protected void createParameterUsedIssue(CtParameter<?> usedParameter, CtParameterReference<?> parameterUsage) {
		throw new RefactoringException("The parameter " + usedParameter.getSimpleName()
		+ " cannot be removed because it is used (" + parameterUsage.getPosition() + ")");
	}

	/**
	 * Override this method to get access to details about this refactoring issue.
	 * @param toBeRemovedExpression is the expression which delivers value for the argument of the removed parameter,
	 * where {@link #canRemoveExpression(CtExpression)} returned false.
	 */
	protected void createExpressionCannotBeRemovedIssue(CtInvocation<?> invocation, CtExpression<?> toBeRemovedExpression) {
		throw new RefactoringException("The expression " + toBeRemovedExpression
		+ ", which creates argument of the to be removed parameter in invocation " + invocation + " cannot be removed."
		+ " Override method `canRemoveExpression` to customize this behavior.");
	}

	protected void refactorNoCheck() {
		removeInvocationArguments();
		removeMethodParameters();
	}

	protected void removeInvocationArguments() {
		List<CtInvocation<?>> invocations = getTargetInvocations();
		for (CtInvocation<?> invocation : invocations) {
			removeInvocationArgument(invocation);
		}
	}

	protected void removeInvocationArgument(CtInvocation<?> invocation) {
		invocation.removeArgument(invocation.getArguments().get(this.parameterIndex));
	}

	protected void removeMethodParameters() {
		List<CtExecutable<?>> executables = getTargetExecutables();
		for (CtExecutable<?> executable : executables) {
			removeParameter(executable);
		}
	}

	protected void removeParameter(CtExecutable<?> executable) {
		executable.removeParameter(executable.getParameters().get(this.parameterIndex));
	}
}
