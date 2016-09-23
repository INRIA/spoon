/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.support.compiler.jdt;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteralConcatenation;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtAnnotatedElementType;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtInheritanceScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ParentExiter extends CtInheritanceScanner {

	private final JDTTreeBuilder jdtTreeBuilder;

	private CtElement child;
	private ASTNode childJDT;
	private Map<CtTypedElement<?>, List<CtTypeReference<? extends java.lang.annotation.Annotation>>> annotationsMap = new HashMap<>();

	/**
	 * @param jdtTreeBuilder
	 */
	ParentExiter(JDTTreeBuilder jdtTreeBuilder) {
		this.jdtTreeBuilder = jdtTreeBuilder;
	}

	public void setChild(CtElement child) {
		this.child = child;
	}

	public void setChild(ASTNode child) {
		this.childJDT = child;
	}

	@Override
	public void scanCtElement(CtElement e) {
		if (child instanceof CtAnnotation && this.jdtTreeBuilder.getContextBuilder().annotationValueName.isEmpty()) {
			e.addAnnotation((CtAnnotation<?>) child);
			if (e instanceof CtTypedElement && JDTTreeBuilderQuery.hasAnnotationWithType((Annotation) childJDT, CtAnnotatedElementType.TYPE_USE)) {
				List<CtTypeReference<? extends java.lang.annotation.Annotation>> annotations = new ArrayList<>();
				if (!annotationsMap.containsKey(e)) {
					annotationsMap.put((CtTypedElement<?>) e, annotations);
				} else {
					annotations = annotationsMap.get(e);
				}
				annotations.add(((CtAnnotation) child).getType());
				annotationsMap.put((CtTypedElement<?>) e, annotations);
			}
			return;
		}
	}

	private void substituteAnnotation(CtTypedElement ele) {
		if (annotationsMap.containsKey(ele)) {
			List<CtTypeReference<? extends java.lang.annotation.Annotation>> annotations = annotationsMap.get(ele);
			for (CtTypeReference<? extends java.lang.annotation.Annotation> annotation : annotations) {
				final CtAnnotation<? extends java.lang.annotation.Annotation> targetAnnotation = ele.getAnnotation(annotation);
				ele.removeAnnotation(targetAnnotation);
				ele.getType().addAnnotation(targetAnnotation);
			}
			annotationsMap.remove(ele);
		}
	}

	@Override
	public <R> void scanCtExecutable(CtExecutable<R> e) {
		if (child instanceof CtTypeAccess) {
			e.addThrownType(((CtTypeAccess) child).getAccessedType());
			return;
		} else if (child instanceof CtParameter) {
			e.addParameter((CtParameter<?>) child);
			return;
		} else if (child instanceof CtBlock && !(e instanceof CtMethod || e instanceof CtConstructor)) {
			e.setBody((CtBlock<R>) child);
			return;
		}
		super.scanCtExecutable(e);
	}

	@Override
	public void scanCtFormalTypeDeclarer(CtFormalTypeDeclarer e) {
		if (childJDT instanceof TypeParameter && child instanceof CtTypeParameter) {
			e.addFormalCtTypeParameter((CtTypeParameter) child);
		}
		return;
	}

	@Override
	public void scanCtLoop(CtLoop loop) {
		if (loop.getBody() == null && child instanceof CtStatement) {
			CtStatement child = (CtStatement) this.child;
			if (!(this.child instanceof CtBlock)) {
				child = jdtTreeBuilder.getFactory().Code().createCtBlock(child);
				child.setImplicit(true);
			}
			loop.setBody(child);
		}
		super.scanCtLoop(loop);
	}

	@Override
	public <T, E extends CtExpression<?>> void scanCtTargetedExpression(CtTargetedExpression<T, E> targetedExpression) {
		if (child instanceof CtExpression) {
			targetedExpression.setTarget((E) child);
			return;
		}
		super.scanCtTargetedExpression(targetedExpression);
	}

	@Override
	public <T> void scanCtType(CtType<T> type) {
		if (child instanceof CtType && !(child instanceof CtTypeParameter)) {
			if (type.getTypeMembers().contains(child)) {
				type.removeTypeMember((CtType) child);
			}
			type.addNestedType((CtType<?>) child);
			return;
		} else if (child instanceof CtEnumValue && type instanceof CtEnum) {
			((CtEnum) type).addEnumValue((CtEnumValue) child);
		} else if (child instanceof CtField) {
			type.addField((CtField<?>) child);
			return;
		} else if (child instanceof CtConstructor) {
			return;
		}
		if (child instanceof CtMethod) {
			type.addMethod((CtMethod<?>) child);
			return;
		}
		super.scanCtType(type);
	}

	@Override
	public <T> void scanCtVariable(CtVariable<T> v) {
		if (childJDT instanceof TypeReference && child instanceof CtTypeAccess) {
			v.setType(((CtTypeAccess) child).getAccessedType());
			substituteAnnotation((CtTypedElement) v);
			return;
		} else if (child instanceof CtExpression && hasChildEqualsToDefaultValue(v)) {
			v.setDefaultExpression((CtExpression<T>) child);
			return;
		}
		super.scanCtVariable(v);
	}

	private <T> boolean hasChildEqualsToDefaultValue(CtVariable<T> ctVariable) {
		if (jdtTreeBuilder.getContextBuilder().stack.peek().node instanceof AnnotationMethodDeclaration) {
			final AnnotationMethodDeclaration parent = (AnnotationMethodDeclaration) jdtTreeBuilder.getContextBuilder().stack.peek().node;
			// Default value is equals to the jdt child.
			return parent.defaultValue != null && getFinalExpressionFromCast(parent.defaultValue).equals(childJDT)
					// Return type not yet initialized.
					&& !child.equals(ctVariable.getDefaultExpression());
		}
		final AbstractVariableDeclaration parent = (AbstractVariableDeclaration) jdtTreeBuilder.getContextBuilder().stack.peek().node;
		// Default value is equals to the jdt child.
		return parent.initialization != null && getFinalExpressionFromCast(parent.initialization).equals(childJDT)
				// Return type not yet initialized.
				&& !child.equals(ctVariable.getDefaultExpression());
	}

	@Override
	public <A extends java.lang.annotation.Annotation> void visitCtAnnotation(CtAnnotation<A> annotation) {
		if (child instanceof CtExpression) {
			annotation.addValue(this.jdtTreeBuilder.getContextBuilder().annotationValueName.peek(), child);
		}
		super.visitCtAnnotation(annotation);
	}

	@Override
	public <T> void visitCtConstructor(CtConstructor<T> e) {
		if (e.getBody() == null && child instanceof CtBlock) {
			e.setBody((CtBlock) child);
			return;
		} else if (child instanceof CtStatement) {
			visitCtBlock(e.getBody());
			return;
		}
		super.visitCtConstructor(e);
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> e) {
		if (e.getBody() == null && child instanceof CtBlock) {
			e.setBody((CtBlock) child);
			return;
		} else if (child instanceof CtStatement) {
			visitCtBlock(e.getBody());
			return;
		} else if (child instanceof CtTypeAccess && hasChildEqualsToType(e)) {
			e.setType(((CtTypeAccess) child).getAccessedType());
			substituteAnnotation(e);
			return;
		}
		super.visitCtMethod(e);
	}

	private <T> boolean hasChildEqualsToType(CtMethod<T> ctMethod) {
		final MethodDeclaration parent = (MethodDeclaration) jdtTreeBuilder.getContextBuilder().stack.peek().node;
		// Return type is equals to the jdt child.
		return parent.returnType != null && parent.returnType.equals(childJDT)
				// Return type not yet initialized.
				&& !child.equals(ctMethod.getType());
	}

	@Override
	public <T> void visitCtAnnotationMethod(CtAnnotationMethod<T> annotationMethod) {
		if (child instanceof CtExpression && hasChildEqualsToDefaultValue(annotationMethod)) {
			annotationMethod.setDefaultExpression((CtExpression) child);
			return;
		}
		super.visitCtAnnotationMethod(annotationMethod);
	}

	private <T> boolean hasChildEqualsToDefaultValue(CtAnnotationMethod<T> ctAnnotationMethod) {
		final AnnotationMethodDeclaration parent = (AnnotationMethodDeclaration) jdtTreeBuilder.getContextBuilder().stack.peek().node;
		// Default value is equals to the jdt child.
		return parent.defaultValue != null && parent.defaultValue.equals(childJDT)
				// Default value not yet initialized.
				&& !child.equals(ctAnnotationMethod.getDefaultExpression());
	}

	@Override
	public void visitCtAnonymousExecutable(CtAnonymousExecutable e) {
		if (child instanceof CtBlock) {
			e.setBody((CtBlock) child);
			return;
		}
		super.visitCtAnonymousExecutable(e);
	}

	@Override
	public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {
		if (visitArrayAccess(arrayRead)) {
			super.visitCtArrayRead(arrayRead);
		}
	}

	@Override
	public <T> void visitCtArrayWrite(CtArrayWrite<T> arrayWrite) {
		if (visitArrayAccess(arrayWrite)) {
			super.visitCtArrayWrite(arrayWrite);
		}
	}

	private <T, E extends CtExpression<?>> boolean visitArrayAccess(CtArrayAccess<T, E> arrayAccess) {
		if (child instanceof CtExpression) {
			if (arrayAccess.getTarget() == null) {
				arrayAccess.setTarget((E) child);
				return false;
			} else {
				arrayAccess.setIndexExpression((CtExpression<Integer>) child);
				return false;
			}
		}
		return true;
	}

	@Override
	public <T> void visitCtAssert(CtAssert<T> asserted) {
		if (child instanceof CtExpression) {
			if (asserted.getAssertExpression() == null) {
				asserted.setAssertExpression((CtExpression<Boolean>) child);
				return;
			} else {
				asserted.setExpression((CtExpression<T>) child);
				return;
			}
		}
		super.visitCtAssert(asserted);
	}

	@Override
	public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignement) {
		if (child instanceof CtExpression) {
			if (assignement.getAssigned() == null) {
				assignement.setAssigned((CtExpression<T>) child);
				return;
			} else if (assignement.getAssignment() == null) {
				assignement.setAssignment((CtExpression<A>) child);
				return;
			}
		}
		super.visitCtAssignment(assignement);
	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		if (child instanceof CtExpression) {
			if (operator.getLeftHandOperand() == null) {
				operator.setLeftHandOperand((CtExpression<?>) child);
				return;
			} else if (operator.getRightHandOperand() == null) {
				operator.setRightHandOperand((CtExpression<?>) child);
				return;
			} else if (jdtTreeBuilder.getContextBuilder().stack.peek().node instanceof StringLiteralConcatenation) {
				CtBinaryOperator<?> op = operator.getFactory().Core().createBinaryOperator();
				op.setKind(BinaryOperatorKind.PLUS);
				op.setLeftHandOperand(operator.getRightHandOperand());
				op.setRightHandOperand((CtExpression<?>) child);
				operator.setRightHandOperand(op);
				return;
			}
		}
		super.visitCtBinaryOperator(operator);
	}

	@Override
	public <R> void visitCtBlock(CtBlock<R> block) {
		if (child instanceof CtStatement) {
			block.addStatement((CtStatement) child);
			return;
		}
		super.visitCtBlock(block);
	}

	@Override
	public <E> void visitCtCase(CtCase<E> caseStatement) {
		final ASTNode node = jdtTreeBuilder.getContextBuilder().stack.peek().node;
		if (node instanceof CaseStatement && ((CaseStatement) node).constantExpression != null && caseStatement.getCaseExpression() == null && child instanceof CtExpression) {
			caseStatement.setCaseExpression((CtExpression<E>) child);
			return;
		} else if (child instanceof CtStatement) {
			caseStatement.addStatement((CtStatement) child);
			return;
		}
		super.visitCtCase(caseStatement);
	}

	@Override
	public void visitCtCatch(CtCatch catchBlock) {
		if (child instanceof CtBlock) {
			catchBlock.setBody((CtBlock<?>) child);
			return;
		} else if (child instanceof CtCatchVariable) {
			catchBlock.setParameter((CtCatchVariable<? extends Throwable>) child);
			return;
		}
		super.visitCtCatch(catchBlock);
	}

	@Override
	public <T> void visitCtCatchVariable(CtCatchVariable<T> e) {
		if (jdtTreeBuilder.getContextBuilder().stack.peekFirst().node instanceof UnionTypeReference) {
			e.addMultiType((CtTypeReference<?>) child);
			return;
		}
		super.visitCtCatchVariable(e);
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		if (child instanceof CtConstructor) {
			CtConstructor<T> c = (CtConstructor<T>) child;
			ctClass.addConstructor(c);
			if (c.getPosition() != null && c.getPosition().getSourceStart() == -1) {
				c.setImplicit(true);
			}
		}
		if (child instanceof CtAnonymousExecutable) {
			ctClass.addAnonymousExecutable((CtAnonymousExecutable) child);
		}
		super.visitCtClass(ctClass);
	}

	@Override
	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		if (childJDT instanceof TypeReference && child instanceof CtTypeAccess) {
			if (typeParameter.getSuperclass() == null) {
				typeParameter.setSuperclass(((CtTypeAccess) child).getAccessedType());
			} else if (typeParameter.getSuperclass() instanceof CtIntersectionTypeReference) {
				typeParameter.getSuperclass().asCtIntersectionTypeReference().addBound(((CtTypeAccess) child).getAccessedType());
			} else {
				final List<CtTypeReference<?>> refs = new ArrayList<>();
				refs.add(typeParameter.getSuperclass());
				refs.add(((CtTypeAccess) child).getAccessedType());
				typeParameter.setSuperclass(jdtTreeBuilder.getFactory().Type().createIntersectionTypeReferenceWithBounds(refs));
			}
			return;
		}
		super.visitCtTypeParameter(typeParameter);
	}

	@Override
	public <T> void visitCtConditional(CtConditional<T> conditional) {
		if (child instanceof CtExpression) {
			if (conditional.getCondition() == null) {
				conditional.setCondition((CtExpression<Boolean>) child);
			} else if (conditional.getThenExpression() == null) {
				conditional.setThenExpression((CtExpression<T>) child);
			} else if (conditional.getElseExpression() == null) {
				conditional.setElseExpression((CtExpression<T>) child);
			}
		}
		super.visitCtConditional(conditional);
	}

	@Override
	public void visitCtDo(CtDo doLoop) {
		if (doLoop.getBody() != null && child instanceof CtExpression && doLoop.getLoopingExpression() == null) {
			doLoop.setLoopingExpression((CtExpression<Boolean>) child);
			return;
		}
		super.visitCtDo(doLoop);
	}

	@Override
	public void visitCtFor(CtFor forLoop) {
		if (isContainedInForInit() && child instanceof CtStatement) {
			forLoop.addForInit((CtStatement) child);
			return;
		} else if (isContainedInForUpdate() && child instanceof CtStatement) {
			forLoop.addForUpdate((CtStatement) child);
			return;
		} else if (forLoop.getExpression() == null && child instanceof CtExpression) {
			forLoop.setExpression((CtExpression<Boolean>) child);
			return;
		}
		super.visitCtFor(forLoop);
	}

	private boolean isContainedInForInit() {
		if (!(jdtTreeBuilder.getContextBuilder().stack.peek().node instanceof ForStatement)) {
			return false;
		}
		final ForStatement parent = (ForStatement) jdtTreeBuilder.getContextBuilder().stack.peek().node;
		if (parent.initializations == null) {
			return false;
		}
		for (Statement initialization : parent.initializations) {
			if (initialization != null && initialization.equals(childJDT)) {
				return true;
			}
		}
		return false;
	}

	private boolean isContainedInForUpdate() {
		if (!(jdtTreeBuilder.getContextBuilder().stack.peek().node instanceof ForStatement)) {
			return false;
		}
		final ForStatement parent = (ForStatement) jdtTreeBuilder.getContextBuilder().stack.peek().node;
		if (parent.increments == null) {
			return false;
		}
		for (Statement increment : parent.increments) {
			if (increment != null && increment.equals(childJDT)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void visitCtForEach(CtForEach foreach) {
		if (foreach.getVariable() == null && child instanceof CtVariable) {
			foreach.setVariable((CtLocalVariable<?>) child);
			return;
		} else if (foreach.getExpression() == null && child instanceof CtExpression) {
			foreach.setExpression((CtExpression<?>) child);
			return;
		}
		super.visitCtForEach(foreach);
	}

	@Override
	public void visitCtWhile(CtWhile whileLoop) {
		if (whileLoop.getLoopingExpression() == null && child instanceof CtExpression) {
			whileLoop.setLoopingExpression((CtExpression<Boolean>) child);
			return;
		}
		super.visitCtWhile(whileLoop);
	}

	@Override
	public void visitCtIf(CtIf ifElement) {
		if (ifElement.getCondition() == null && child instanceof CtExpression) {
			ifElement.setCondition((CtExpression<Boolean>) child);
			return;
		} else if (child instanceof CtStatement) {
			CtStatement child = (CtStatement) this.child;
			if (!(this.child instanceof CtBlock)) {
				child = jdtTreeBuilder.getFactory().Code().createCtBlock(child);
				child.setImplicit(true);
			}
			if (ifElement.getThenStatement() == null) {
				ifElement.setThenStatement(child);
				return;
			} else if (ifElement.getElseStatement() == null) {
				ifElement.setElseStatement(child);
				return;
			}
		}
		super.visitCtIf(ifElement);
	}

	@Override
	public <T> void visitCtSuperAccess(CtSuperAccess<T> superAccess) {
		if (child instanceof CtTypeAccess<?>) {
			superAccess.setTarget((CtTypeAccess<?>) child);
			return;
		}
		super.visitCtSuperAccess(superAccess);
	}

	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		if (childJDT instanceof TypeReference && child instanceof CtTypeAccess) {
			invocation.getExecutable().addActualTypeArgument(((CtTypeAccess) child).getAccessedType());
			return;
		} else if (child instanceof CtExpression) {
			if (hasChildEqualsToReceiver(invocation) || hasChildEqualsToQualification(invocation)) {
				if (child instanceof CtThisAccess) {
					final CtTypeReference<?> declaringType = invocation.getExecutable().getDeclaringType();
					if (declaringType != null && invocation.getExecutable().isStatic() && child.isImplicit()) {
						invocation.setTarget(jdtTreeBuilder.getFactory().Code().createTypeAccess(declaringType, declaringType.isAnonymous()));
					} else if (declaringType != null && !invocation.getExecutable().isStatic() && child.isImplicit()) {
						((CtThisAccess) child).setTarget(jdtTreeBuilder.getFactory().Code().createTypeAccess(declaringType, true));
						invocation.setTarget((CtThisAccess<?>) child);
					} else {
						invocation.setTarget((CtThisAccess<?>) child);
					}
				} else {
					invocation.setTarget((CtExpression<?>) child);
				}
			} else {
				invocation.addArgument((CtExpression<?>) child);
			}
			return;
		}
		super.visitCtInvocation(invocation);
	}

	private <T> boolean hasChildEqualsToQualification(CtInvocation<T> ctInvocation) {
		if (!(jdtTreeBuilder.getContextBuilder().stack.peek().node instanceof ExplicitConstructorCall)) {
			return false;
		}
		final ExplicitConstructorCall parent = (ExplicitConstructorCall) jdtTreeBuilder.getContextBuilder().stack.peek().node;
		// qualification is equals to the jdt child.
		return parent.qualification != null && getFinalExpressionFromCast(parent.qualification).equals(childJDT)
				// qualification not yet initialized.
				&& !child.equals(ctInvocation.getTarget());
	}

	private <T> boolean hasChildEqualsToReceiver(CtInvocation<T> ctInvocation) {
		if (!(jdtTreeBuilder.getContextBuilder().stack.peek().node instanceof MessageSend)) {
			return false;
		}
		final MessageSend parent = (MessageSend) jdtTreeBuilder.getContextBuilder().stack.peek().node;
		// Receiver is equals to the jdt child.
		return parent.receiver != null && getFinalExpressionFromCast(parent.receiver).equals(childJDT)
				// Receiver not yet initialized.
				&& !child.equals(ctInvocation.getTarget());
	}

	private Expression getFinalExpressionFromCast(Expression potentialCase) {
		if (!(potentialCase instanceof CastExpression)) {
			return potentialCase;
		}
		return getFinalExpressionFromCast(((CastExpression) potentialCase).expression);
	}

	@Override
	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		if (childJDT instanceof TypeReference && child instanceof CtTypeAccess) {
			final ArrayAllocationExpression arrayAlloc = (ArrayAllocationExpression) jdtTreeBuilder.getContextBuilder().stack.peek().node;
			newArray.setType((CtArrayTypeReference) jdtTreeBuilder.getFactory().Type().createArrayReference(((CtTypeAccess) child).getAccessedType(), arrayAlloc.dimensions.length));
		} else if (child instanceof CtExpression) {
			if (isContainedInDimensionExpression()) {
				newArray.addDimensionExpression((CtExpression<Integer>) child);
			} else if (child instanceof CtNewArray && childJDT instanceof ArrayInitializer && jdtTreeBuilder.getContextBuilder().stack.peek().node instanceof ArrayAllocationExpression) {
				newArray.setElements(((CtNewArray) child).getElements());
			} else {
				newArray.addElement((CtExpression) child);
			}
		}
	}

	private boolean isContainedInDimensionExpression() {
		if (!(jdtTreeBuilder.getContextBuilder().stack.peek().node instanceof ArrayAllocationExpression)) {
			return false;
		}
		final ArrayAllocationExpression parent = (ArrayAllocationExpression) jdtTreeBuilder.getContextBuilder().stack.peek().node;
		if (parent.dimensions == null) {
			return false;
		}
		for (Expression dimension : parent.dimensions) {
			if (dimension != null && getFinalExpressionFromCast(dimension).equals(childJDT)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
		if (child instanceof CtTypeAccess) {
			if (hasChildEqualsToType(ctConstructorCall)) {
				ctConstructorCall.getExecutable().setType(((CtTypeAccess) child).getAccessedType());
			} else {
				ctConstructorCall.addActualTypeArgument(((CtTypeAccess) child).getAccessedType());
			}
			return;
		} else if (child instanceof CtExpression) {
			if (hasChildEqualsToEnclosingInstance(ctConstructorCall)) {
				ctConstructorCall.setTarget((CtExpression<?>) child);
			} else {
				ctConstructorCall.addArgument((CtExpression<?>) child);
			}
			return;
		}
		super.visitCtConstructorCall(ctConstructorCall);
	}

	private <T> boolean hasChildEqualsToEnclosingInstance(CtConstructorCall<T> ctConstructorCall) {
		if (!(jdtTreeBuilder.getContextBuilder().stack.peek().node instanceof QualifiedAllocationExpression)) {
			return false;
		}
		final QualifiedAllocationExpression parent = (QualifiedAllocationExpression) jdtTreeBuilder.getContextBuilder().stack.peek().node;
		// Enclosing instance is equals to the jdt child.
		return parent.enclosingInstance != null && getFinalExpressionFromCast(parent.enclosingInstance).equals(childJDT)
				// Enclosing instance not yet initialized.
				&& !child.equals(ctConstructorCall.getTarget());
	}

	private <T> boolean hasChildEqualsToType(CtConstructorCall<T> ctConstructorCall) {
		final AllocationExpression parent = (AllocationExpression) jdtTreeBuilder.getContextBuilder().stack.peek().node;
		// Type is equals to the jdt child.
		return parent.type != null && parent.type.equals(childJDT)
				// Type not yet initialized.
				&& !((CtTypeAccess) child).getAccessedType().equals(ctConstructorCall.getExecutable().getType());
	}

	@Override
	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		if (child instanceof CtClass) {
			newClass.setAnonymousClass((CtClass<?>) child);
			final QualifiedAllocationExpression node = (QualifiedAllocationExpression) jdtTreeBuilder.getContextBuilder().stack.peek().node;
			final ReferenceBinding[] referenceBindings = node.resolvedType == null ? null : node.resolvedType.superInterfaces();
			if (referenceBindings != null && referenceBindings.length > 0) {
				((CtClass<?>) child).addSuperInterface(newClass.getType().clone());
			} else if (newClass.getType() != null) {
				((CtClass<?>) child).setSuperclass(newClass.getType().clone());
			}
			return;
		}
		super.visitCtNewClass(newClass);
	}

	@Override
	public <T> void visitCtLambda(CtLambda<T> lambda) {
		if (child instanceof CtParameter) {
			lambda.addParameter((CtParameter<?>) child);
			return;
		} else if (child instanceof CtBlock) {
			lambda.setBody((CtBlock) child);
			return;
		} else if (child instanceof CtExpression) {
			lambda.setExpression((CtExpression<T>) child);
		}
		super.visitCtLambda(lambda);
	}

	@Override
	public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(CtExecutableReferenceExpression<T, E> expression) {
		if (child instanceof CtExpression) {
			expression.setTarget((E) child);
		}
		super.visitCtExecutableReferenceExpression(expression);
	}

	@Override
	public void visitCtPackage(CtPackage ctPackage) {
		if (child instanceof CtType) {
			if (ctPackage.getTypes().contains(child)) {
				ctPackage.getTypes().remove(child);
			}
			ctPackage.getTypes().add((CtType<?>) child);
			if (child.getPosition() != null && child.getPosition().getCompilationUnit() != null) {
				child.getPosition().getCompilationUnit().getDeclaredTypes().add((CtType<?>) child);
			}
			return;
		}
		super.visitCtPackage(ctPackage);
	}

	@Override
	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		if (child instanceof CtExpression) {
			returnStatement.setReturnedExpression((CtExpression<R>) child);
			return;
		}
		super.visitCtReturn(returnStatement);
	}

	@Override
	public <E> void visitCtSwitch(CtSwitch<E> switchStatement) {
		if (switchStatement.getSelector() == null && child instanceof CtExpression) {
			switchStatement.setSelector((CtExpression<E>) child);
			return;
		}
		if (child instanceof CtCase) {
			switchStatement.addCase((CtCase<E>) child);
			return;
		}
		super.visitCtSwitch(switchStatement);
	}

	@Override
	public void visitCtSynchronized(CtSynchronized synchro) {
		if (synchro.getExpression() == null && child instanceof CtExpression) {
			synchro.setExpression((CtExpression<?>) child);
			return;
		}
		if (synchro.getBlock() == null && child instanceof CtBlock) {
			synchro.setBlock((CtBlock<?>) child);
			return;
		}
		super.visitCtSynchronized(synchro);
	}

	@Override
	public void visitCtThrow(CtThrow throwStatement) {
		if (throwStatement.getThrownExpression() == null && child instanceof CtExpression) {
			throwStatement.setThrownExpression((CtExpression<? extends Throwable>) child);
			return;
		}
		super.visitCtThrow(throwStatement);
	}

	@Override
	public void visitCtTry(CtTry tryBlock) {
		if (child instanceof CtBlock) {
			final CtBlock<?> childBlock = (CtBlock<?>) this.child;
			if (tryBlock.getCatchers().size() > 0 && tryBlock.getCatchers().get(tryBlock.getCatchers().size() - 1).getBody() == null) {
				tryBlock.getCatchers().get(tryBlock.getCatchers().size() - 1).setBody(childBlock);
			} else if (tryBlock.getBody() != null && tryBlock.getFinalizer() == null) {
				tryBlock.setFinalizer(childBlock);
			} else {
				tryBlock.setBody(childBlock);
			}
			return;
		} else if (child instanceof CtCatch) {
			tryBlock.addCatcher((CtCatch) child);
			return;
		}
		super.visitCtTry(tryBlock);
	}

	@Override
	public void visitCtTryWithResource(CtTryWithResource tryWithResource) {
		if (child instanceof CtLocalVariable) {
			tryWithResource.addResource((CtLocalVariable<?>) child);
		}
		super.visitCtTryWithResource(tryWithResource);
	}

	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		if (operator.getOperand() == null && child instanceof CtExpression) {
			operator.setOperand((CtExpression<T>) child);
			return;
		}
		super.visitCtUnaryOperator(operator);
	}

	@Override
	public void visitCtTypeParameterReference(CtTypeParameterReference e) {
		if (childJDT instanceof TypeReference && child instanceof CtTypeAccess) {
			e.addBound(((CtTypeAccess) child).getAccessedType());
		}
		super.visitCtTypeParameterReference(e);
	}
}
