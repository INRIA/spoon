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
package spoon.reflect.visitor;

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
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtWildcardReference;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

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
		super();
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
	public void scan(Collection<? extends CtElement> elements) {
		if ((elements != null)) {
			for (CtElement e : elements) {
				scan(e);
			}
		}

	}

	/**
	 * Generically scans a meta-model element.
	 */
	public void scan(CtElement element) {
		if ((element != null)) {
			element.accept(this);
		}
	}

	public <A extends Annotation> void visitCtAnnotation(
			final CtAnnotation<A> annotation) {
		enter(annotation);
		scan(annotation.getComments());
		scan(annotation.getAnnotationType());
		scan(annotation.getAnnotations());
		scan(annotation.getValues());
		exit(annotation);
	}

	/**
	 * Generically scans an object that can be an element, a reference, or a
	 * collection of those.
	 */
	public void scan(Object o) {
		if (o instanceof CtElement) {
			scan((CtElement) o);
		}
		if (o instanceof Collection<?>) {
			for (Object obj : (Collection<?>) o) {
				scan(obj);
			}
		}
		if (o instanceof Map<?, ?>) {
			for (Object obj : ((Map) o).values()) {
				scan(obj);
			}
		}
	}

	public <A extends Annotation> void visitCtAnnotationType(
			final CtAnnotationType<A> annotationType) {
		enter(annotationType);
		scan(annotationType.getAnnotations());
		scan(annotationType.getTypeMembers());
		scan(annotationType.getComments());
		exit(annotationType);
	}

	public void visitCtAnonymousExecutable(final CtAnonymousExecutable anonymousExec) {
		enter(anonymousExec);
		scan(anonymousExec.getAnnotations());
		scan(anonymousExec.getBody());
		scan(anonymousExec.getComments());
		exit(anonymousExec);
	}

	@Override
	public <T> void visitCtArrayRead(final CtArrayRead<T> arrayRead) {
		enter(arrayRead);
		scan(arrayRead.getAnnotations());
		scan(arrayRead.getType());
		scan(arrayRead.getTypeCasts());
		scan(arrayRead.getTarget());
		scan(arrayRead.getIndexExpression());
		scan(arrayRead.getComments());
		exit(arrayRead);
	}

	@Override
	public <T> void visitCtArrayWrite(final CtArrayWrite<T> arrayWrite) {
		enter(arrayWrite);
		scan(arrayWrite.getAnnotations());
		scan(arrayWrite.getType());
		scan(arrayWrite.getTypeCasts());
		scan(arrayWrite.getTarget());
		scan(arrayWrite.getIndexExpression());
		scan(arrayWrite.getComments());
		exit(arrayWrite);
	}

	public <T> void visitCtArrayTypeReference(final CtArrayTypeReference<T> reference) {
		enter(reference);
		scan(reference.getComments());
		scan(reference.getDeclaringType());
		scan(reference.getComponentType());
		scan(reference.getActualTypeArguments());
		scan(reference.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtAssert(final CtAssert<T> asserted) {
		enter(asserted);
		scan(asserted.getAnnotations());
		scan(asserted.getAssertExpression());
		scan(asserted.getExpression());
		scan(asserted.getComments());
		exit(asserted);
	}

	public <T, A extends T> void visitCtAssignment(
			final CtAssignment<T, A> assignement) {
		enter(assignement);
		scan(assignement.getAnnotations());
		scan(assignement.getType());
		scan(assignement.getTypeCasts());
		scan(assignement.getAssigned());
		scan(assignement.getAssignment());
		scan(assignement.getComments());
		exit(assignement);
	}

	public <T> void visitCtBinaryOperator(final CtBinaryOperator<T> operator) {
		enter(operator);
		scan(operator.getAnnotations());
		scan(operator.getType());
		scan(operator.getTypeCasts());
		scan(operator.getLeftHandOperand());
		scan(operator.getRightHandOperand());
		scan(operator.getComments());
		exit(operator);
	}

	public <R> void visitCtBlock(final CtBlock<R> block) {
		enter(block);
		scan(block.getAnnotations());
		scan(block.getStatements());
		scan(block.getComments());
		exit(block);
	}

	public void visitCtBreak(final CtBreak breakStatement) {
		enter(breakStatement);
		scan(breakStatement.getAnnotations());
		scan(breakStatement.getComments());
		exit(breakStatement);
	}

	public <S> void visitCtCase(final CtCase<S> caseStatement) {
		enter(caseStatement);
		scan(caseStatement.getAnnotations());
		scan(caseStatement.getCaseExpression());
		scan(caseStatement.getStatements());
		scan(caseStatement.getComments());
		exit(caseStatement);
	}

	public void visitCtCatch(final CtCatch catchBlock) {
		enter(catchBlock);
		scan(catchBlock.getAnnotations());
		scan(catchBlock.getParameter());
		scan(catchBlock.getBody());
		scan(catchBlock.getComments());
		exit(catchBlock);
	}

	public <T> void visitCtClass(final CtClass<T> ctClass) {
		enter(ctClass);
		scan(ctClass.getAnnotations());
		scan(ctClass.getSuperclass());
		scan(ctClass.getSuperInterfaces());
		scan(ctClass.getFormalCtTypeParameters());
		scan(ctClass.getTypeMembers());
		scan(ctClass.getComments());
		exit(ctClass);
	}

	@Override
	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		enter(typeParameter);
		scan(typeParameter.getAnnotations());
		scan(typeParameter.getSuperclass());
		scan(typeParameter.getComments());
		exit(typeParameter);
	}

	public <T> void visitCtConditional(final CtConditional<T> conditional) {
		enter(conditional);
		scan(conditional.getAnnotations());
		scan(conditional.getCondition());
		scan(conditional.getThenExpression());
		scan(conditional.getElseExpression());
		scan(conditional.getComments());
		scan(conditional.getTypeCasts());
		exit(conditional);
	}

	public <T> void visitCtConstructor(final CtConstructor<T> c) {
		enter(c);
		scan(c.getAnnotations());
		scan(c.getParameters());
		scan(c.getThrownTypes());
		scan(c.getFormalCtTypeParameters());
		scan(c.getBody());
		scan(c.getComments());
		exit(c);
	}

	public void visitCtContinue(final CtContinue continueStatement) {
		enter(continueStatement);
		scan(continueStatement.getAnnotations());
		scan(continueStatement.getLabelledStatement());
		scan(continueStatement.getComments());
		exit(continueStatement);
	}

	public void visitCtDo(final CtDo doLoop) {
		enter(doLoop);
		scan(doLoop.getAnnotations());
		scan(doLoop.getLoopingExpression());
		scan(doLoop.getBody());
		scan(doLoop.getComments());
		exit(doLoop);
	}

	public <T extends Enum<?>> void visitCtEnum(final CtEnum<T> ctEnum) {
		enter(ctEnum);
		scan(ctEnum.getAnnotations());
		scan(ctEnum.getSuperInterfaces());
		scan(ctEnum.getTypeMembers());
		scan(ctEnum.getEnumValues());
		scan(ctEnum.getComments());
		exit(ctEnum);
	}

	public <T> void visitCtExecutableReference(
			final CtExecutableReference<T> reference) {
		enter(reference);
		scan(reference.getDeclaringType());
		scan(reference.getType());
		scan(reference.getParameters());
		scan(reference.getActualTypeArguments());
		scan(reference.getAnnotations());
		scan(reference.getComments());
		exit(reference);
	}

	public <T> void visitCtField(final CtField<T> f) {
		enter(f);
		scan(f.getAnnotations());
		scan(f.getType());
		scan(f.getDefaultExpression());
		scan(f.getComments());
		exit(f);
	}

	@Override
	public <T> void visitCtEnumValue(final CtEnumValue<T> enumValue) {
		enter(enumValue);
		scan(enumValue.getAnnotations());
		scan(enumValue.getType());
		scan(enumValue.getDefaultExpression());
		scan(enumValue.getComments());
		exit(enumValue);
	}

	@Override
	public <T> void visitCtThisAccess(final CtThisAccess<T> thisAccess) {
		enter(thisAccess);
		scan(thisAccess.getComments());
		scan(thisAccess.getAnnotations());
		scan(thisAccess.getType());
		scan(thisAccess.getTypeCasts());
		scan(thisAccess.getTarget());
		exit(thisAccess);
	}

	public <T> void visitCtAnnotationFieldAccess(
			final CtAnnotationFieldAccess<T> annotationFieldAccess) {
		enter(annotationFieldAccess);
		scan(annotationFieldAccess.getComments());
		scan(annotationFieldAccess.getAnnotations());
		scan(annotationFieldAccess.getType());
		scan(annotationFieldAccess.getTypeCasts());
		scan(annotationFieldAccess.getTarget());
		scan(annotationFieldAccess.getVariable());
		scan(annotationFieldAccess.getComments());
		exit(annotationFieldAccess);
	}

	public <T> void visitCtFieldReference(final CtFieldReference<T> reference) {
		enter(reference);
		scan(reference.getDeclaringType());
		scan(reference.getType());
		scan(reference.getAnnotations());
		exit(reference);
	}

	public void visitCtFor(final CtFor forLoop) {
		enter(forLoop);
		scan(forLoop.getAnnotations());
		scan(forLoop.getForInit());
		scan(forLoop.getExpression());
		scan(forLoop.getForUpdate());
		scan(forLoop.getBody());
		scan(forLoop.getComments());
		exit(forLoop);
	}

	public void visitCtForEach(final CtForEach foreach) {
		enter(foreach);
		scan(foreach.getAnnotations());
		scan(foreach.getVariable());
		scan(foreach.getExpression());
		scan(foreach.getBody());
		scan(foreach.getComments());
		exit(foreach);
	}

	public void visitCtIf(final CtIf ifElement) {
		enter(ifElement);
		scan(ifElement.getAnnotations());
		scan(ifElement.getCondition());
		scan((CtStatement) ifElement.getThenStatement());
		scan((CtStatement) ifElement.getElseStatement());
		scan(ifElement.getComments());
		exit(ifElement);
	}

	public <T> void visitCtInterface(final CtInterface<T> intrface) {
		enter(intrface);
		scan(intrface.getAnnotations());
		scan(intrface.getSuperInterfaces());
		scan(intrface.getFormalCtTypeParameters());
		scan(intrface.getTypeMembers());
		scan(intrface.getComments());
		exit(intrface);
	}

	public <T> void visitCtInvocation(final CtInvocation<T> invocation) {
		enter(invocation);
		scan(invocation.getAnnotations());
		scan(invocation.getTypeCasts());
		scan(invocation.getTarget());
		scan(invocation.getExecutable());
		scan(invocation.getArguments());
		scan(invocation.getComments());
		exit(invocation);
	}

	public <T> void visitCtLiteral(final CtLiteral<T> literal) {
		enter(literal);
		scan(literal.getAnnotations());
		scan(literal.getType());
		scan(literal.getValue());
		scan(literal.getTypeCasts());
		scan(literal.getComments());
		exit(literal);
	}

	public <T> void visitCtLocalVariable(final CtLocalVariable<T> localVariable) {
		enter(localVariable);
		scan(localVariable.getAnnotations());
		scan(localVariable.getType());
		scan(localVariable.getDefaultExpression());
		scan(localVariable.getComments());
		exit(localVariable);
	}

	public <T> void visitCtLocalVariableReference(
			final CtLocalVariableReference<T> reference) {
		enter(reference);
		scan(reference.getType());
		scan(reference.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtCatchVariable(final CtCatchVariable<T> catchVariable) {
		enter(catchVariable);
		scan(catchVariable.getComments());
		scan(catchVariable.getAnnotations());
		scan(catchVariable.getType());
		scan(catchVariable.getMultiTypes());
		exit(catchVariable);
	}

	public <T> void visitCtCatchVariableReference(final CtCatchVariableReference<T> reference) {
		enter(reference);
		scan(reference.getComments());
		scan(reference.getType());
		scan(reference.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtMethod(final CtMethod<T> m) {
		enter(m);
		scan(m.getAnnotations());
		scan(m.getFormalCtTypeParameters());
		scan(m.getType());
		scan(m.getParameters());
		scan(m.getThrownTypes());
		scan(m.getBody());
		scan(m.getComments());
		exit(m);
	}

	@Override
	public <T> void visitCtAnnotationMethod(CtAnnotationMethod<T> annotationMethod) {
		enter(annotationMethod);
		scan(annotationMethod.getAnnotations());
		scan(annotationMethod.getType());
		scan(annotationMethod.getDefaultExpression());
		scan(annotationMethod.getComments());
		exit(annotationMethod);
	}

	public <T> void visitCtNewArray(final CtNewArray<T> newArray) {
		enter(newArray);
		scan(newArray.getAnnotations());
		scan(newArray.getType());
		scan(newArray.getTypeCasts());
		scan(newArray.getElements());
		scan(newArray.getDimensionExpressions());
		scan(newArray.getComments());
		exit(newArray);
	}

	@Override
	public <T> void visitCtConstructorCall(final CtConstructorCall<T> ctConstructorCall) {
		enter(ctConstructorCall);
		scan(ctConstructorCall.getAnnotations());
		scan(ctConstructorCall.getTypeCasts());
		scan(ctConstructorCall.getExecutable());
		scan(ctConstructorCall.getTarget());
		scan(ctConstructorCall.getArguments());
		scan(ctConstructorCall.getComments());
		exit(ctConstructorCall);
	}

	public <T> void visitCtNewClass(final CtNewClass<T> newClass) {
		enter(newClass);
		scan(newClass.getAnnotations());
		scan(newClass.getTypeCasts());
		scan(newClass.getExecutable());
		scan(newClass.getTarget());
		scan(newClass.getArguments());
		scan(newClass.getAnonymousClass());
		scan(newClass.getComments());
		exit(newClass);
	}

	@Override
	public <T> void visitCtLambda(final CtLambda<T> lambda) {
		enter(lambda);
		scan(lambda.getAnnotations());
		scan(lambda.getType());
		scan(lambda.getTypeCasts());
		scan(lambda.getParameters());
		scan(lambda.getBody());
		scan(lambda.getExpression());
		scan(lambda.getComments());
		exit(lambda);
	}

	@Override
	public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(
			final CtExecutableReferenceExpression<T, E> expression) {
		enter(expression);
		scan(expression.getComments());
		scan(expression.getAnnotations());
		scan(expression.getType());
		scan(expression.getTypeCasts());
		scan(expression.getExecutable());
		scan(expression.getTarget());
		exit(expression);
	}

	public <T, A extends T> void visitCtOperatorAssignment(
			final CtOperatorAssignment<T, A> assignment) {
		enter(assignment);
		scan(assignment.getAnnotations());
		scan(assignment.getType());
		scan(assignment.getTypeCasts());
		scan(assignment.getAssigned());
		scan(assignment.getAssignment());
		scan(assignment.getComments());
		exit(assignment);
	}

	public void visitCtPackage(final CtPackage ctPackage) {
		enter(ctPackage);
		scan(ctPackage.getAnnotations());
		scan(ctPackage.getPackages());
		scan(ctPackage.getTypes());
		scan(ctPackage.getComments());
		exit(ctPackage);
	}

	public void visitCtPackageReference(final CtPackageReference reference) {
		enter(reference);
		scan(reference.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtParameter(final CtParameter<T> parameter) {
		enter(parameter);
		scan(parameter.getAnnotations());
		scan(parameter.getType());
		scan(parameter.getComments());
		exit(parameter);
	}

	public <T> void visitCtParameterReference(final CtParameterReference<T> reference) {
		enter(reference);
		scan(reference.getType());
		scan(reference.getAnnotations());
		scan(reference.getDeclaringExecutable());
		exit(reference);
	}

	public <R> void visitCtReturn(final CtReturn<R> returnStatement) {
		enter(returnStatement);
		scan(returnStatement.getAnnotations());
		scan(returnStatement.getReturnedExpression());
		scan(returnStatement.getComments());
		exit(returnStatement);
	}

	public <R> void visitCtStatementList(final CtStatementList statements) {
		enter(statements);
		scan(statements.getAnnotations());
		scan(statements.getStatements());
		scan(statements.getComments());
		exit(statements);
	}

	public <S> void visitCtSwitch(final CtSwitch<S> switchStatement) {
		enter(switchStatement);
		scan(switchStatement.getAnnotations());
		scan(switchStatement.getSelector());
		scan(switchStatement.getCases());
		scan(switchStatement.getComments());
		exit(switchStatement);
	}

	public void visitCtSynchronized(final CtSynchronized synchro) {
		enter(synchro);
		scan(synchro.getAnnotations());
		scan(synchro.getExpression());
		scan(synchro.getBlock());
		scan(synchro.getComments());
		exit(synchro);
	}

	public void visitCtThrow(final CtThrow throwStatement) {
		enter(throwStatement);
		scan(throwStatement.getAnnotations());
		scan(throwStatement.getThrownExpression());
		scan(throwStatement.getComments());
		exit(throwStatement);
	}

	public void visitCtTry(final CtTry tryBlock) {
		enter(tryBlock);
		scan(tryBlock.getAnnotations());
		scan(tryBlock.getBody());
		scan(tryBlock.getCatchers());
		scan(tryBlock.getFinalizer());
		scan(tryBlock.getComments());
		exit(tryBlock);
	}

	@Override
	public void visitCtTryWithResource(final CtTryWithResource tryWithResource) {
		enter(tryWithResource);
		scan(tryWithResource.getAnnotations());
		scan(tryWithResource.getResources());
		scan(tryWithResource.getBody());
		scan(tryWithResource.getCatchers());
		scan(tryWithResource.getFinalizer());
		scan(tryWithResource.getComments());
		exit(tryWithResource);
	}

	public void visitCtTypeParameterReference(final CtTypeParameterReference ref) {
		enter(ref);
		scan(ref.getPackage());
		scan(ref.getDeclaringType());
		scan(ref.getAnnotations());
		scan(ref.getBoundingType());
		exit(ref);
	}

	@Override
	public void visitCtWildcardReference(CtWildcardReference wildcardReference) {
		enter(wildcardReference);
		scan(wildcardReference.getAnnotations());
		scan(wildcardReference.getBoundingType());
		exit(wildcardReference);
	}

	@Override
	public <T> void visitCtIntersectionTypeReference(final CtIntersectionTypeReference<T> reference) {
		enter(reference);
		scan(reference.getAnnotations());
		scan(reference.getBounds());
		exit(reference);
	}

	public <T> void visitCtTypeReference(final CtTypeReference<T> reference) {
		enter(reference);
		scan(reference.getPackage());
		scan(reference.getDeclaringType());
		scan(reference.getActualTypeArguments());
		scan(reference.getAnnotations());
		scan(reference.getComments());
		exit(reference);
	}

	@Override
	public <T> void visitCtTypeAccess(final CtTypeAccess<T> typeAccess) {
		enter(typeAccess);
		scan(typeAccess.getAnnotations());
		scan(typeAccess.getTypeCasts());
		scan(typeAccess.getAccessedType());
		scan(typeAccess.getComments());
		exit(typeAccess);
	}

	public <T> void visitCtUnaryOperator(final CtUnaryOperator<T> operator) {
		enter(operator);
		scan(operator.getAnnotations());
		scan(operator.getType());
		scan(operator.getTypeCasts());
		scan(operator.getOperand());
		scan(operator.getComments());
		exit(operator);
	}

	@Override
	public <T> void visitCtVariableRead(final CtVariableRead<T> variableRead) {
		enter(variableRead);
		scan(variableRead.getAnnotations());
		scan(variableRead.getTypeCasts());
		scan(variableRead.getVariable());
		scan(variableRead.getComments());
		exit(variableRead);
	}

	@Override
	public <T> void visitCtVariableWrite(final CtVariableWrite<T> variableWrite) {
		enter(variableWrite);
		scan(variableWrite.getAnnotations());
		scan(variableWrite.getTypeCasts());
		scan(variableWrite.getVariable());
		scan(variableWrite.getComments());
		exit(variableWrite);
	}

	public void visitCtWhile(final CtWhile whileLoop) {
		enter(whileLoop);
		scan(whileLoop.getAnnotations());
		scan(whileLoop.getLoopingExpression());
		scan(whileLoop.getBody());
		scan(whileLoop.getComments());
		exit(whileLoop);
	}

	public <T> void visitCtCodeSnippetExpression(final CtCodeSnippetExpression<T> expression) {
		enter(expression);
		scan(expression.getComments());
		scan(expression.getAnnotations());
		scan(expression.getTypeCasts());
		exit(expression);
	}

	public void visitCtCodeSnippetStatement(final CtCodeSnippetStatement statement) {
		enter(statement);
		scan(statement.getComments());
		scan(statement.getAnnotations());
		exit(statement);
	}

	public <T> void visitCtUnboundVariableReference(final CtUnboundVariableReference<T> reference) {
		enter(reference);
		exit(reference);
	}

	@Override
	public <T> void visitCtFieldRead(final CtFieldRead<T> fieldRead) {
		enter(fieldRead);
		scan(fieldRead.getAnnotations());
		scan(fieldRead.getTypeCasts());
		scan(fieldRead.getTarget());
		scan(fieldRead.getVariable());
		scan(fieldRead.getComments());
		exit(fieldRead);
	}

	@Override
	public <T> void visitCtFieldWrite(final CtFieldWrite<T> fieldWrite) {
		enter(fieldWrite);
		scan(fieldWrite.getAnnotations());
		scan(fieldWrite.getTypeCasts());
		scan(fieldWrite.getTarget());
		scan(fieldWrite.getVariable());
		scan(fieldWrite.getComments());
		exit(fieldWrite);
	}

	@Override
	public <T> void visitCtSuperAccess(final CtSuperAccess<T> f) {
		enter(f);
		scan(f.getComments());
		scan(f.getAnnotations());
		scan(f.getType());
		scan(f.getTypeCasts());
		scan(f.getTarget());
		scan(f.getVariable());
		exit(f);
	}

	@Override
	public void visitCtComment(final CtComment comment) {
		enter(comment);
		scan(comment.getComments());
		scan(comment.getAnnotations());
		exit(comment);
	}
}
