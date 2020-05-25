/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;


import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;

/**
 * This visitor implements a deep-search scan on the model.
 *
 * Ensures that all children nodes are visited once, a visit means three method
 * calls, one call to "enter", one call to "exit" and one call to scan.
 *
 * Is used by the processing and filtering engine.
 */
public abstract class CtScanner implements CtVisitor {
	/**
	 * Default constructor.
	 */
	public CtScanner() {
	}

	/**
	 * This method is upcalled by the scanner when entering a scanned element.
	 * To be overridden to implement specific scanners.
	 */
	protected void enter(CtElement e) {
	}

	/**
	 * This method is upcalled by the scanner when exiting a scanned element. To
	 * be overridden to implement specific scanners.
	 */
	protected void exit(CtElement e) {
	}

	/**
	 * Generically scans a collection of meta-model elements.
	 */
	public void scan(CtRole role, Collection<? extends CtElement> elements) {
		if (elements != null) {
			// we use defensive copy so as to be able to change the class while scanning
			// otherwise one gets a ConcurrentModificationException
			for (CtElement e : new ArrayList<>(elements)) {
				scan(role, e);
			}
		}
	}

	/**
	 * Generically scans a Map of meta-model elements.
	 */
	public void scan(CtRole role, Map<String, ? extends CtElement> elements) {
		if (elements != null) {
			for (CtElement obj : elements.values()) {
				scan(role, obj);
			}
		}
	}

	/**
	 * Generically scans a collection of meta-model elements.
	 */
	public void scan(Collection<? extends CtElement> elements) {
		scan(null, elements);
	}

	/**
	 * Generically scans a meta-model element.
	 */
	public void scan(CtRole role, CtElement element) {
		scan(element);
	}

	/**
	 * Generically scans a meta-model element.
	 */
	public void scan(CtElement element) {
		if (element != null) {
			element.accept(this);
		}
	}

	public <A extends Annotation> void visitCtAnnotation(final CtAnnotation<A> annotation) {
		enter(annotation);
		scan(CtRole.TYPE, annotation.getType());
		scan(CtRole.COMMENT, annotation.getComments());
		scan(CtRole.ANNOTATION_TYPE, annotation.getAnnotationType());
		scan(CtRole.ANNOTATION, annotation.getAnnotations());
		scan(CtRole.VALUE, annotation.getValues());
		exit(annotation);
	}

	/**
	 * Generically scans an object that can be an element, a reference, or a
	 * collection of those.
	 */
	public void scan(Object o) {
		scan(null, o);
	}

	/**
	 * Generically scans an object that can be an element, a reference, or a
	 * collection of those.
	 */
	public void scan(CtRole role, Object o) {
		if (o instanceof CtElement) {
			scan(role, ((CtElement) (o)));
		}
		if (o instanceof Collection<?>) {
			scan(role, (Collection<? extends CtElement>) o);
		}
		if (o instanceof Map<?, ?>) {
			scan(role, (Map<String, ? extends CtElement>) o);
		}
	}

	public <A extends Annotation> void visitCtAnnotationType(final CtAnnotationType<A> annotationType) {
		enter(annotationType);
		scan(CtRole.ANNOTATION, annotationType.getAnnotations());
		scan(CtRole.TYPE_MEMBER, annotationType.getTypeMembers());
		scan(CtRole.COMMENT, annotationType.getComments());
		exit(annotationType);
	}

	public void visitCtAnonymousExecutable(final CtAnonymousExecutable anonymousExec) {
		enter(anonymousExec);
		scan(CtRole.ANNOTATION, anonymousExec.getAnnotations());
		scan(CtRole.BODY, anonymousExec.getBody());
		scan(CtRole.COMMENT, anonymousExec.getComments());
		exit(anonymousExec);
	}

	@Override
	public <T> void visitCtArrayRead(final CtArrayRead<T> arrayRead) {
		enter(arrayRead);
		scan(CtRole.ANNOTATION, arrayRead.getAnnotations());
		scan(CtRole.TYPE, arrayRead.getType());
		scan(CtRole.CAST, arrayRead.getTypeCasts());
		scan(CtRole.TARGET, arrayRead.getTarget());
		scan(CtRole.EXPRESSION, arrayRead.getIndexExpression());
		scan(CtRole.COMMENT, arrayRead.getComments());
		exit(arrayRead);
	}

	@Override
	public <T> void visitCtArrayWrite(final CtArrayWrite<T> arrayWrite) {
		enter(arrayWrite);
		scan(CtRole.ANNOTATION, arrayWrite.getAnnotations());
		scan(CtRole.TYPE, arrayWrite.getType());
		scan(CtRole.CAST, arrayWrite.getTypeCasts());
		scan(CtRole.TARGET, arrayWrite.getTarget());
		scan(CtRole.EXPRESSION, arrayWrite.getIndexExpression());
		scan(CtRole.COMMENT, arrayWrite.getComments());
		exit(arrayWrite);
	}

	public <T> void visitCtArrayTypeReference(final CtArrayTypeReference<T> reference) {
		enter(reference);
		scan(CtRole.PACKAGE_REF, reference.getPackage());
		scan(CtRole.DECLARING_TYPE, reference.getDeclaringType());
		scan(CtRole.TYPE, reference.getComponentType());
		scan(CtRole.TYPE_ARGUMENT, reference.getActualTypeArguments());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtAssert(final CtAssert<T> asserted) {
		enter(asserted);
		scan(CtRole.ANNOTATION, asserted.getAnnotations());
		scan(CtRole.CONDITION, asserted.getAssertExpression());
		scan(CtRole.EXPRESSION, asserted.getExpression());
		scan(CtRole.COMMENT, asserted.getComments());
		exit(asserted);
	}

	public <T, A extends T> void visitCtAssignment(final CtAssignment<T, A> assignement) {
		enter(assignement);
		scan(CtRole.ANNOTATION, assignement.getAnnotations());
		scan(CtRole.TYPE, assignement.getType());
		scan(CtRole.CAST, assignement.getTypeCasts());
		scan(CtRole.ASSIGNED, assignement.getAssigned());
		scan(CtRole.ASSIGNMENT, assignement.getAssignment());
		scan(CtRole.COMMENT, assignement.getComments());
		exit(assignement);
	}

	public <T> void visitCtBinaryOperator(final CtBinaryOperator<T> operator) {
		enter(operator);
		scan(CtRole.ANNOTATION, operator.getAnnotations());
		scan(CtRole.TYPE, operator.getType());
		scan(CtRole.CAST, operator.getTypeCasts());
		scan(CtRole.LEFT_OPERAND, operator.getLeftHandOperand());
		scan(CtRole.RIGHT_OPERAND, operator.getRightHandOperand());
		scan(CtRole.COMMENT, operator.getComments());
		exit(operator);
	}

	public <R> void visitCtBlock(final CtBlock<R> block) {
		enter(block);
		scan(CtRole.ANNOTATION, block.getAnnotations());
		scan(CtRole.STATEMENT, block.getStatements());
		scan(CtRole.COMMENT, block.getComments());
		exit(block);
	}

	public void visitCtBreak(final CtBreak breakStatement) {
		enter(breakStatement);
		scan(CtRole.ANNOTATION, breakStatement.getAnnotations());
		scan(CtRole.COMMENT, breakStatement.getComments());
		exit(breakStatement);
	}

	public <S> void visitCtCase(final CtCase<S> caseStatement) {
		enter(caseStatement);
		scan(CtRole.ANNOTATION, caseStatement.getAnnotations());
		scan(CtRole.EXPRESSION, caseStatement.getCaseExpressions());
		scan(CtRole.STATEMENT, caseStatement.getStatements());
		scan(CtRole.COMMENT, caseStatement.getComments());
		exit(caseStatement);
	}

	public void visitCtCatch(final CtCatch catchBlock) {
		enter(catchBlock);
		scan(CtRole.ANNOTATION, catchBlock.getAnnotations());
		scan(CtRole.PARAMETER, catchBlock.getParameter());
		scan(CtRole.BODY, catchBlock.getBody());
		scan(CtRole.COMMENT, catchBlock.getComments());
		exit(catchBlock);
	}

	public <T> void visitCtClass(final CtClass<T> ctClass) {
		enter(ctClass);
		scan(CtRole.ANNOTATION, ctClass.getAnnotations());
		scan(CtRole.SUPER_TYPE, ctClass.getSuperclass());
		scan(CtRole.INTERFACE, ctClass.getSuperInterfaces());
		scan(CtRole.TYPE_PARAMETER, ctClass.getFormalCtTypeParameters());
		scan(CtRole.TYPE_MEMBER, ctClass.getTypeMembers());
		scan(CtRole.COMMENT, ctClass.getComments());
		exit(ctClass);
	}

	@Override
	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		enter(typeParameter);
		scan(CtRole.ANNOTATION, typeParameter.getAnnotations());
		scan(CtRole.SUPER_TYPE, typeParameter.getSuperclass());
		scan(CtRole.COMMENT, typeParameter.getComments());
		exit(typeParameter);
	}

	public <T> void visitCtConditional(final CtConditional<T> conditional) {
		enter(conditional);
		scan(CtRole.TYPE, conditional.getType());
		scan(CtRole.ANNOTATION, conditional.getAnnotations());
		scan(CtRole.CONDITION, conditional.getCondition());
		scan(CtRole.THEN, conditional.getThenExpression());
		scan(CtRole.ELSE, conditional.getElseExpression());
		scan(CtRole.COMMENT, conditional.getComments());
		scan(CtRole.CAST, conditional.getTypeCasts());
		exit(conditional);
	}

	public <T> void visitCtConstructor(final CtConstructor<T> c) {
		enter(c);
		scan(CtRole.ANNOTATION, c.getAnnotations());
		scan(CtRole.PARAMETER, c.getParameters());
		scan(CtRole.THROWN, c.getThrownTypes());
		scan(CtRole.TYPE_PARAMETER, c.getFormalCtTypeParameters());
		scan(CtRole.BODY, c.getBody());
		scan(CtRole.COMMENT, c.getComments());
		exit(c);
	}

	public void visitCtContinue(final CtContinue continueStatement) {
		enter(continueStatement);
		scan(CtRole.ANNOTATION, continueStatement.getAnnotations());
		scan(CtRole.COMMENT, continueStatement.getComments());
		exit(continueStatement);
	}

	public void visitCtDo(final CtDo doLoop) {
		enter(doLoop);
		scan(CtRole.ANNOTATION, doLoop.getAnnotations());
		scan(CtRole.EXPRESSION, doLoop.getLoopingExpression());
		scan(CtRole.BODY, doLoop.getBody());
		scan(CtRole.COMMENT, doLoop.getComments());
		exit(doLoop);
	}

	public <T extends Enum<?>> void visitCtEnum(final CtEnum<T> ctEnum) {
		enter(ctEnum);
		scan(CtRole.ANNOTATION, ctEnum.getAnnotations());
		scan(CtRole.INTERFACE, ctEnum.getSuperInterfaces());
		scan(CtRole.TYPE_MEMBER, ctEnum.getTypeMembers());
		scan(CtRole.VALUE, ctEnum.getEnumValues());
		scan(CtRole.COMMENT, ctEnum.getComments());
		exit(ctEnum);
	}

	public <T> void visitCtExecutableReference(final CtExecutableReference<T> reference) {
		enter(reference);
		scan(CtRole.DECLARING_TYPE, reference.getDeclaringType());
		scan(CtRole.TYPE, reference.getType());
		scan(CtRole.ARGUMENT_TYPE, reference.getParameters());
		scan(CtRole.TYPE_ARGUMENT, reference.getActualTypeArguments());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
		scan(CtRole.COMMENT, reference.getComments());
		exit(reference);
	}

	public <T> void visitCtField(final CtField<T> f) {
		enter(f);
		scan(CtRole.ANNOTATION, f.getAnnotations());
		scan(CtRole.TYPE, f.getType());
		scan(CtRole.DEFAULT_EXPRESSION, f.getDefaultExpression());
		scan(CtRole.COMMENT, f.getComments());
		exit(f);
	}

	@Override
	public <T> void visitCtEnumValue(final CtEnumValue<T> enumValue) {
		enter(enumValue);
		scan(CtRole.ANNOTATION, enumValue.getAnnotations());
		scan(CtRole.TYPE, enumValue.getType());
		scan(CtRole.DEFAULT_EXPRESSION, enumValue.getDefaultExpression());
		scan(CtRole.COMMENT, enumValue.getComments());
		exit(enumValue);
	}

	@Override
	public <T> void visitCtThisAccess(final CtThisAccess<T> thisAccess) {
		enter(thisAccess);
		scan(CtRole.COMMENT, thisAccess.getComments());
		scan(CtRole.ANNOTATION, thisAccess.getAnnotations());
		scan(CtRole.TYPE, thisAccess.getType());
		scan(CtRole.CAST, thisAccess.getTypeCasts());
		scan(CtRole.TARGET, thisAccess.getTarget());
		exit(thisAccess);
	}

	public <T> void visitCtAnnotationFieldAccess(final CtAnnotationFieldAccess<T> annotationFieldAccess) {
		enter(annotationFieldAccess);
		scan(CtRole.COMMENT, annotationFieldAccess.getComments());
		scan(CtRole.ANNOTATION, annotationFieldAccess.getAnnotations());
		scan(CtRole.CAST, annotationFieldAccess.getTypeCasts());
		scan(CtRole.TARGET, annotationFieldAccess.getTarget());
		scan(CtRole.VARIABLE, annotationFieldAccess.getVariable());
		exit(annotationFieldAccess);
	}

	public <T> void visitCtFieldReference(final CtFieldReference<T> reference) {
		enter(reference);
		scan(CtRole.DECLARING_TYPE, reference.getDeclaringType());
		scan(CtRole.TYPE, reference.getType());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
		exit(reference);
	}

	public void visitCtFor(final CtFor forLoop) {
		enter(forLoop);
		scan(CtRole.ANNOTATION, forLoop.getAnnotations());
		scan(CtRole.FOR_INIT, forLoop.getForInit());
		scan(CtRole.EXPRESSION, forLoop.getExpression());
		scan(CtRole.FOR_UPDATE, forLoop.getForUpdate());
		scan(CtRole.BODY, forLoop.getBody());
		scan(CtRole.COMMENT, forLoop.getComments());
		exit(forLoop);
	}

	public void visitCtForEach(final CtForEach foreach) {
		enter(foreach);
		scan(CtRole.ANNOTATION, foreach.getAnnotations());
		scan(CtRole.FOREACH_VARIABLE, foreach.getVariable());
		scan(CtRole.EXPRESSION, foreach.getExpression());
		scan(CtRole.BODY, foreach.getBody());
		scan(CtRole.COMMENT, foreach.getComments());
		exit(foreach);
	}

	public void visitCtIf(final CtIf ifElement) {
		enter(ifElement);
		scan(CtRole.ANNOTATION, ifElement.getAnnotations());
		scan(CtRole.CONDITION, ifElement.getCondition());
		scan(CtRole.THEN, ((CtStatement) (ifElement.getThenStatement())));
		scan(CtRole.ELSE, ((CtStatement) (ifElement.getElseStatement())));
		scan(CtRole.COMMENT, ifElement.getComments());
		exit(ifElement);
	}

	public <T> void visitCtInterface(final CtInterface<T> intrface) {
		enter(intrface);
		scan(CtRole.ANNOTATION, intrface.getAnnotations());
		scan(CtRole.INTERFACE, intrface.getSuperInterfaces());
		scan(CtRole.TYPE_PARAMETER, intrface.getFormalCtTypeParameters());
		scan(CtRole.TYPE_MEMBER, intrface.getTypeMembers());
		scan(CtRole.COMMENT, intrface.getComments());
		exit(intrface);
	}

	public <T> void visitCtInvocation(final CtInvocation<T> invocation) {
		enter(invocation);
		scan(CtRole.ANNOTATION, invocation.getAnnotations());
		scan(CtRole.CAST, invocation.getTypeCasts());
		scan(CtRole.TARGET, invocation.getTarget());
		scan(CtRole.EXECUTABLE_REF, invocation.getExecutable());
		scan(CtRole.ARGUMENT, invocation.getArguments());
		scan(CtRole.COMMENT, invocation.getComments());
		exit(invocation);
	}

	public <T> void visitCtLiteral(final CtLiteral<T> literal) {
		enter(literal);
		scan(CtRole.ANNOTATION, literal.getAnnotations());
		scan(CtRole.TYPE, literal.getType());
		scan(CtRole.CAST, literal.getTypeCasts());
		scan(CtRole.COMMENT, literal.getComments());
		exit(literal);
	}

	public <T> void visitCtLocalVariable(final CtLocalVariable<T> localVariable) {
		enter(localVariable);
		scan(CtRole.ANNOTATION, localVariable.getAnnotations());
		scan(CtRole.TYPE, localVariable.getType());
		scan(CtRole.DEFAULT_EXPRESSION, localVariable.getDefaultExpression());
		scan(CtRole.COMMENT, localVariable.getComments());
		exit(localVariable);
	}

	public <T> void visitCtLocalVariableReference(final CtLocalVariableReference<T> reference) {
		enter(reference);
		scan(CtRole.TYPE, reference.getType());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtCatchVariable(final CtCatchVariable<T> catchVariable) {
		enter(catchVariable);
		scan(CtRole.COMMENT, catchVariable.getComments());
		scan(CtRole.ANNOTATION, catchVariable.getAnnotations());
		scan(CtRole.MULTI_TYPE, catchVariable.getMultiTypes());
		exit(catchVariable);
	}

	public <T> void visitCtCatchVariableReference(final CtCatchVariableReference<T> reference) {
		enter(reference);
		scan(CtRole.TYPE, reference.getType());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtMethod(final CtMethod<T> m) {
		enter(m);
		scan(CtRole.ANNOTATION, m.getAnnotations());
		scan(CtRole.TYPE_PARAMETER, m.getFormalCtTypeParameters());
		scan(CtRole.TYPE, m.getType());
		scan(CtRole.PARAMETER, m.getParameters());
		scan(CtRole.THROWN, m.getThrownTypes());
		scan(CtRole.BODY, m.getBody());
		scan(CtRole.COMMENT, m.getComments());
		exit(m);
	}

	@Override
	public <T> void visitCtAnnotationMethod(CtAnnotationMethod<T> annotationMethod) {
		enter(annotationMethod);
		scan(CtRole.ANNOTATION, annotationMethod.getAnnotations());
		scan(CtRole.TYPE, annotationMethod.getType());
		scan(CtRole.DEFAULT_EXPRESSION, annotationMethod.getDefaultExpression());
		scan(CtRole.COMMENT, annotationMethod.getComments());
		exit(annotationMethod);
	}

	public <T> void visitCtNewArray(final CtNewArray<T> newArray) {
		enter(newArray);
		scan(CtRole.ANNOTATION, newArray.getAnnotations());
		scan(CtRole.TYPE, newArray.getType());
		scan(CtRole.CAST, newArray.getTypeCasts());
		scan(CtRole.EXPRESSION, newArray.getElements());
		scan(CtRole.DIMENSION, newArray.getDimensionExpressions());
		scan(CtRole.COMMENT, newArray.getComments());
		exit(newArray);
	}

	@Override
	public <T> void visitCtConstructorCall(final CtConstructorCall<T> ctConstructorCall) {
		enter(ctConstructorCall);
		scan(CtRole.ANNOTATION, ctConstructorCall.getAnnotations());
		scan(CtRole.CAST, ctConstructorCall.getTypeCasts());
		scan(CtRole.EXECUTABLE_REF, ctConstructorCall.getExecutable());
		scan(CtRole.TARGET, ctConstructorCall.getTarget());
		scan(CtRole.ARGUMENT, ctConstructorCall.getArguments());
		scan(CtRole.COMMENT, ctConstructorCall.getComments());
		exit(ctConstructorCall);
	}

	public <T> void visitCtNewClass(final CtNewClass<T> newClass) {
		enter(newClass);
		scan(CtRole.ANNOTATION, newClass.getAnnotations());
		scan(CtRole.CAST, newClass.getTypeCasts());
		scan(CtRole.EXECUTABLE_REF, newClass.getExecutable());
		scan(CtRole.TARGET, newClass.getTarget());
		scan(CtRole.ARGUMENT, newClass.getArguments());
		scan(CtRole.NESTED_TYPE, newClass.getAnonymousClass());
		scan(CtRole.COMMENT, newClass.getComments());
		exit(newClass);
	}

	@Override
	public <T> void visitCtLambda(final CtLambda<T> lambda) {
		enter(lambda);
		scan(CtRole.ANNOTATION, lambda.getAnnotations());
		scan(CtRole.TYPE, lambda.getType());
		scan(CtRole.CAST, lambda.getTypeCasts());
		scan(CtRole.PARAMETER, lambda.getParameters());
		scan(CtRole.BODY, lambda.getBody());
		scan(CtRole.EXPRESSION, lambda.getExpression());
		scan(CtRole.COMMENT, lambda.getComments());
		exit(lambda);
	}

	@Override
	public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(final CtExecutableReferenceExpression<T, E> expression) {
		enter(expression);
		scan(CtRole.COMMENT, expression.getComments());
		scan(CtRole.ANNOTATION, expression.getAnnotations());
		scan(CtRole.TYPE, expression.getType());
		scan(CtRole.CAST, expression.getTypeCasts());
		scan(CtRole.EXECUTABLE_REF, expression.getExecutable());
		scan(CtRole.TARGET, expression.getTarget());
		exit(expression);
	}

	public <T, A extends T> void visitCtOperatorAssignment(final CtOperatorAssignment<T, A> assignment) {
		enter(assignment);
		scan(CtRole.ANNOTATION, assignment.getAnnotations());
		scan(CtRole.TYPE, assignment.getType());
		scan(CtRole.CAST, assignment.getTypeCasts());
		scan(CtRole.ASSIGNED, assignment.getAssigned());
		scan(CtRole.ASSIGNMENT, assignment.getAssignment());
		scan(CtRole.COMMENT, assignment.getComments());
		exit(assignment);
	}

	public void visitCtPackage(final CtPackage ctPackage) {
		enter(ctPackage);
		scan(CtRole.ANNOTATION, ctPackage.getAnnotations());
		scan(CtRole.SUB_PACKAGE, ctPackage.getPackages());
		scan(CtRole.CONTAINED_TYPE, ctPackage.getTypes());
		scan(CtRole.COMMENT, ctPackage.getComments());
		exit(ctPackage);
	}

	public void visitCtPackageReference(final CtPackageReference reference) {
		enter(reference);
		scan(CtRole.ANNOTATION, reference.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtParameter(final CtParameter<T> parameter) {
		enter(parameter);
		scan(CtRole.ANNOTATION, parameter.getAnnotations());
		scan(CtRole.TYPE, parameter.getType());
		scan(CtRole.COMMENT, parameter.getComments());
		exit(parameter);
	}

	public <T> void visitCtParameterReference(final CtParameterReference<T> reference) {
		enter(reference);
		scan(CtRole.TYPE, reference.getType());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
		exit(reference);
	}

	public <R> void visitCtReturn(final CtReturn<R> returnStatement) {
		enter(returnStatement);
		scan(CtRole.ANNOTATION, returnStatement.getAnnotations());
		scan(CtRole.EXPRESSION, returnStatement.getReturnedExpression());
		scan(CtRole.COMMENT, returnStatement.getComments());
		exit(returnStatement);
	}

	public <R> void visitCtStatementList(final CtStatementList statements) {
		enter(statements);
		scan(CtRole.ANNOTATION, statements.getAnnotations());
		scan(CtRole.STATEMENT, statements.getStatements());
		scan(CtRole.COMMENT, statements.getComments());
		exit(statements);
	}

	public <S> void visitCtSwitch(final CtSwitch<S> switchStatement) {
		enter(switchStatement);
		scan(CtRole.ANNOTATION, switchStatement.getAnnotations());
		scan(CtRole.EXPRESSION, switchStatement.getSelector());
		scan(CtRole.CASE, switchStatement.getCases());
		scan(CtRole.COMMENT, switchStatement.getComments());
		exit(switchStatement);
	}

	public <T, S> void visitCtSwitchExpression(final CtSwitchExpression<T, S> switchExpression) {
		enter(switchExpression);
		scan(CtRole.ANNOTATION, switchExpression.getAnnotations());
		scan(CtRole.EXPRESSION, switchExpression.getSelector());
		scan(CtRole.CASE, switchExpression.getCases());
		scan(CtRole.COMMENT, switchExpression.getComments());
		scan(CtRole.TYPE, switchExpression.getType());
		scan(CtRole.CAST, switchExpression.getTypeCasts());
		exit(switchExpression);
	}

	public void visitCtSynchronized(final CtSynchronized synchro) {
		enter(synchro);
		scan(CtRole.ANNOTATION, synchro.getAnnotations());
		scan(CtRole.EXPRESSION, synchro.getExpression());
		scan(CtRole.BODY, synchro.getBlock());
		scan(CtRole.COMMENT, synchro.getComments());
		exit(synchro);
	}

	public void visitCtThrow(final CtThrow throwStatement) {
		enter(throwStatement);
		scan(CtRole.ANNOTATION, throwStatement.getAnnotations());
		scan(CtRole.EXPRESSION, throwStatement.getThrownExpression());
		scan(CtRole.COMMENT, throwStatement.getComments());
		exit(throwStatement);
	}

	public void visitCtTry(final CtTry tryBlock) {
		enter(tryBlock);
		scan(CtRole.ANNOTATION, tryBlock.getAnnotations());
		scan(CtRole.BODY, tryBlock.getBody());
		scan(CtRole.CATCH, tryBlock.getCatchers());
		scan(CtRole.FINALIZER, tryBlock.getFinalizer());
		scan(CtRole.COMMENT, tryBlock.getComments());
		exit(tryBlock);
	}

	@Override
	public void visitCtTryWithResource(final CtTryWithResource tryWithResource) {
		enter(tryWithResource);
		scan(CtRole.ANNOTATION, tryWithResource.getAnnotations());
		scan(CtRole.TRY_RESOURCE, tryWithResource.getResources());
		scan(CtRole.BODY, tryWithResource.getBody());
		scan(CtRole.CATCH, tryWithResource.getCatchers());
		scan(CtRole.FINALIZER, tryWithResource.getFinalizer());
		scan(CtRole.COMMENT, tryWithResource.getComments());
		exit(tryWithResource);
	}

	public void visitCtTypeParameterReference(final CtTypeParameterReference ref) {
		enter(ref);
		scan(CtRole.PACKAGE_REF, ref.getPackage());
		scan(CtRole.DECLARING_TYPE, ref.getDeclaringType());
		scan(CtRole.ANNOTATION, ref.getAnnotations());
		exit(ref);
	}

	@Override
	public void visitCtWildcardReference(CtWildcardReference wildcardReference) {
		enter(wildcardReference);
		scan(CtRole.PACKAGE_REF, wildcardReference.getPackage());
		scan(CtRole.DECLARING_TYPE, wildcardReference.getDeclaringType());
		scan(CtRole.ANNOTATION, wildcardReference.getAnnotations());
		scan(CtRole.BOUNDING_TYPE, wildcardReference.getBoundingType());
		exit(wildcardReference);
	}

	@Override
	public <T> void visitCtIntersectionTypeReference(final CtIntersectionTypeReference<T> reference) {
		enter(reference);
		scan(CtRole.PACKAGE_REF, reference.getPackage());
		scan(CtRole.DECLARING_TYPE, reference.getDeclaringType());
		// TypeReferenceTest fails if actual type arguments are really not set-able on CtIntersectionTypeReference
		scan(CtRole.TYPE_ARGUMENT, reference.getActualTypeArguments());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
		scan(CtRole.BOUND, reference.getBounds());
		exit(reference);
	}

	public <T> void visitCtTypeReference(final CtTypeReference<T> reference) {
		enter(reference);
		scan(CtRole.PACKAGE_REF, reference.getPackage());
		scan(CtRole.DECLARING_TYPE, reference.getDeclaringType());
		scan(CtRole.TYPE_ARGUMENT, reference.getActualTypeArguments());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
		scan(CtRole.COMMENT, reference.getComments());
		exit(reference);
	}

	@Override
	public <T> void visitCtTypeAccess(final CtTypeAccess<T> typeAccess) {
		enter(typeAccess);
		scan(CtRole.ANNOTATION, typeAccess.getAnnotations());
		scan(CtRole.CAST, typeAccess.getTypeCasts());
		scan(CtRole.ACCESSED_TYPE, typeAccess.getAccessedType());
		scan(CtRole.COMMENT, typeAccess.getComments());
		exit(typeAccess);
	}

	public <T> void visitCtUnaryOperator(final CtUnaryOperator<T> operator) {
		enter(operator);
		scan(CtRole.ANNOTATION, operator.getAnnotations());
		scan(CtRole.TYPE, operator.getType());
		scan(CtRole.CAST, operator.getTypeCasts());
		scan(CtRole.EXPRESSION, operator.getOperand());
		scan(CtRole.COMMENT, operator.getComments());
		exit(operator);
	}

	@Override
	public <T> void visitCtVariableRead(final CtVariableRead<T> variableRead) {
		enter(variableRead);
		scan(CtRole.ANNOTATION, variableRead.getAnnotations());
		scan(CtRole.CAST, variableRead.getTypeCasts());
		scan(CtRole.VARIABLE, variableRead.getVariable());
		scan(CtRole.COMMENT, variableRead.getComments());
		exit(variableRead);
	}

	@Override
	public <T> void visitCtVariableWrite(final CtVariableWrite<T> variableWrite) {
		enter(variableWrite);
		scan(CtRole.ANNOTATION, variableWrite.getAnnotations());
		scan(CtRole.CAST, variableWrite.getTypeCasts());
		scan(CtRole.VARIABLE, variableWrite.getVariable());
		scan(CtRole.COMMENT, variableWrite.getComments());
		exit(variableWrite);
	}

	public void visitCtWhile(final CtWhile whileLoop) {
		enter(whileLoop);
		scan(CtRole.ANNOTATION, whileLoop.getAnnotations());
		scan(CtRole.EXPRESSION, whileLoop.getLoopingExpression());
		scan(CtRole.BODY, whileLoop.getBody());
		scan(CtRole.COMMENT, whileLoop.getComments());
		exit(whileLoop);
	}

	public <T> void visitCtCodeSnippetExpression(final CtCodeSnippetExpression<T> expression) {
		enter(expression);
		scan(CtRole.TYPE, expression.getType());
		scan(CtRole.COMMENT, expression.getComments());
		scan(CtRole.ANNOTATION, expression.getAnnotations());
		scan(CtRole.CAST, expression.getTypeCasts());
		exit(expression);
	}

	public void visitCtCodeSnippetStatement(final CtCodeSnippetStatement statement) {
		enter(statement);
		scan(CtRole.COMMENT, statement.getComments());
		scan(CtRole.ANNOTATION, statement.getAnnotations());
		exit(statement);
	}

	public <T> void visitCtUnboundVariableReference(final CtUnboundVariableReference<T> reference) {
		enter(reference);
		scan(CtRole.TYPE, reference.getType());
		exit(reference);
	}

	@Override
	public <T> void visitCtFieldRead(final CtFieldRead<T> fieldRead) {
		enter(fieldRead);
		scan(CtRole.ANNOTATION, fieldRead.getAnnotations());
		scan(CtRole.CAST, fieldRead.getTypeCasts());
		scan(CtRole.TARGET, fieldRead.getTarget());
		scan(CtRole.VARIABLE, fieldRead.getVariable());
		scan(CtRole.COMMENT, fieldRead.getComments());
		exit(fieldRead);
	}

	@Override
	public <T> void visitCtFieldWrite(final CtFieldWrite<T> fieldWrite) {
		enter(fieldWrite);
		scan(CtRole.ANNOTATION, fieldWrite.getAnnotations());
		scan(CtRole.CAST, fieldWrite.getTypeCasts());
		scan(CtRole.TARGET, fieldWrite.getTarget());
		scan(CtRole.VARIABLE, fieldWrite.getVariable());
		scan(CtRole.COMMENT, fieldWrite.getComments());
		exit(fieldWrite);
	}

	@Override
	public <T> void visitCtSuperAccess(final CtSuperAccess<T> f) {
		enter(f);
		scan(CtRole.COMMENT, f.getComments());
		scan(CtRole.ANNOTATION, f.getAnnotations());
		scan(CtRole.CAST, f.getTypeCasts());
		scan(CtRole.TARGET, f.getTarget());
		scan(CtRole.VARIABLE, f.getVariable());
		exit(f);
	}

	@Override
	public void visitCtComment(final CtComment comment) {
		enter(comment);
		scan(CtRole.COMMENT, comment.getComments());
		scan(CtRole.ANNOTATION, comment.getAnnotations());
		exit(comment);
	}

	@Override
	public void visitCtJavaDoc(final CtJavaDoc javaDoc) {
		enter(javaDoc);
		scan(CtRole.COMMENT, javaDoc.getComments());
		scan(CtRole.ANNOTATION, javaDoc.getAnnotations());
		scan(CtRole.COMMENT_TAG, javaDoc.getTags());
		exit(javaDoc);
	}

	@Override
	public void visitCtJavaDocTag(final CtJavaDocTag docTag) {
		enter(docTag);
		scan(CtRole.COMMENT, docTag.getComments());
		scan(CtRole.ANNOTATION, docTag.getAnnotations());
		exit(docTag);
	}

	@Override
	public void visitCtImport(final CtImport ctImport) {
		enter(ctImport);
		scan(CtRole.IMPORT_REFERENCE, ctImport.getReference());
		scan(CtRole.ANNOTATION, ctImport.getAnnotations());
		scan(CtRole.COMMENT, ctImport.getComments());
		exit(ctImport);
	}

	@Override
	public void visitCtModule(CtModule module) {
		enter(module);
		scan(CtRole.COMMENT, module.getComments());
		scan(CtRole.ANNOTATION, module.getAnnotations());
		scan(CtRole.MODULE_DIRECTIVE, module.getModuleDirectives());
		scan(CtRole.SUB_PACKAGE, module.getRootPackage());
		exit(module);
	}

	@Override
	public void visitCtModuleReference(CtModuleReference moduleReference) {
		enter(moduleReference);
		scan(CtRole.ANNOTATION, moduleReference.getAnnotations());
		exit(moduleReference);
	}

	@Override
	public void visitCtPackageExport(CtPackageExport moduleExport) {
		enter(moduleExport);
		scan(CtRole.COMMENT, moduleExport.getComments());
		scan(CtRole.PACKAGE_REF, moduleExport.getPackageReference());
		scan(CtRole.MODULE_REF, moduleExport.getTargetExport());
		scan(CtRole.ANNOTATION, moduleExport.getAnnotations());
		exit(moduleExport);
	}

	@Override
	public void visitCtModuleRequirement(CtModuleRequirement moduleRequirement) {
		enter(moduleRequirement);
		scan(CtRole.COMMENT, moduleRequirement.getComments());
		scan(CtRole.MODULE_REF, moduleRequirement.getModuleReference());
		scan(CtRole.ANNOTATION, moduleRequirement.getAnnotations());
		exit(moduleRequirement);
	}

	@Override
	public void visitCtProvidedService(CtProvidedService moduleProvidedService) {
		enter(moduleProvidedService);
		scan(CtRole.COMMENT, moduleProvidedService.getComments());
		scan(CtRole.SERVICE_TYPE, moduleProvidedService.getServiceType());
		scan(CtRole.IMPLEMENTATION_TYPE, moduleProvidedService.getImplementationTypes());
		scan(CtRole.ANNOTATION, moduleProvidedService.getAnnotations());
		exit(moduleProvidedService);
	}

	@Override
	public void visitCtUsedService(CtUsedService usedService) {
		enter(usedService);
		scan(CtRole.COMMENT, usedService.getComments());
		scan(CtRole.SERVICE_TYPE, usedService.getServiceType());
		scan(CtRole.ANNOTATION, usedService.getAnnotations());
		exit(usedService);
	}

	@Override
	public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {
		enter(compilationUnit);
		scan(CtRole.COMMENT, compilationUnit.getComments());
		scan(CtRole.ANNOTATION, compilationUnit.getAnnotations());
		scan(CtRole.PACKAGE_DECLARATION, compilationUnit.getPackageDeclaration());
		scan(CtRole.DECLARED_IMPORT, compilationUnit.getImports());
		scan(CtRole.DECLARED_MODULE_REF, compilationUnit.getDeclaredModuleReference());
		scan(CtRole.DECLARED_TYPE_REF, compilationUnit.getDeclaredTypeReferences());
		exit(compilationUnit);
	}

	@Override
	public void visitCtPackageDeclaration(CtPackageDeclaration packageDeclaration) {
		enter(packageDeclaration);
		scan(CtRole.COMMENT, packageDeclaration.getComments());
		scan(CtRole.ANNOTATION, packageDeclaration.getAnnotations());
		scan(CtRole.PACKAGE_REF, packageDeclaration.getReference());
		exit(packageDeclaration);
	}

	@Override
	public void visitCtTypeMemberWildcardImportReference(CtTypeMemberWildcardImportReference wildcardReference) {
		enter(wildcardReference);
		scan(CtRole.TYPE_REF, wildcardReference.getTypeReference());
		exit(wildcardReference);
	}

	@Override
	public void visitCtYieldStatement(CtYieldStatement statement) {
		enter(statement);
		scan(CtRole.ANNOTATION, statement.getAnnotations());
		scan(CtRole.EXPRESSION, statement.getExpression());
		scan(CtRole.COMMENT, statement.getComments());
		exit(statement);
		}
}

