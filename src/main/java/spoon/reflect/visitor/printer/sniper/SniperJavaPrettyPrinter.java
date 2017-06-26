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
package spoon.reflect.visitor.printer.sniper;

import spoon.compiler.Environment;
import spoon.experimental.modelobs.ActionBasedChangeListener;
import spoon.experimental.modelobs.action.Action;
import spoon.experimental.modelobs.action.AddAction;
import spoon.experimental.modelobs.action.DeleteAction;
import spoon.experimental.modelobs.action.DeleteAllAction;
import spoon.experimental.modelobs.action.UpdateAction;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLabelledFlowBreak;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtMultiTypedElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.printer.sniper.element.SniperCtAbstractInvocation;
import spoon.reflect.visitor.printer.sniper.element.SniperCtAnnotation;
import spoon.reflect.visitor.printer.sniper.element.SniperCtAnnotationFieldAccess;
import spoon.reflect.visitor.printer.sniper.element.SniperCtAnnotationMethod;
import spoon.reflect.visitor.printer.sniper.element.SniperCtArrayAccess;
import spoon.reflect.visitor.printer.sniper.element.SniperCtAssert;
import spoon.reflect.visitor.printer.sniper.element.SniperCtAssignment;
import spoon.reflect.visitor.printer.sniper.element.SniperCtBinaryOperator;
import spoon.reflect.visitor.printer.sniper.element.SniperCtBodyHolder;
import spoon.reflect.visitor.printer.sniper.element.SniperCtCase;
import spoon.reflect.visitor.printer.sniper.element.SniperCtCatch;
import spoon.reflect.visitor.printer.sniper.element.SniperCtClass;
import spoon.reflect.visitor.printer.sniper.element.SniperCtComment;
import spoon.reflect.visitor.printer.sniper.element.SniperCtConditional;
import spoon.reflect.visitor.printer.sniper.element.SniperCtConstructor;
import spoon.reflect.visitor.printer.sniper.element.SniperCtConstructorCall;
import spoon.reflect.visitor.printer.sniper.element.SniperCtDo;
import spoon.reflect.visitor.printer.sniper.element.SniperCtElement;
import spoon.reflect.visitor.printer.sniper.element.SniperCtEnum;
import spoon.reflect.visitor.printer.sniper.element.SniperCtExecutable;
import spoon.reflect.visitor.printer.sniper.element.SniperCtExecutableReferenceExpression;
import spoon.reflect.visitor.printer.sniper.element.SniperCtExpression;
import spoon.reflect.visitor.printer.sniper.element.SniperCtFieldAccess;
import spoon.reflect.visitor.printer.sniper.element.SniperCtFieldReference;
import spoon.reflect.visitor.printer.sniper.element.SniperCtFor;
import spoon.reflect.visitor.printer.sniper.element.SniperCtForEach;
import spoon.reflect.visitor.printer.sniper.element.SniperCtFormalTypeDeclarer;
import spoon.reflect.visitor.printer.sniper.element.SniperCtIf;
import spoon.reflect.visitor.printer.sniper.element.SniperCtIntersectionTypeReference;
import spoon.reflect.visitor.printer.sniper.element.SniperCtInvocation;
import spoon.reflect.visitor.printer.sniper.element.SniperCtJavaDoc;
import spoon.reflect.visitor.printer.sniper.element.SniperCtJavaDocTag;
import spoon.reflect.visitor.printer.sniper.element.SniperCtLabelledFlowBreak;
import spoon.reflect.visitor.printer.sniper.element.SniperCtLambda;
import spoon.reflect.visitor.printer.sniper.element.SniperCtLiteral;
import spoon.reflect.visitor.printer.sniper.element.SniperCtMethod;
import spoon.reflect.visitor.printer.sniper.element.SniperCtModifiable;
import spoon.reflect.visitor.printer.sniper.element.SniperCtMultiTypedElement;
import spoon.reflect.visitor.printer.sniper.element.SniperCtNamedElement;
import spoon.reflect.visitor.printer.sniper.element.SniperCtNewArray;
import spoon.reflect.visitor.printer.sniper.element.SniperCtNewClass;
import spoon.reflect.visitor.printer.sniper.element.SniperCtOperatorAssignment;
import spoon.reflect.visitor.printer.sniper.element.SniperCtPackage;
import spoon.reflect.visitor.printer.sniper.element.SniperCtParameter;
import spoon.reflect.visitor.printer.sniper.element.SniperCtRHSReceiver;
import spoon.reflect.visitor.printer.sniper.element.SniperCtReturn;
import spoon.reflect.visitor.printer.sniper.element.SniperCtStatement;
import spoon.reflect.visitor.printer.sniper.element.SniperCtStatementList;
import spoon.reflect.visitor.printer.sniper.element.SniperCtSwitch;
import spoon.reflect.visitor.printer.sniper.element.SniperCtSynchronized;
import spoon.reflect.visitor.printer.sniper.element.SniperCtTargetedExpression;
import spoon.reflect.visitor.printer.sniper.element.SniperCtThrow;
import spoon.reflect.visitor.printer.sniper.element.SniperCtTry;
import spoon.reflect.visitor.printer.sniper.element.SniperCtTryWithResource;
import spoon.reflect.visitor.printer.sniper.element.SniperCtType;
import spoon.reflect.visitor.printer.sniper.element.SniperCtTypeInformation;
import spoon.reflect.visitor.printer.sniper.element.SniperCtTypeParameterReference;
import spoon.reflect.visitor.printer.sniper.element.SniperCtTypeReference;
import spoon.reflect.visitor.printer.sniper.element.SniperCtTypedElement;
import spoon.reflect.visitor.printer.sniper.element.SniperCtUnaryOperator;
import spoon.reflect.visitor.printer.sniper.element.SniperCtVariable;
import spoon.reflect.visitor.printer.sniper.element.SniperCtVariableAccess;
import spoon.reflect.visitor.printer.sniper.element.SniperCtWhile;

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A visitor for generating Java code from the program compile-time model.
 */
public class SniperJavaPrettyPrinter extends CtScanner
		implements PrettyPrinter {

	private Deque<Action> actions;
	private Environment env;
	private HashMap<CtElement, Deque<Action>> actionsOnElement;
	private spoon.reflect.visitor.printer.sniper.SniperWriter writer;

	public SniperJavaPrettyPrinter(Environment env) {
		this.actions = env.getActionChanges();
		this.env = env;
	}

	@Override
	public String printPackageInfo(CtPackage pack) {
		return "";
	}

	@Override
	public String getResult() {
		return writer.toString();
	}

	public void reset() {
		writer.clear();
	}

	@Override
	public void calculate(CompilationUnit sourceCompilationUnit, List<CtType<?>> types) {
		actionsOnElement = new HashMap<>();
		Deque<Action> actionOnTypes = new ArrayDeque<>();
		for (CtType<?> ctType : types) {
			for (Action action : actions) {
				CtElement element = action.getContext().getElementWhereChangeHappens();
				if (element instanceof CtReference && element.isParentInitialized()) {
					element = element.getParent();
				}
				try {
					if (element.hasParent(ctType) || ctType.equals(element)) {
						if (!actionsOnElement.containsKey(element)) {
							actionsOnElement.put(element, new ArrayDeque<Action>());
						}
						actionsOnElement.get(element).addFirst(action);
						actionOnTypes.add(action);
					}
				} catch (ParentNotInitializedException e) {
					System.out.println(element);
					e.printStackTrace();
				}
			}
			for (CtElement ctElement : new HashSet<>(actionsOnElement.keySet())) {
				if (ctElement.getPosition() == null || ctElement.getPosition() instanceof NoSourcePosition) {
					actionsOnElement.remove(ctElement);
				} else {
					cleanDeleteAll(actionsOnElement.get(ctElement));
					removeMultiModifiersActions(actionsOnElement.get(ctElement));
				}
			}

			writer = new spoon.reflect.visitor.printer.sniper.SniperWriter(sourceCompilationUnit.getOriginalSourceCode(), env);
			scan(ctType);
		}
	}

	private void cleanDeleteAll(Deque<Action> actions) {
		DeleteAllAction currentDeleteAllAction = null;
		for (Action action : new ArrayDeque<>(actions)) {
			if (action instanceof DeleteAllAction) {
				currentDeleteAllAction = (DeleteAllAction) action;
				continue;
			}
			if (currentDeleteAllAction != null && action instanceof AddAction) {
				Object oldContent = currentDeleteAllAction.getRemovedValue();
				if (oldContent instanceof List) {
					List<?> list = (List) oldContent;
					if (list.contains(((AddAction) action).getNewValue())) {
						List<?> elementToRemove = new ArrayList<>(list);
						List<Action> addToDelete = new ArrayList<>();
						for (Object o : list) {
							for (Action a : new ArrayDeque<>(actions)) {
								if (o.equals(a.getChangedValue())) {
									elementToRemove.remove(o);
									addToDelete.add(a);
								}
							}
						}
						actions.removeFirstOccurrence(currentDeleteAllAction);
						for (Object o : elementToRemove) {
							if (o instanceof CtElement) {
								actions.addFirst(new DeleteAction<>(currentDeleteAllAction.getContext(), (CtElement) o));
							} else {
								actions.addFirst(new DeleteAction<>(currentDeleteAllAction.getContext(), o));
							}
						}
						for (Action action1 : addToDelete) {
							actions.removeFirstOccurrence(action1);
						}
						break;
					}
				}
			}
		}
	}

	private void removeMultiModifiersActions(Deque<Action> actions) {
		boolean isModifiersAlreadyPresent = false;
		for (Action action : new ArrayDeque<>(actions)) {
			if (action.getContext().getChangedProperty() == CtRole.MODIFIER) {
				if (isModifiersAlreadyPresent) {
					actions.remove(action);
				}
				isModifiersAlreadyPresent = true;
			}
		}
	}

	@Override
	public Map<Integer, Integer> getLineNumberMapping() {
		return null;
	}

	@Override
	protected void enter(CtElement e) {
		if (actionsOnElement.containsKey(e)) {
			SniperWriter s = new SniperWriter(actionsOnElement.get(e));
			if (e.getPosition() != null && !(e.getPosition() instanceof NoSourcePosition)) {
				s.scan(e);
				if (!s.getActions().isEmpty()) {
					throw new RuntimeException("All actions are not applied (" + s.getActions().size() + ")");
				}
			}
		}
		super.enter(e);
	}

	class SniperWriter extends CtInheritanceScanner {
		private Deque<Action> actions;

		SniperWriter(Deque<Action> actions) {
			this.actions = new ArrayDeque<>(actions);
		}

		public Deque<Action> getActions() {
			return actions;
		}

		private Action applyAction(ActionBasedChangeListener sniper, Action action) {
			try {
				sniper.onAction(action);
				return null;
			} catch (SniperNotHandledAction ignore) {
			}
			try {
				if (action instanceof AddAction) {
					sniper.onAdd((AddAction) action);
				} else if (action instanceof UpdateAction) {
					sniper.onUpdate((UpdateAction) action);
				} else if (action instanceof DeleteAllAction) {
					sniper.onDeleteAll((DeleteAllAction) action);
				} else if (action instanceof DeleteAction) {
					sniper.onDelete((DeleteAction) action);
				}
			} catch (SniperNotHandledAction ignore) {
				return action;
			}
			return null;
		}

		private void applyActions(ActionBasedChangeListener sniper) {
			Iterator<Action> actionIterator = actions.iterator();
			while (actionIterator.hasNext()) {
				Action action = actionIterator.next();
				Action consumed = applyAction(sniper, action);
				if (consumed == null) {
					actions.removeFirstOccurrence(action);
				}
			}
		}

		@Override
		public <T> void scanCtAbstractInvocation(CtAbstractInvocation<T> e) {
			applyActions(new SniperCtAbstractInvocation(writer, e));
			super.scanCtAbstractInvocation(e);
		}
		@Override
		public <T> void visitCtConditional(CtConditional<T> e) {
			applyActions(new SniperCtConditional(writer, e));
			super.visitCtConditional(e);
		}
		@Override
		public <T> void visitCtParameter(CtParameter<T> e) {
			applyActions(new SniperCtParameter(writer, e));
			super.visitCtParameter(e);
		}
		@Override
		public void scanCtLoop(CtLoop e) {
			applyActions(new SniperCtBodyHolder(writer, e));
			super.scanCtLoop(e);
		}
		@Override
		public void visitCtWhile(CtWhile e) {
			applyActions(new SniperCtWhile(writer, e));
			super.visitCtWhile(e);
		}
		@Override
		public <T> void visitCtTypeReference(CtTypeReference<T> e) {
			applyActions(new SniperCtTypeReference(writer, e));
			applyActions(new SniperCtTypeInformation(writer, e));
			super.visitCtTypeReference(e);
		}
		@Override
		public void scanCtElement(CtElement e) {
			applyActions(new SniperCtElement(writer, e));
			super.scanCtElement(e);
		}
		@Override
		public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> e) {
			applyActions(new SniperCtAssignment(writer, e));
			applyActions(new SniperCtRHSReceiver(writer, e));
			super.visitCtAssignment(e);
		}
		@Override
		public <T> void visitCtBinaryOperator(CtBinaryOperator<T> e) {
			applyActions(new SniperCtBinaryOperator(writer, e));
			super.visitCtBinaryOperator(e);
		}
		@Override
		public void visitCtForEach(CtForEach e) {
			applyActions(new SniperCtForEach(writer, e));
			super.visitCtForEach(e);
		}
		@Override
		public <T> void visitCtConstructor(CtConstructor<T> e) {
			applyActions(new SniperCtConstructor(writer, e));
			super.visitCtConstructor(e);
		}
		@Override
		public <T, E extends CtExpression<?>> void scanCtTargetedExpression(CtTargetedExpression<T, E> e) {
			applyActions(new SniperCtTargetedExpression(writer, e));
			super.scanCtTargetedExpression(e);
		}
		@Override
		public void scanCtNamedElement(CtNamedElement e) {
			applyActions(new SniperCtNamedElement(writer, e));
			super.scanCtNamedElement(e);
		}
		@Override
		public void scanCtModifiable(CtModifiable e) {
			applyActions(new SniperCtModifiable(writer, e));
			super.scanCtModifiable(e);
		}
		@Override
		public void visitCtComment(CtComment e) {
			applyActions(new SniperCtComment(writer, e));
			super.visitCtComment(e);
		}

		@Override
		public <T> void visitCtField(CtField<T> e) {
			applyActions(new SniperCtRHSReceiver(writer, e));
			super.visitCtField(e);
		}

		@Override
		public <T> void visitCtLocalVariable(CtLocalVariable<T> e) {
			applyActions(new SniperCtRHSReceiver(writer, e));
			super.visitCtLocalVariable(e);
		}

		@Override
		public <T> void scanCtFieldAccess(CtFieldAccess<T> e) {
			applyActions(new SniperCtFieldAccess(writer, e));
			super.scanCtFieldAccess(e);
		}
		@Override
		public <T> void scanCtTypeInformation(CtTypeInformation e) {
			applyActions(new SniperCtTypeInformation(writer, e));
			super.scanCtTypeInformation(e);
		}
		@Override
		public void visitCtJavaDoc(CtJavaDoc e) {
			applyActions(new SniperCtJavaDoc(writer, e));
			super.visitCtJavaDoc(e);
		}
		@Override
		public <R> void visitCtStatementList(CtStatementList e) {
			applyActions(new SniperCtStatementList(writer, e));
			super.visitCtStatementList(e);
		}
		@Override
		public <T, A extends T> void visitCtOperatorAssignment(CtOperatorAssignment<T, A> e) {
			applyActions(new SniperCtOperatorAssignment(writer, e));
			super.visitCtOperatorAssignment(e);
		}
		@Override
		public <T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess<T> e) {
			applyActions(new SniperCtAnnotationFieldAccess(writer, e));
			super.visitCtAnnotationFieldAccess(e);
		}
		@Override
		public <T> void visitCtAnnotationMethod(CtAnnotationMethod<T> e) {
			applyActions(new SniperCtAnnotationMethod(writer, e));
			super.visitCtAnnotationMethod(e);
		}
		@Override
		public void scanCtLabelledFlowBreak(CtLabelledFlowBreak e) {
			applyActions(new SniperCtLabelledFlowBreak(writer, e));
			super.scanCtLabelledFlowBreak(e);
		}
		@Override
		public <T, E extends CtExpression<?>> void scanCtArrayAccess(CtArrayAccess<T, E> e) {
			applyActions(new SniperCtArrayAccess(writer, e));
			super.scanCtArrayAccess(e);
		}
		@Override
		public void visitCtPackage(CtPackage e) {
			applyActions(new SniperCtPackage(writer, e));
			super.visitCtPackage(e);
		}
		@Override
		public void visitCtTryWithResource(CtTryWithResource e) {
			applyActions(new SniperCtTryWithResource(writer, e));
			super.visitCtTryWithResource(e);
		}
		@Override
		public <T> void visitCtClass(CtClass<T> e) {
			applyActions(new SniperCtClass(writer, e));
			super.visitCtClass(e);
		}
		@Override
		public <E> void visitCtSwitch(CtSwitch<E> e) {
			applyActions(new SniperCtSwitch(writer, e));
			super.visitCtSwitch(e);
		}
		@Override
		public void visitCtSynchronized(CtSynchronized e) {
			applyActions(new SniperCtSynchronized(writer, e));
			super.visitCtSynchronized(e);
		}
		@Override
		public void visitCtTry(CtTry e) {
			applyActions(new SniperCtTry(writer, e));
			applyActions(new SniperCtBodyHolder(writer, e));
			super.visitCtTry(e);
		}
		@Override
		public <T> void visitCtAssert(CtAssert<T> e) {
			applyActions(new SniperCtAssert(writer, e));
			super.visitCtAssert(e);
		}
		@Override
		public <T> void visitCtInvocation(CtInvocation<T> e) {
			applyActions(new SniperCtInvocation(writer, e));
			super.visitCtInvocation(e);
		}
		@Override
		public void visitCtTypeParameterReference(CtTypeParameterReference e) {
			applyActions(new SniperCtTypeParameterReference(writer, e));
			super.visitCtTypeParameterReference(e);
		}
		@Override
		public void scanCtStatement(CtStatement e) {
			applyActions(new SniperCtStatement(writer, e));
			super.scanCtStatement(e);
		}
		@Override
		public <T> void visitCtUnaryOperator(CtUnaryOperator<T> e) {
			applyActions(new SniperCtUnaryOperator(writer, e));
			super.visitCtUnaryOperator(e);
		}
		@Override
		public <T> void scanCtTypedElement(CtTypedElement<T> e) {
			applyActions(new SniperCtTypedElement(writer, e));
			super.scanCtTypedElement(e);
		}
		@Override
		public void visitCtFor(CtFor e) {
			applyActions(new SniperCtFor(writer, e));
			super.visitCtFor(e);
		}
		@Override
		public void scanCtFormalTypeDeclarer(CtFormalTypeDeclarer e) {
			applyActions(new SniperCtFormalTypeDeclarer(writer, e));
			super.scanCtFormalTypeDeclarer(e);
		}
		@Override
		public <R> void scanCtExecutable(CtExecutable<R> e) {
			applyActions(new SniperCtExecutable(writer, e));
			applyActions(new SniperCtBodyHolder(writer, e));
			super.scanCtExecutable(e);
		}
		@Override
		public void scanCtMultiTypedElement(CtMultiTypedElement e) {
			applyActions(new SniperCtMultiTypedElement(writer, e));
			super.scanCtMultiTypedElement(e);
		}
		@Override
		public void visitCtIf(CtIf e) {
			applyActions(new SniperCtIf(writer, e));
			super.visitCtIf(e);
		}
		@Override
		public <T> void scanCtType(CtType<T> e) {
			applyActions(new SniperCtType(writer, e));
			applyActions(new SniperCtTypeInformation(writer, e));
			super.scanCtType(e);
		}
		@Override
		public <T> void scanCtVariable(CtVariable<T> e) {
			applyActions(new SniperCtVariable(writer, e));
			super.scanCtVariable(e);
		}
		@Override
		public <E> void visitCtCase(CtCase<E> e) {
			applyActions(new SniperCtCase(writer, e));
			super.visitCtCase(e);
		}
		@Override
		public void visitCtCatch(CtCatch e) {
			applyActions(new SniperCtCatch(writer, e));
			super.visitCtCatch(e);
		}
		@Override
		public <T> void visitCtConstructorCall(CtConstructorCall<T> e) {
			applyActions(new SniperCtConstructorCall(writer, e));
			super.visitCtConstructorCall(e);
		}
		@Override
		public <T> void visitCtMethod(CtMethod<T> e) {
			applyActions(new SniperCtMethod(writer, e));
			super.visitCtMethod(e);
		}
		@Override
		public <T> void visitCtIntersectionTypeReference(CtIntersectionTypeReference<T> e) {
			applyActions(new SniperCtIntersectionTypeReference(writer, e));
			super.visitCtIntersectionTypeReference(e);
		}
		@Override
		public <T> void visitCtLambda(CtLambda<T> e) {
			applyActions(new SniperCtLambda(writer, e));
			super.visitCtLambda(e);
		}
		@Override
		public <T> void visitCtNewArray(CtNewArray<T> e) {
			applyActions(new SniperCtNewArray(writer, e));
			super.visitCtNewArray(e);
		}
		@Override
		public void visitCtThrow(CtThrow e) {
			applyActions(new SniperCtThrow(writer, e));
			super.visitCtThrow(e);
		}
		@Override
		public void visitCtJavaDocTag(CtJavaDocTag e) {
			applyActions(new SniperCtJavaDocTag(writer, e));
			super.visitCtJavaDocTag(e);
		}
		@Override
		public <T> void visitCtLiteral(CtLiteral<T> e) {
			applyActions(new SniperCtLiteral(writer, e));
			super.visitCtLiteral(e);
		}
		@Override
		public <R> void visitCtReturn(CtReturn<R> e) {
			applyActions(new SniperCtReturn(writer, e));
			super.visitCtReturn(e);
		}
		@Override
		public void visitCtDo(CtDo e) {
			applyActions(new SniperCtDo(writer, e));
			super.visitCtDo(e);
		}
		@Override
		public <A extends Annotation> void visitCtAnnotation(CtAnnotation<A> e) {
			applyActions(new SniperCtAnnotation(writer, e));
			super.visitCtAnnotation(e);
		}
		@Override
		public <T> void scanCtVariableAccess(CtVariableAccess<T> e) {
			applyActions(new SniperCtVariableAccess(writer, e));
			super.scanCtVariableAccess(e);
		}
		@Override
		public <T> void scanCtExpression(CtExpression<T> e) {
			applyActions(new SniperCtExpression(writer, e));
			super.scanCtExpression(e);
		}
		@Override
		public <T> void visitCtFieldReference(CtFieldReference<T> e) {
			applyActions(new SniperCtFieldReference(writer, e));
			super.visitCtFieldReference(e);
		}
		@Override
		public <T extends Enum<?>> void visitCtEnum(CtEnum<T> e) {
			applyActions(new SniperCtEnum(writer, e));
			super.visitCtEnum(e);
		}
		@Override
		public <T> void visitCtNewClass(CtNewClass<T> e) {
			applyActions(new SniperCtNewClass(writer, e));
			super.visitCtNewClass(e);
		}
		@Override
		public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(CtExecutableReferenceExpression<T, E> e) {
			applyActions(new SniperCtExecutableReferenceExpression(writer, e));
			super.visitCtExecutableReferenceExpression(e);
		}


	}
}

