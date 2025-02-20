/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
import org.eclipse.jdt.internal.compiler.ast.EitherOrMultiPattern;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteralConcatenation;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.jspecify.annotations.Nullable;
import spoon.SpoonException;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CaseKind;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCasePattern;
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
import spoon.reflect.code.CtPattern;
import spoon.reflect.code.CtRecordPattern;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
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
import spoon.reflect.declaration.CtReceiverParameter;
import spoon.reflect.declaration.CtRecord;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spoon.reflect.code.BinaryOperatorKind.INSTANCEOF;

@SuppressWarnings("unchecked")
public class ParentExiter extends CtInheritanceScanner {

	private final JDTTreeBuilder jdtTreeBuilder;

	private CtElement child;
	private ASTNode childJDT;
	private ASTPair parentPair;
	private Map<CtTypedElement<?>, List<CtAnnotation>> annotationsMap = new HashMap<>();

	/**
	 * @param jdtTreeBuilder
	 */
	ParentExiter(JDTTreeBuilder jdtTreeBuilder) {
		this.jdtTreeBuilder = jdtTreeBuilder;
	}

	public void exitParent(ASTPair pair) {
		this.parentPair = pair;
		scan(pair.element());
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
			// we check if the current element can have the annotation attached
			CtAnnotatedElementType annotatedElementType = CtAnnotation.getAnnotatedElementTypeForCtElement(e);
			annotatedElementType = (e instanceof CtTypeParameter || e instanceof CtTypeParameterReference) ? CtAnnotatedElementType.TYPE_USE : annotatedElementType;

			// in case of noclasspath, we cannot be 100% sure, so we guess it must be attached...
			if (this.jdtTreeBuilder.getFactory().getEnvironment().getNoClasspath() || (annotatedElementType != null && JDTTreeBuilderQuery.hasAnnotationWithType((Annotation) childJDT, annotatedElementType))) {
				e.addAnnotation((CtAnnotation<?>) child);
			}

			// in this case the annotation should be (also) attached to the type
			if (e instanceof CtTypedElement && JDTTreeBuilderQuery.hasAnnotationWithType((Annotation) childJDT, CtAnnotatedElementType.TYPE_USE)) {
				List<CtAnnotation> annotations = new ArrayList<>();
				if (!annotationsMap.containsKey(e)) {
					annotationsMap.put((CtTypedElement<?>) e, annotations);
				} else {
					annotations = annotationsMap.get(e);
				}
				annotations.add((CtAnnotation) child.clone());
				annotationsMap.put((CtTypedElement<?>) e, annotations);
			}
		}
	}

	private void substituteAnnotation(CtTypedElement ele) {
		if (annotationsMap.containsKey(ele)) {
			List<CtAnnotation> annotations = annotationsMap.get(ele);
			for (CtAnnotation annotation : annotations) {

				// in case of noclasspath we attached previously the element:
				// if we are here, we may have find an element for whom it's a better place
				if (this.jdtTreeBuilder.getFactory().getEnvironment().getNoClasspath() && annotation.isParentInitialized()) {
					CtElement parent = annotation.getParent();
					parent.removeAnnotation(annotation);
				}

				if (!ele.getType().getAnnotations().contains(annotation)) {
					ele.getType().addAnnotation(annotation.clone());
				}
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
		} else if (child instanceof CtReceiverParameter ctReceiverParameter) {
			e.setReceiverParameter(ctReceiverParameter);
		}
		super.scanCtExecutable(e);
	}

	@Override
	public void scanCtFormalTypeDeclarer(CtFormalTypeDeclarer e) {
		if (childJDT instanceof TypeParameter && child instanceof CtTypeParameter) {
			e.addFormalCtTypeParameter((CtTypeParameter) child);
		}
	}

	@Override
	public void scanCtLoop(CtLoop loop) {
		if (loop.getBody() == null && child instanceof CtStatement) {
			CtStatement child = (CtStatement) this.child;
			if (!(this.child instanceof CtBlock)) {
				child = jdtTreeBuilder.getFactory().Code().createCtBlock(child);
				child.setImplicit(true);
				child.setPosition(this.child.getPosition());
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
		} else if (child instanceof CtField<?> field) {
			// We add the field in addRecordComponent. Afterward, however, JDT visits the Field itself -> Duplication.
			// To combat this, we delete the existing field and trust JDTs version.
			if (type instanceof CtRecord record && !field.isStatic()) {
				CtField<?> existing = record.getField(field.getSimpleName());
				if (existing != null) {
					record.removeField(existing);
				}
			}
			type.addField(field);
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
		if (jdtTreeBuilder.getContextBuilder().getCurrentNode() instanceof AnnotationMethodDeclaration) {
			final AnnotationMethodDeclaration parent = (AnnotationMethodDeclaration) jdtTreeBuilder.getContextBuilder().getCurrentNode();
			// Default value is equals to the jdt child.
			return parent.defaultValue != null && getFinalExpressionFromCast(parent.defaultValue).equals(childJDT)
					// Return type not yet initialized.
					&& !child.equals(ctVariable.getDefaultExpression());
		}
		final AbstractVariableDeclaration parent = (AbstractVariableDeclaration) jdtTreeBuilder.getContextBuilder().getCurrentNode();
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
		final MethodDeclaration parent = (MethodDeclaration) jdtTreeBuilder.getContextBuilder().getCurrentNode();
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
		final AnnotationMethodDeclaration parent = (AnnotationMethodDeclaration) jdtTreeBuilder.getContextBuilder().getCurrentNode();
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
		CtElement child = operator.getKind() == INSTANCEOF && operator.getLeftHandOperand() != null
			? adjustIfLocalVariableToTypePattern(this.child)
			: this.child;
		if (child instanceof CtExpression) {
			if (operator.getLeftHandOperand() == null) {
				operator.setLeftHandOperand((CtExpression<?>) child);
				return;
			} else if (operator.getRightHandOperand() == null) {
				if (child.getPosition().isValidPosition()) {
					int childEnd = child.getPosition().getSourceEnd();
					SourcePosition oldPos = operator.getPosition();
					if (oldPos.isValidPosition() && oldPos.getSourceEnd() < childEnd) {
						//fix parent position if right hand expression is `x instanceof List<?>` which has bad sourceEnd ending before `<?>
						int[] lineSeparatorPositions = jdtTreeBuilder.getContextBuilder().getCompilationUnitLineSeparatorPositions();
						operator.setPosition(operator.getFactory().Core().createSourcePosition(
								oldPos.getCompilationUnit(),
								oldPos.getSourceStart(), childEnd,
								lineSeparatorPositions));
					}
				}
				operator.setRightHandOperand((CtExpression<?>) child);
				return;
			} else if (jdtTreeBuilder.getContextBuilder().getCurrentNode() instanceof StringLiteralConcatenation) {
				CtBinaryOperator<?> op = operator.getFactory().Core().createBinaryOperator();
				op.setKind(BinaryOperatorKind.PLUS);
				op.setLeftHandOperand(operator.getLeftHandOperand());
				op.setRightHandOperand(operator.getRightHandOperand());
				op.setType(operator.getFactory().Type().stringType());
				operator.setLeftHandOperand(op);
				operator.setRightHandOperand(((CtExpression<?>) child));
				int[] lineSeparatorPositions = jdtTreeBuilder.getContextBuilder().getCompilationUnitLineSeparatorPositions();
				SourcePosition leftPosition = op.getLeftHandOperand().getPosition();
				SourcePosition rightPosition = op.getRightHandOperand().getPosition();
				op.setPosition(op.getFactory().createSourcePosition(leftPosition.getCompilationUnit(), leftPosition.getSourceStart(), rightPosition.getSourceEnd(), lineSeparatorPositions));
				return;
			}
		}
		super.visitCtBinaryOperator(operator);
	}

	/**
	 * {@return the original element if it is not a local variable, a type pattern containing the local variable otherwise}
	 *
	 * @param original the original element
	 */
	private CtElement adjustIfLocalVariableToTypePattern(CtElement original) {
		CtElement child = original;
		// check if this is a type pattern, as it needs special treatment
		// patterns are only allowed for instanceof and on the right hand
		if (child instanceof CtLocalVariable) {
			CtTypePattern typePattern = child.getFactory().Core().createTypePattern();
			typePattern.setVariable((CtLocalVariable<?>) child);
			// as we create the type pattern just here, we need to set its source position - which is luckily the same
			typePattern.setPosition(child.getPosition());
			child = typePattern; // replace the local variable with a pattern (which is a CtExpression)
		}
		return child;
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
	public void visitCtBreak(CtBreak b) {
		super.visitCtBreak(b);
	}

	@Override
	public <E> void visitCtCase(CtCase<E> caseStatement) {
		final ASTNode node = jdtTreeBuilder.getContextBuilder().getCurrentNode();
		if (node instanceof CaseStatement cs) {
			caseStatement.setCaseKind(cs.isSwitchRule ? CaseKind.ARROW : CaseKind.COLON);
		}
		if (shouldAddAsCaseExpression(caseStatement, node)) {
			if (child instanceof CtPattern pattern) {
				caseStatement.addCaseExpression((CtExpression<E>) jdtTreeBuilder.getFactory().Core().createCasePattern().setPattern(pattern));
			} else {
				caseStatement.addCaseExpression((CtExpression<E>) child);
			}
			return;
		} else if (child instanceof CtStatement) {
			caseStatement.addStatement((CtStatement) child);
			return;
		} else if (child instanceof CtExpression<?> guard) {
			caseStatement.setGuard(guard);
		}
		super.visitCtCase(caseStatement);
	}

	private <E> boolean shouldAddAsCaseExpression(CtCase<E> caseStatement, ASTNode node) {
		if (!(node instanceof CaseStatement cs)) {
			return false;
		}
		if (cs.constantExpressions == null) {
			return false;
		}
		if (child instanceof CtExpression
			&& caseStatement.getCaseExpressions().size() < cs.constantExpressions.length) {
			return true;
		}
		// case A _, B _ -> {} is only one constantExpression in JDT, but an EitherOrMultiPattern
		// so we need to unpack it and see how many case expressions it actually is
		if (cs.constantExpressions.length == 1 && cs.constantExpressions[0] instanceof EitherOrMultiPattern eomp) {
			// returns true if we still expect more case expressions to be added
			return caseStatement.getCaseExpressions().size() < eomp.getAlternatives().length;
		}
		return false;
	}

	@Override
	public void visitCtCatch(CtCatch catchBlock) {
		if (child instanceof CtBlock) {
			catchBlock.setBody((CtBlock<?>) child);
			return;
		} else if (child instanceof CtCatchVariable) {
			catchBlock.setParameter((CtCatchVariable<? extends Throwable>) child);
			// Catch annotations are processed before actual CtCatchVariable is created and because of that they attach to CtCatch.
			// Since annotations cannot be attached to CtCatch itself, we can simply transfer them to CtCatchVariable.
			catchBlock.getAnnotations().forEach(a -> { a.setParent(child); child.addAnnotation(a); });
			catchBlock.setAnnotations(List.of());
			return;
		}
		super.visitCtCatch(catchBlock);
	}

	@Override
	public <T> void visitCtCatchVariable(CtCatchVariable<T> e) {
		if (jdtTreeBuilder.getContextBuilder().getCurrentNode() instanceof UnionTypeReference) {
			e.addMultiType((CtTypeReference<?>) child);
			return;
		}
		super.visitCtCatchVariable(e);
	}

	@Override
	public void visitCtCasePattern(CtCasePattern casePattern) {
		if (child instanceof CtPattern pattern) {
			casePattern.setPattern(pattern);
		}
		super.visitCtCasePattern(casePattern);
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		if (child instanceof CtConstructor) {
			CtConstructor<T> constructor = (CtConstructor<T>) child;
			ctClass.addConstructor(constructor);
			fixJdtEnumConstructorSuperCall(ctClass, constructor);
		}
		if (child instanceof CtAnonymousExecutable) {
			ctClass.addAnonymousExecutable((CtAnonymousExecutable) child);
		}
		super.visitCtClass(ctClass);
	}

	private <T> void fixJdtEnumConstructorSuperCall(CtClass<T> ctClass, CtConstructor<T> constructor) {
		// For some reason JDT inserts a `super()` call in implicit enum constructors.
		// Explicit super calls are forbidden as java.lang.Enum subclasses are permitted by the JLS to delegate
		// to the Enum constructor in whatever way they like.
		// The constructor is implicit so this isn't *technically* illegal, but it doesn't really make much sense
		// as explicit constructors can never contain such a call. Additionally, the Enum class from the standard
		// library has a "String, int" constructor, rendering the parameterless supercall semantically invalid.
		// We just remove the call to make it a bit more consistent.
		// See https://github.com/INRIA/spoon/issues/4758 for more details.
		if (!child.isImplicit() || !ctClass.isEnum() || !constructor.getParameters().isEmpty()) {
			return;
		}
		if (constructor.getBody().getStatements().isEmpty()) {
			return;
		}
		if (!(constructor.getBody().getStatement(0) instanceof CtInvocation)) {
			return;
		}

		CtInvocation<?> superCall = constructor.getBody().getStatement(0);
		if (superCall.getExecutable().getSimpleName().equals("<init>")) {
			constructor.getBody().removeStatement(superCall);
		}
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
		} else if (isContainedInForCondition() && child instanceof CtExpression) {
			forLoop.setExpression((CtExpression<Boolean>) child);
			return;
		}
		super.visitCtFor(forLoop);
	}

	private boolean isContainedInForInit() {
		if (!(jdtTreeBuilder.getContextBuilder().getCurrentNode() instanceof ForStatement)) {
			return false;
		}
		final ForStatement parent = (ForStatement) jdtTreeBuilder.getContextBuilder().getCurrentNode();
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
		if (!(jdtTreeBuilder.getContextBuilder().getCurrentNode() instanceof ForStatement)) {
			return false;
		}
		final ForStatement parent = (ForStatement) jdtTreeBuilder.getContextBuilder().getCurrentNode();
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

	private boolean isContainedInForCondition() {
		if (!(jdtTreeBuilder.getContextBuilder().getCurrentNode() instanceof ForStatement)) {
			return false;
		}
		final ForStatement parent = (ForStatement) jdtTreeBuilder.getContextBuilder().getCurrentNode();
		return parent.condition != null && parent.condition.equals(childJDT);
	}

	@Override
	public void visitCtForEach(CtForEach foreach) {
		if (foreach.getVariable() == null && child instanceof CtLocalVariable<?>) {
			foreach.setVariable((CtLocalVariable<?>) child);
		} else if (foreach.getExpression() == null && child instanceof CtExpression) {
			foreach.setExpression((CtExpression<?>) child);
		} else {
			super.visitCtForEach(foreach);
		}
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
			// we create implicit blocks everywhere for facilitating transformation
			if (!(this.child instanceof CtBlock)) {
				child = jdtTreeBuilder.getFactory().Code().createCtBlock(child);
				child.setImplicit(true);
				child.setPosition(this.child.getPosition());
			}

			IfStatement ifJDT = (IfStatement) this.parentPair.node();
			if (ifJDT.thenStatement == this.childJDT) {
				//we are visiting `then` of `if`
				ifElement.setThenStatement(child);
				return;
			} else if (ifJDT.elseStatement == this.childJDT) {
				//we are visiting `else` of `if`
				ifElement.setElseStatement(child);
				return;
			} else {
				throw new SpoonException("Unexpected call of ParentExiter on CtIf");
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
					if (!setTargetFromUnqualifiedAccess(invocation)) {
						final CtTypeReference<?> declaringType = invocation.getExecutable().getDeclaringType();
						if (declaringType != null && invocation.getExecutable().isStatic() && child.isImplicit()) {
							invocation.setTarget(jdtTreeBuilder.getFactory().Code().createTypeAccess(declaringType, true));
						} else {
							invocation.setTarget((CtThisAccess<?>) child);
						}
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

	private <T> boolean setTargetFromUnqualifiedAccess(CtInvocation<T> invocation) {
		// A call to a statically imported method (e.g. assertTrue(false)) is modelled as
		// "this.assertTrue(false)" by JDT. We need to unscramble that heuristically and replace the
		// "this" reference with the correct type (e.g. org.junit.api.Assertions)

		// Additionally, references to methods of enclosing classes are also modelled as "this" by JDT.
		// Compare with Test "correctlySetsThisTargetForUnqualifiedCalls".

		// We need a MessageSend as the parent to resolve the actualType from the receiver
		if (!(parentPair.node() instanceof MessageSend messageSend)) {
			return false;
		}
		if (messageSend.actualReceiverType == null || messageSend.receiver.resolvedType == null) {
			return false;
		}

		ReferenceBuilder referenceBuilder = jdtTreeBuilder.getReferencesBuilder();
		CtTypeReference<?> actualReceiverType = referenceBuilder.getTypeReference(messageSend.actualReceiverType);
		CtTypeReference<?> resolvedReceiverType = referenceBuilder.getTypeReference(messageSend.receiver.resolvedType);

		// If they match we have a normal "this" reference
		if (actualReceiverType.equals(resolvedReceiverType)) {
			return false;
		}

		if (messageSend.binding() == null || !messageSend.binding().isStatic()) {
			// Emulate outer this access
			while (resolvedReceiverType != null) {
				resolvedReceiverType = resolvedReceiverType.getDeclaringType();
				if (actualReceiverType.equals(resolvedReceiverType)) {
					invocation.setTarget(jdtTreeBuilder.getFactory().Code().createThisAccess(actualReceiverType, true));
					return true;
				}
			}
			// I don't think this can happen but let's be conservative and preserve the previous behaviour
			return false;
		}

		// If not, we probably had a static import/static outer method reference here and should use the actual type
		// instead
		invocation.setTarget(jdtTreeBuilder.getFactory().Code().createTypeAccess(actualReceiverType, true));
		return true;
	}

	private <T> boolean hasChildEqualsToQualification(CtInvocation<T> ctInvocation) {
		if (!(jdtTreeBuilder.getContextBuilder().getCurrentNode() instanceof ExplicitConstructorCall)) {
			return false;
		}
		final ExplicitConstructorCall parent = (ExplicitConstructorCall) jdtTreeBuilder.getContextBuilder().getCurrentNode();
		// qualification is equals to the jdt child.
		return parent.qualification != null && getFinalExpressionFromCast(parent.qualification).equals(childJDT)
				// qualification not yet initialized.
				&& !child.equals(ctInvocation.getTarget());
	}

	private <T> boolean hasChildEqualsToReceiver(CtInvocation<T> ctInvocation) {
		if (!(jdtTreeBuilder.getContextBuilder().getCurrentNode() instanceof MessageSend)) {
			return false;
		}
		final MessageSend parent = (MessageSend) jdtTreeBuilder.getContextBuilder().getCurrentNode();
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
			final ArrayAllocationExpression arrayAlloc = (ArrayAllocationExpression) jdtTreeBuilder.getContextBuilder().getCurrentNode();
			newArray.setType((CtArrayTypeReference) jdtTreeBuilder.getFactory().Type().createArrayReference(((CtTypeAccess) child).getAccessedType(), arrayAlloc.dimensions.length));
		} else if (child instanceof CtExpression) {
			if (isContainedInDimensionExpression()) {
				newArray.addDimensionExpression((CtExpression<Integer>) child);
			} else if (child instanceof CtNewArray && childJDT instanceof ArrayInitializer && jdtTreeBuilder.getContextBuilder().getCurrentNode() instanceof ArrayAllocationExpression) {
				newArray.setElements(((CtNewArray) child).getElements());
			} else {
				newArray.addElement((CtExpression) child);
			}
		}
	}

	private boolean isContainedInDimensionExpression() {
		if (!(jdtTreeBuilder.getContextBuilder().getCurrentNode() instanceof ArrayAllocationExpression)) {
			return false;
		}
		final ArrayAllocationExpression parent = (ArrayAllocationExpression) jdtTreeBuilder.getContextBuilder().getCurrentNode();
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
		if (!(jdtTreeBuilder.getContextBuilder().getCurrentNode() instanceof QualifiedAllocationExpression)) {
			return false;
		}
		final QualifiedAllocationExpression parent = (QualifiedAllocationExpression) jdtTreeBuilder.getContextBuilder().getCurrentNode();
		// Enclosing instance is equals to the jdt child.
		return parent.enclosingInstance != null && getFinalExpressionFromCast(parent.enclosingInstance).equals(childJDT)
				// Enclosing instance not yet initialized.
				&& !child.equals(ctConstructorCall.getTarget());
	}

	private <T> boolean hasChildEqualsToType(CtConstructorCall<T> ctConstructorCall) {
		final AllocationExpression parent = (AllocationExpression) jdtTreeBuilder.getContextBuilder().getCurrentNode();
		// Type is equals to the jdt child.
		return parent.type != null && parent.type.equals(childJDT);
	}


	@Override
	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		if (child instanceof CtClass) {
			newClass.setAnonymousClass((CtClass<?>) child);
			final QualifiedAllocationExpression node = (QualifiedAllocationExpression) jdtTreeBuilder.getContextBuilder().getCurrentNode();
			final ReferenceBinding[] referenceBindings = node.resolvedType == null ? null : node.resolvedType.superInterfaces();
			if (referenceBindings != null && referenceBindings.length > 0) {
				//the interface of anonymous class is not printed so it must have no position
				//note: the interface is sometimes already assigned so call setSuperInterfaces to replace it
				((CtClass<?>) child).setSuperInterfaces(Collections.singleton(cloneAsImplicit(newClass.getType())));
			} else if (newClass.getType() != null) {
				//the super class of anonymous class is not printed so it must have no position
				((CtClass<?>) child).setSuperclass(cloneAsImplicit(newClass.getType()));
			}
			return;
		}
		super.visitCtNewClass(newClass);
	}

	private <T extends CtElement> T cloneAsImplicit(T ele) {
		ele = (T) ele.clone();
		ele.accept(new CtScanner() {
			@Override
			protected void enter(CtElement e) {
				e.setPosition(SourcePosition.NOPOSITION);
			}
		});
		ele.setImplicit(true);
		return ele;
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
		} else if (child instanceof CtTypeParameterReference) {
			expression.getExecutable().addActualTypeArgument((CtTypeReference<?>) child);
		}
		super.visitCtExecutableReferenceExpression(expression);
	}

	@Override
	public void visitCtPackage(CtPackage ctPackage) {
		if (child instanceof CtType) {
			CtType<?> type = (CtType<?>) child;
			if (ctPackage.getTypes().contains(type)) {
				ctPackage.removeType(type);
			}
			ctPackage.addType(type);
			CompilationUnit cu = type.getPosition().getCompilationUnit();
			if (cu != null) {
				cu.addDeclaredType(type);
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
			//we have all statements of the case. Update source position now
			child.setPosition(jdtTreeBuilder.getPositionBuilder().buildPosition((CtCase<E>) child));
			return;
		}
		super.visitCtSwitch(switchStatement);
	}

	@Override
	public <T, S> void visitCtSwitchExpression(CtSwitchExpression<T, S> switchExpression) {
		if (switchExpression.getSelector() == null && child instanceof CtExpression) {
			switchExpression.setSelector((CtExpression<S>) child);
			return;
		}
		if (child instanceof CtCase) {
			switchExpression.addCase((CtCase<S>) child);
			//we have all statements of the case. Update source position now
			child.setPosition(jdtTreeBuilder.getPositionBuilder().buildPosition((CtCase<S>) child));
			return;
		}
		super.visitCtSwitchExpression(switchExpression);
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
			CtCatch lastCatcher = getLastCatcher(tryBlock);
			if (lastCatcher != null && lastCatcher.getBody() == null) {
				lastCatcher.setBody(childBlock);
				//we have finally all the information needed to build full position of CtCatch element
				lastCatcher.setPosition(jdtTreeBuilder.getPositionBuilder().buildPosition(lastCatcher));
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

	/**
	 * @param tryBlock
	 * @return last CtCatch of `tryBlock` or null
	 */
	private @Nullable CtCatch getLastCatcher(CtTry tryBlock) {
		List<CtCatch> catchers = tryBlock.getCatchers();
		int nrCatchers = catchers.size();
		if (nrCatchers > 0) {
			return catchers.get(nrCatchers - 1);
		}
		return null;
	}

	@Override
	public void visitCtTryWithResource(CtTryWithResource tryWithResource) {
		if (child instanceof CtLocalVariable<?> var) {
			// normal, happy path of declaring a new variable
			tryWithResource.addResource(var);
		} else if (child instanceof CtVariableRead<?> read) {
			tryWithResource.addResource(read);
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
	public void visitCtWildcardReference(CtWildcardReference e) {
		if (childJDT instanceof TypeReference && child instanceof CtTypeAccess) {
			e.setBoundingType(((CtTypeAccess) child).getAccessedType());
		}
		super.visitCtWildcardReference(e);
	}

	@Override
	public void visitCtYieldStatement(CtYieldStatement e) {
		if (child instanceof CtExpression) {
			e.setExpression((CtExpression<?>) child);
			if (e.isImplicit()) {
				e.setPosition(child.getPosition());
			}
			return;
		}
		super.visitCtYieldStatement(e);
	}

	@Override
	public void visitCtTypePattern(CtTypePattern pattern) {
		if (child instanceof CtLocalVariable) {
			pattern.setVariable((CtLocalVariable<?>) child);
		}
		super.visitCtTypePattern(pattern);
	}

	@Override
	public void visitCtRecord(CtRecord recordType) {
		if (child instanceof CtConstructor newConstructor) {
			adjustConstructors(recordType, newConstructor);
		}
		if (child instanceof CtAnonymousExecutable) {
			recordType.addAnonymousExecutable((CtAnonymousExecutable) child);
		}
		if (child instanceof CtRecordComponent) {
			((CtRecord) recordType).addRecordComponent((CtRecordComponent) child);
		}
		super.visitCtRecord(recordType);
	}

	@Override
	public void visitCtRecordPattern(CtRecordPattern pattern) {
		CtElement child = adjustIfLocalVariableToTypePattern(this.child);
		if (child instanceof CtTypeReference<?> typeReference) {
			// JDTTreeBuilder#visit(SingleTypeReference wraps the child in a CtTypeAccess later on,
			// replacing its parent. Therefore, we need to use a clone for this otherwise the typeReference
			// has two different parents (one wins and the model is inconsistent).
			pattern.setRecordType(typeReference.clone());
		} else if (child instanceof CtPattern innerPattern) {
			pattern.addPattern(innerPattern);
		}
	}

	/**
	 * Modifies the set of constructors of a {@link CtRecord} instance based on the properties of a new constructor.
	 * <p>
	 * This method performs the following operations:
	 * <p>
	 * - If the new constructor is implicit, the method checks against all constructors in the record. If a constructor
	 * with matching parameters is found, the function returns without adding the new constructor.
	 * <p>
	 * - If the new constructor is not implicit, the method traverses the existing constructors of the record. If any
	 * implicit constructor with matching parameters is found, it's removed from the record.
	 * <p>
	 * - If constructor to be added passes the conditions above, or no matching parameters are found, it gets added to the record.
	 *
	 * @param recordType     The {@link CtRecord} instance to which the new constructor might be added.
	 * @param newConstructor The new constructor that should be added to the record, contingent on certain conditions.
	 */
	private static void adjustConstructors(CtRecord recordType, CtConstructor<Object> newConstructor) {
		if (newConstructor.isImplicit()) {
			for (CtConstructor<Object> constructor : recordType.getConstructors()) {
				if (hasSameParameters(newConstructor, constructor)) {
					return;
				}
			}
		} else {
			for (CtConstructor<Object> constructor : recordType.getConstructors()) {
				if (constructor.isImplicit() && hasSameParameters(newConstructor, constructor)) {
					recordType.removeConstructor(constructor);
				}
			}
		}
		recordType.addConstructor(newConstructor);
	}

	private static boolean hasSameParameters(CtConstructor<Object> newConstructor, CtConstructor<Object> constructor) {
		// use endsWith because constructor already has a declaring type set while newConstructor doesn't
		// but we are only interested in the parameters, so we compare e.g. "R(int)" with "(int)"
		return constructor.getSignature().endsWith(newConstructor.getSignature());
	}

	@Override
	public void visitCtRecordComponent(CtRecordComponent recordComponent) {
		if (childJDT instanceof TypeReference && child instanceof CtTypeAccess) {
			recordComponent.setType(((CtTypeAccess) child).getAccessedType());
			substituteAnnotation((CtTypedElement) recordComponent);
			return;
		}
		scanCtElement(recordComponent);
	}
}
