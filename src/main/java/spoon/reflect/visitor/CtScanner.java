/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
import spoon.reflect.code.CtArrayAccess;
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
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
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
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.internal.CtImplicitArrayTypeReference;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.internal.CtCircularTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.internal.CtImplicitTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;

import java.lang.annotation.Annotation;
import java.util.Collection;

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
			CtAnnotation<A> annotation) {
		enter(annotation);
		scan(annotation.getAnnotationType());
		scan(annotation.getAnnotations());
		for (Object o : annotation.getElementValues().values()) {
			scan(o);
		}
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
		if (o instanceof CtReference) {
			scan((CtReference) o);
		}
		if (o instanceof Collection<?>) {
			for (Object obj : (Collection<?>) o) {
				scan(obj);
			}
		}
	}

	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {
		enter(annotationType);
		scan(annotationType.getAnnotations());
		scan(annotationType.getNestedTypes());
		scan(annotationType.getFields());
		exit(annotationType);
	}

	public void visitCtAnonymousExecutable(CtAnonymousExecutable anonymousExec) {
		enter(anonymousExec);
		scan(anonymousExec.getAnnotations());
		scan(anonymousExec.getBody());
		exit(anonymousExec);
	}

	public <T, E extends CtExpression<?>> void visitCtArrayAccess(CtArrayAccess<T, E> arrayAccess) {
		enter(arrayAccess);
		scan(arrayAccess.getAnnotations());
		scan(arrayAccess.getType());
		scan(arrayAccess.getTypeCasts());
		scan(arrayAccess.getTarget());
		scan(arrayAccess.getIndexExpression());
		exit(arrayAccess);
	}

	@Override
	public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {
		enter(arrayRead);
		scan(arrayRead.getAnnotations());
		scan(arrayRead.getType());
		scan(arrayRead.getTypeCasts());
		scan(arrayRead.getTarget());
		scan(arrayRead.getIndexExpression());
		exit(arrayRead);
	}

	@Override
	public <T> void visitCtArrayWrite(CtArrayWrite<T> arrayWrite) {
		enter(arrayWrite);
		scan(arrayWrite.getAnnotations());
		scan(arrayWrite.getType());
		scan(arrayWrite.getTypeCasts());
		scan(arrayWrite.getTarget());
		scan(arrayWrite.getIndexExpression());
		exit(arrayWrite);
	}

	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		enter(reference);
		scan(reference.getDeclaringType());
		scan(reference.getPackage());
		scan(reference.getComponentType());
		scan(reference.getActualTypeArguments());
		scan(reference.getAnnotations());
		exit(reference);
	}

	@Override
	public <T> void visitCtImplicitArrayTypeReference(CtImplicitArrayTypeReference<T> reference) {
		visitCtArrayTypeReference(reference);
	}

	public <T> void visitCtAssert(CtAssert<T> asserted) {
		enter(asserted);
		scan(asserted.getAnnotations());
		scan(asserted.getAssertExpression());
		scan(asserted.getExpression());
		exit(asserted);
	}

	public <T, A extends T> void visitCtAssignment(
			CtAssignment<T, A> assignement) {
		enter(assignement);
		scan(assignement.getAnnotations());
		scan(assignement.getType());
		scan(assignement.getTypeCasts());
		scan(assignement.getAssigned());
		scan(assignement.getAssignment());
		exit(assignement);
	}

	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		enter(operator);
		scan(operator.getAnnotations());
		scan(operator.getType());
		scan(operator.getTypeCasts());
		scan(operator.getLeftHandOperand());
		scan(operator.getRightHandOperand());
		exit(operator);
	}

	public <R> void visitCtBlock(CtBlock<R> block) {
		enter(block);
		scan(block.getAnnotations());
		scan(block.getStatements());
		exit(block);
	}

	public void visitCtBreak(CtBreak breakStatement) {
		enter(breakStatement);
		scan(breakStatement.getAnnotations());
		exit(breakStatement);
	}

	public <S> void visitCtCase(CtCase<S> caseStatement) {
		enter(caseStatement);
		scan(caseStatement.getAnnotations());
		scan(caseStatement.getCaseExpression());
		scan(caseStatement.getStatements());
		exit(caseStatement);
	}

	public void visitCtCatch(CtCatch catchBlock) {
		enter(catchBlock);
		scan(catchBlock.getAnnotations());
		scan(catchBlock.getParameter());
		scan(catchBlock.getBody());
		exit(catchBlock);
	}

	public <T> void visitCtClass(CtClass<T> ctClass) {
		enter(ctClass);
		scan(ctClass.getAnnotations());
		scan(ctClass.getSuperclass());
		scan(ctClass.getSuperInterfaces());
		scan(ctClass.getFormalTypeParameters());
		scan(ctClass.getAnonymousExecutables());
		scan(ctClass.getNestedTypes());
		scan(ctClass.getFields());
		scan(ctClass.getConstructors());
		scan(ctClass.getMethods());
		exit(ctClass);
	}

	public <T> void visitCtConditional(CtConditional<T> conditional) {
		enter(conditional);
		scan(conditional.getAnnotations());
		scan(conditional.getCondition());
		scan(conditional.getThenExpression());
		scan(conditional.getElseExpression());
		exit(conditional);
	}

	public <T> void visitCtConstructor(CtConstructor<T> c) {
		enter(c);
		scan(c.getAnnotations());
		scan(c.getParameters());
		scan(c.getThrownTypes());
		scan(c.getFormalTypeParameters());
		scan(c.getBody());
		exit(c);
	}

	public void visitCtContinue(CtContinue continueStatement) {
		enter(continueStatement);
		scan(continueStatement.getAnnotations());
		scan(continueStatement.getLabelledStatement());
		exit(continueStatement);
	}

	public void visitCtDo(CtDo doLoop) {
		enter(doLoop);
		scan(doLoop.getAnnotations());
		scan(doLoop.getLoopingExpression());
		scan(doLoop.getBody());
		exit(doLoop);
	}

	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		enter(ctEnum);
		scan(ctEnum.getAnnotations());
		scan(ctEnum.getSuperInterfaces());
		scan(ctEnum.getFields());
		scan(ctEnum.getConstructors());
		scan(ctEnum.getMethods());
		scan(ctEnum.getNestedTypes());
		exit(ctEnum);
	}

	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {
		enter(reference);
		scan(reference.getDeclaringType());
		scan(reference.getType());
		scan(reference.getActualTypeArguments());
		scan(reference.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtField(CtField<T> f) {
		enter(f);
		scan(f.getAnnotations());
		scan(f.getType());
		scan(f.getDefaultExpression());
		exit(f);
	}

	@Override
	public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
		enter(thisAccess);
		scan(thisAccess.getType());
		scan(thisAccess.getTypeCasts());
		scan(thisAccess.getTarget());
		exit(thisAccess);
	}

	public <T> void visitCtAnnotationFieldAccess(
			CtAnnotationFieldAccess<T> annotationFieldAccess) {
		enter(annotationFieldAccess);
		scan(annotationFieldAccess.getAnnotations());
		scan(annotationFieldAccess.getType());
		scan(annotationFieldAccess.getTypeCasts());
		scan(annotationFieldAccess.getTarget());
		scan(annotationFieldAccess.getVariable());
		exit(annotationFieldAccess);
	}

	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		enter(reference);
		scan(reference.getDeclaringType());
		scan(reference.getType());
		scan(reference.getAnnotations());
		exit(reference);
	}

	public void visitCtFor(CtFor forLoop) {
		enter(forLoop);
		scan(forLoop.getAnnotations());
		scan(forLoop.getForInit());
		scan(forLoop.getExpression());
		scan(forLoop.getForUpdate());
		scan(forLoop.getBody());
		exit(forLoop);
	}

	public void visitCtForEach(CtForEach foreach) {
		enter(foreach);
		scan(foreach.getAnnotations());
		scan(foreach.getVariable());
		scan(foreach.getExpression());
		scan(foreach.getBody());
		exit(foreach);
	}

	public void visitCtIf(CtIf ifElement) {
		enter(ifElement);
		scan(ifElement.getAnnotations());
		scan(ifElement.getCondition());
		scan((CtStatement) ifElement.getThenStatement());
		scan((CtStatement) ifElement.getElseStatement());
		exit(ifElement);
	}

	public <T> void visitCtInterface(CtInterface<T> intrface) {
		enter(intrface);
		scan(intrface.getAnnotations());
		scan(intrface.getSuperInterfaces());
		scan(intrface.getFormalTypeParameters());
		scan(intrface.getNestedTypes());
		scan(intrface.getFields());
		scan(intrface.getMethods());
		exit(intrface);
	}

	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		enter(invocation);
		scan(invocation.getAnnotations());
		scan(invocation.getTypeCasts());
		scan(invocation.getTarget());
		scan(invocation.getExecutable());
		scan(invocation.getArguments());
		exit(invocation);
	}

	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		enter(literal);
		scan(literal.getAnnotations());
		scan(literal.getType());
		scan(literal.getValue());
		scan(literal.getTypeCasts());
		exit(literal);
	}

	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
		enter(localVariable);
		scan(localVariable.getAnnotations());
		scan(localVariable.getType());
		scan(localVariable.getDefaultExpression());
		exit(localVariable);
	}

	public <T> void visitCtLocalVariableReference(
			CtLocalVariableReference<T> reference) {
		enter(reference);
		scan(reference.getType());
		scan(reference.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
		enter(catchVariable);
		scan(catchVariable.getAnnotations());
		scan(catchVariable.getType());
		exit(catchVariable);
	}

	public <T> void visitCtCatchVariableReference(CtCatchVariableReference<T> reference) {
		enter(reference);
		scan(reference.getType());
		scan(reference.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtMethod(CtMethod<T> m) {
		enter(m);
		scan(m.getAnnotations());
		scan(m.getType());
		scan(m.getParameters());
		scan(m.getThrownTypes());
		scan(m.getFormalTypeParameters());
		scan(m.getBody());
		exit(m);
	}

	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		enter(newArray);
		scan(newArray.getAnnotations());
		scan(newArray.getType());
		scan(newArray.getTypeCasts());
		scan(newArray.getElements());
		scan(newArray.getDimensionExpressions());
		exit(newArray);
	}

	@Override
	public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
		enter(ctConstructorCall);
		scan(ctConstructorCall.getAnnotations());
		scan(ctConstructorCall.getTypeCasts());
		scan(ctConstructorCall.getExecutable());
		scan(ctConstructorCall.getTarget());
		scan(ctConstructorCall.getArguments());
		exit(ctConstructorCall);
	}

	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		enter(newClass);
		scan(newClass.getAnnotations());
		scan(newClass.getType());
		scan(newClass.getTypeCasts());
		scan(newClass.getExecutable());
		scan(newClass.getTarget());
		scan(newClass.getArguments());
		scan(newClass.getAnonymousClass());
		exit(newClass);
	}

	@Override
	public <T> void visitCtLambda(CtLambda<T> lambda) {
		enter(lambda);
		scan(lambda.getAnnotations());
		scan(lambda.getType());
		scan(lambda.getTypeCasts());
		scan(lambda.getParameters());
		scan(lambda.getBody());
		scan(lambda.getExpression());
		exit(lambda);
	}

	@Override
	public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(
			CtExecutableReferenceExpression<T, E> expression) {
		enter(expression);
		scan(expression.getType());
		scan(expression.getTypeCasts());
		scan(expression.getExecutable());
		scan(expression.getTarget());
		exit(expression);
	}

	public <T, A extends T> void visitCtOperatorAssignment(
			CtOperatorAssignment<T, A> assignment) {
		enter(assignment);
		scan(assignment.getAnnotations());
		scan(assignment.getType());
		scan(assignment.getTypeCasts());
		scan(assignment.getAssigned());
		scan(assignment.getAssignment());
		exit(assignment);
	}

	public void visitCtPackage(CtPackage ctPackage) {
		enter(ctPackage);
		scan(ctPackage.getAnnotations());
		scan(ctPackage.getPackages());
		scan(ctPackage.getTypes());
		exit(ctPackage);
	}

	public void visitCtPackageReference(CtPackageReference reference) {
		enter(reference);
		exit(reference);
	}

	public <T> void visitCtParameter(CtParameter<T> parameter) {
		enter(parameter);
		scan(parameter.getAnnotations());
		scan(parameter.getType());
		exit(parameter);
	}

	public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
		enter(reference);
		scan(reference.getType());
		scan(reference.getAnnotations());
		exit(reference);
	}

	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		enter(returnStatement);
		scan(returnStatement.getAnnotations());
		scan(returnStatement.getReturnedExpression());
		exit(returnStatement);
	}

	public <R> void visitCtStatementList(CtStatementList statements) {
		enter(statements);
		scan(statements.getAnnotations());
		scan(statements.getStatements());
		exit(statements);
	}

	public <S> void visitCtSwitch(CtSwitch<S> switchStatement) {
		enter(switchStatement);
		scan(switchStatement.getAnnotations());
		scan(switchStatement.getSelector());
		scan(switchStatement.getCases());
		exit(switchStatement);
	}

	public void visitCtSynchronized(CtSynchronized synchro) {
		enter(synchro);
		scan(synchro.getAnnotations());
		scan(synchro.getExpression());
		scan(synchro.getBlock());
		exit(synchro);
	}

	public void visitCtThrow(CtThrow throwStatement) {
		enter(throwStatement);
		scan(throwStatement.getAnnotations());
		scan(throwStatement.getThrownExpression());
		exit(throwStatement);
	}

	public void visitCtTry(CtTry tryBlock) {
		enter(tryBlock);
		scan(tryBlock.getAnnotations());
		scan(tryBlock.getBody());
		scan(tryBlock.getCatchers());
		scan(tryBlock.getFinalizer());
		exit(tryBlock);
	}

	@Override
	public void visitCtTryWithResource(CtTryWithResource tryWithResource) {
		enter(tryWithResource);
		scan(tryWithResource.getAnnotations());
		scan(tryWithResource.getResources());
		scan(tryWithResource.getBody());
		scan(tryWithResource.getCatchers());
		scan(tryWithResource.getFinalizer());
		exit(tryWithResource);
	}

	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		enter(typeParameter);
		scan(typeParameter.getAnnotations());
		scan(typeParameter.getBounds());
		exit(typeParameter);
	}

	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
		enter(ref);
		scan(ref.getPackage());
		scan(ref.getDeclaringType());
		scan(ref.getActualTypeArguments());
		scan(ref.getAnnotations());
		scan(ref.getBounds());
		exit(ref);
	}

	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		enter(reference);
		scan(reference.getPackage());
		scan(reference.getDeclaringType());
		scan(reference.getActualTypeArguments());
		scan(reference.getAnnotations());
		exit(reference);
	}

	@Override
	public void visitCtCircularTypeReference(CtCircularTypeReference reference) {
		enter(reference);
		exit(reference);
	}

	@Override
	public <T> void visitCtImplicitTypeReference(CtImplicitTypeReference<T> reference) {
		visitCtTypeReference(reference);
	}

	@Override
	public <T> void visitCtTypeAccess(CtTypeAccess<T> typeAccess) {
		enter(typeAccess);
		scan(typeAccess.getAnnotations());
		scan(typeAccess.getType());
		scan(typeAccess.getTypeCasts());
		scan(typeAccess.getAccessedType());
		exit(typeAccess);
	}

	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		enter(operator);
		scan(operator.getAnnotations());
		scan(operator.getType());
		scan(operator.getTypeCasts());
		scan(operator.getOperand());
		exit(operator);
	}

	@Override
	public <T> void visitCtVariableAccess(CtVariableAccess<T> variableAccess) {
		enter(variableAccess);
		scan(variableAccess.getAnnotations());
		scan(variableAccess.getType());
		scan(variableAccess.getTypeCasts());
		scan(variableAccess.getVariable());
		exit(variableAccess);
	}

	@Override
	public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
		enter(variableRead);
		scan(variableRead.getAnnotations());
		scan(variableRead.getType());
		scan(variableRead.getTypeCasts());
		scan(variableRead.getVariable());
		exit(variableRead);
	}

	@Override
	public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
		enter(variableWrite);
		scan(variableWrite.getAnnotations());
		scan(variableWrite.getType());
		scan(variableWrite.getTypeCasts());
		scan(variableWrite.getVariable());
		exit(variableWrite);
	}

	public void visitCtWhile(CtWhile whileLoop) {
		enter(whileLoop);
		scan(whileLoop.getAnnotations());
		scan(whileLoop.getLoopingExpression());
		scan(whileLoop.getBody());
		exit(whileLoop);
	}

	public <T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression<T> expression) {
		enter(expression);
		exit(expression);
	}

	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
		enter(statement);
		exit(statement);
	}

	public <T> void visitCtUnboundVariableReference(CtUnboundVariableReference<T> reference) {
		enter(reference);
		exit(reference);
	}

	@Override
	public <T> void visitCtFieldAccess(CtFieldAccess<T> f) {
		enter(f);
		scan(f.getAnnotations());
		scan(f.getType());
		scan(f.getTypeCasts());
		scan(f.getTarget());
		scan(f.getVariable());
		exit(f);
	}

	@Override
	public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
		enter(fieldRead);
		scan(fieldRead.getAnnotations());
		scan(fieldRead.getType());
		scan(fieldRead.getTypeCasts());
		scan(fieldRead.getTarget());
		scan(fieldRead.getVariable());
		exit(fieldRead);
	}

	@Override
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
		enter(fieldWrite);
		scan(fieldWrite.getAnnotations());
		scan(fieldWrite.getType());
		scan(fieldWrite.getTypeCasts());
		scan(fieldWrite.getTarget());
		scan(fieldWrite.getVariable());
		exit(fieldWrite);
	}

	@Override
	public <T> void visitCtSuperAccess(CtSuperAccess<T> f) {
		enter(f);
		scan(f.getAnnotations());
		scan(f.getType());
		scan(f.getTypeCasts());
		scan(f.getTarget());
		exit(f);
	}
}
