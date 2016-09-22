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


package spoon.support.visitor.equals;


/**
 * Used to check equality between an element and another one.
 *
 * This class is generated automatically by the processor {@link spoon.generating.EqualsVisitorGenerator}.
 */
public class EqualsVisitor extends spoon.reflect.visitor.CtAbstractBiScanner {
	public static boolean equals(spoon.reflect.declaration.CtElement element, spoon.reflect.declaration.CtElement other) {
		return !(new spoon.support.visitor.equals.EqualsVisitor().biScan(element, other));
	}

	private final spoon.support.visitor.equals.EqualsChecker checker = new spoon.support.visitor.equals.EqualsChecker();

	@java.lang.Override
	protected void enter(spoon.reflect.declaration.CtElement e) {
		super.enter(e);
		checker.setOther(stack.peek());
		checker.scan(e);
		if (checker.isNotEqual()) {
			fail();
		}
	}

	public <A extends java.lang.annotation.Annotation> void visitCtAnnotation(final spoon.reflect.declaration.CtAnnotation<A> annotation) {
		spoon.reflect.declaration.CtAnnotation other = ((spoon.reflect.declaration.CtAnnotation) (stack.peek()));
		enter(annotation);
		biScan(annotation.getAnnotationType(), other.getAnnotationType());
		biScan(annotation.getAnnotations(), other.getAnnotations());
		biScan(annotation.getValues().values(), other.getValues().values());
		exit(annotation);
	}

	public <A extends java.lang.annotation.Annotation> void visitCtAnnotationType(final spoon.reflect.declaration.CtAnnotationType<A> annotationType) {
		spoon.reflect.declaration.CtAnnotationType other = ((spoon.reflect.declaration.CtAnnotationType) (stack.peek()));
		enter(annotationType);
		biScan(annotationType.getAnnotations(), other.getAnnotations());
		biScan(annotationType.getTypeMembers(), other.getTypeMembers());
		exit(annotationType);
	}

	public void visitCtAnonymousExecutable(final spoon.reflect.declaration.CtAnonymousExecutable anonymousExec) {
		spoon.reflect.declaration.CtAnonymousExecutable other = ((spoon.reflect.declaration.CtAnonymousExecutable) (stack.peek()));
		enter(anonymousExec);
		biScan(anonymousExec.getAnnotations(), other.getAnnotations());
		biScan(anonymousExec.getBody(), other.getBody());
		exit(anonymousExec);
	}

	@java.lang.Override
	public <T> void visitCtArrayRead(final spoon.reflect.code.CtArrayRead<T> arrayRead) {
		spoon.reflect.code.CtArrayRead other = ((spoon.reflect.code.CtArrayRead) (stack.peek()));
		enter(arrayRead);
		biScan(arrayRead.getAnnotations(), other.getAnnotations());
		biScan(arrayRead.getType(), other.getType());
		biScan(arrayRead.getTypeCasts(), other.getTypeCasts());
		biScan(arrayRead.getTarget(), other.getTarget());
		biScan(arrayRead.getIndexExpression(), other.getIndexExpression());
		exit(arrayRead);
	}

	@java.lang.Override
	public <T> void visitCtArrayWrite(final spoon.reflect.code.CtArrayWrite<T> arrayWrite) {
		spoon.reflect.code.CtArrayWrite other = ((spoon.reflect.code.CtArrayWrite) (stack.peek()));
		enter(arrayWrite);
		biScan(arrayWrite.getAnnotations(), other.getAnnotations());
		biScan(arrayWrite.getType(), other.getType());
		biScan(arrayWrite.getTypeCasts(), other.getTypeCasts());
		biScan(arrayWrite.getTarget(), other.getTarget());
		biScan(arrayWrite.getIndexExpression(), other.getIndexExpression());
		exit(arrayWrite);
	}

	public <T> void visitCtArrayTypeReference(final spoon.reflect.reference.CtArrayTypeReference<T> reference) {
		spoon.reflect.reference.CtArrayTypeReference other = ((spoon.reflect.reference.CtArrayTypeReference) (stack.peek()));
		enter(reference);
		biScan(reference.getDeclaringType(), other.getDeclaringType());
		biScan(reference.getPackage(), other.getPackage());
		biScan(reference.getComponentType(), other.getComponentType());
		biScan(reference.getAnnotations(), other.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtAssert(final spoon.reflect.code.CtAssert<T> asserted) {
		spoon.reflect.code.CtAssert other = ((spoon.reflect.code.CtAssert) (stack.peek()));
		enter(asserted);
		biScan(asserted.getAnnotations(), other.getAnnotations());
		biScan(asserted.getAssertExpression(), other.getAssertExpression());
		biScan(asserted.getExpression(), other.getExpression());
		exit(asserted);
	}

	public <T, A extends T> void visitCtAssignment(final spoon.reflect.code.CtAssignment<T, A> assignement) {
		spoon.reflect.code.CtAssignment other = ((spoon.reflect.code.CtAssignment) (stack.peek()));
		enter(assignement);
		biScan(assignement.getAnnotations(), other.getAnnotations());
		biScan(assignement.getType(), other.getType());
		biScan(assignement.getTypeCasts(), other.getTypeCasts());
		biScan(assignement.getAssigned(), other.getAssigned());
		biScan(assignement.getAssignment(), other.getAssignment());
		exit(assignement);
	}

	public <T> void visitCtBinaryOperator(final spoon.reflect.code.CtBinaryOperator<T> operator) {
		spoon.reflect.code.CtBinaryOperator other = ((spoon.reflect.code.CtBinaryOperator) (stack.peek()));
		enter(operator);
		biScan(operator.getAnnotations(), other.getAnnotations());
		biScan(operator.getType(), other.getType());
		biScan(operator.getTypeCasts(), other.getTypeCasts());
		biScan(operator.getLeftHandOperand(), other.getLeftHandOperand());
		biScan(operator.getRightHandOperand(), other.getRightHandOperand());
		exit(operator);
	}

	public <R> void visitCtBlock(final spoon.reflect.code.CtBlock<R> block) {
		spoon.reflect.code.CtBlock other = ((spoon.reflect.code.CtBlock) (stack.peek()));
		enter(block);
		biScan(block.getAnnotations(), other.getAnnotations());
		biScan(block.getStatements(), other.getStatements());
		exit(block);
	}

	public void visitCtBreak(final spoon.reflect.code.CtBreak breakStatement) {
		spoon.reflect.code.CtBreak other = ((spoon.reflect.code.CtBreak) (stack.peek()));
		enter(breakStatement);
		biScan(breakStatement.getAnnotations(), other.getAnnotations());
		exit(breakStatement);
	}

	public <S> void visitCtCase(final spoon.reflect.code.CtCase<S> caseStatement) {
		spoon.reflect.code.CtCase other = ((spoon.reflect.code.CtCase) (stack.peek()));
		enter(caseStatement);
		biScan(caseStatement.getAnnotations(), other.getAnnotations());
		biScan(caseStatement.getCaseExpression(), other.getCaseExpression());
		biScan(caseStatement.getStatements(), other.getStatements());
		exit(caseStatement);
	}

	public void visitCtCatch(final spoon.reflect.code.CtCatch catchBlock) {
		spoon.reflect.code.CtCatch other = ((spoon.reflect.code.CtCatch) (stack.peek()));
		enter(catchBlock);
		biScan(catchBlock.getAnnotations(), other.getAnnotations());
		biScan(catchBlock.getParameter(), other.getParameter());
		biScan(catchBlock.getBody(), other.getBody());
		exit(catchBlock);
	}

	public <T> void visitCtClass(final spoon.reflect.declaration.CtClass<T> ctClass) {
		spoon.reflect.declaration.CtClass other = ((spoon.reflect.declaration.CtClass) (stack.peek()));
		enter(ctClass);
		biScan(ctClass.getAnnotations(), other.getAnnotations());
		biScan(ctClass.getSuperclass(), other.getSuperclass());
		biScan(ctClass.getSuperInterfaces(), other.getSuperInterfaces());
		biScan(ctClass.getFormalCtTypeParameters(), other.getFormalCtTypeParameters());
		biScan(ctClass.getTypeMembers(), other.getTypeMembers());
		exit(ctClass);
	}

	@java.lang.Override
	public void visitCtTypeParameter(spoon.reflect.declaration.CtTypeParameter typeParameter) {
		spoon.reflect.declaration.CtTypeParameter other = ((spoon.reflect.declaration.CtTypeParameter) (stack.peek()));
		enter(typeParameter);
		biScan(typeParameter.getAnnotations(), other.getAnnotations());
		biScan(typeParameter.getSuperclass(), other.getSuperclass());
		exit(typeParameter);
	}

	public <T> void visitCtConditional(final spoon.reflect.code.CtConditional<T> conditional) {
		spoon.reflect.code.CtConditional other = ((spoon.reflect.code.CtConditional) (stack.peek()));
		enter(conditional);
		biScan(conditional.getAnnotations(), other.getAnnotations());
		biScan(conditional.getCondition(), other.getCondition());
		biScan(conditional.getThenExpression(), other.getThenExpression());
		biScan(conditional.getElseExpression(), other.getElseExpression());
		biScan(conditional.getTypeCasts(), other.getTypeCasts());
		exit(conditional);
	}

	public <T> void visitCtConstructor(final spoon.reflect.declaration.CtConstructor<T> c) {
		spoon.reflect.declaration.CtConstructor other = ((spoon.reflect.declaration.CtConstructor) (stack.peek()));
		enter(c);
		biScan(c.getAnnotations(), other.getAnnotations());
		biScan(c.getParameters(), other.getParameters());
		biScan(c.getThrownTypes(), other.getThrownTypes());
		biScan(c.getFormalCtTypeParameters(), other.getFormalCtTypeParameters());
		biScan(c.getBody(), other.getBody());
		exit(c);
	}

	public void visitCtContinue(final spoon.reflect.code.CtContinue continueStatement) {
		spoon.reflect.code.CtContinue other = ((spoon.reflect.code.CtContinue) (stack.peek()));
		enter(continueStatement);
		biScan(continueStatement.getAnnotations(), other.getAnnotations());
		biScan(continueStatement.getLabelledStatement(), other.getLabelledStatement());
		exit(continueStatement);
	}

	public void visitCtDo(final spoon.reflect.code.CtDo doLoop) {
		spoon.reflect.code.CtDo other = ((spoon.reflect.code.CtDo) (stack.peek()));
		enter(doLoop);
		biScan(doLoop.getAnnotations(), other.getAnnotations());
		biScan(doLoop.getLoopingExpression(), other.getLoopingExpression());
		biScan(doLoop.getBody(), other.getBody());
		exit(doLoop);
	}

	public <T extends java.lang.Enum<?>> void visitCtEnum(final spoon.reflect.declaration.CtEnum<T> ctEnum) {
		spoon.reflect.declaration.CtEnum other = ((spoon.reflect.declaration.CtEnum) (stack.peek()));
		enter(ctEnum);
		biScan(ctEnum.getAnnotations(), other.getAnnotations());
		biScan(ctEnum.getSuperInterfaces(), other.getSuperInterfaces());
		biScan(ctEnum.getTypeMembers(), other.getTypeMembers());
		biScan(ctEnum.getEnumValues(), other.getEnumValues());
		exit(ctEnum);
	}

	public <T> void visitCtExecutableReference(final spoon.reflect.reference.CtExecutableReference<T> reference) {
		spoon.reflect.reference.CtExecutableReference other = ((spoon.reflect.reference.CtExecutableReference) (stack.peek()));
		enter(reference);
		biScan(reference.getDeclaringType(), other.getDeclaringType());
		biScan(reference.getParameters(), other.getParameters());
		biScan(reference.getAnnotations(), other.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtField(final spoon.reflect.declaration.CtField<T> f) {
		spoon.reflect.declaration.CtField other = ((spoon.reflect.declaration.CtField) (stack.peek()));
		enter(f);
		biScan(f.getAnnotations(), other.getAnnotations());
		biScan(f.getType(), other.getType());
		biScan(f.getDefaultExpression(), other.getDefaultExpression());
		exit(f);
	}

	@java.lang.Override
	public <T> void visitCtEnumValue(final spoon.reflect.declaration.CtEnumValue<T> enumValue) {
		spoon.reflect.declaration.CtEnumValue other = ((spoon.reflect.declaration.CtEnumValue) (stack.peek()));
		enter(enumValue);
		biScan(enumValue.getAnnotations(), other.getAnnotations());
		biScan(enumValue.getType(), other.getType());
		biScan(enumValue.getDefaultExpression(), other.getDefaultExpression());
		exit(enumValue);
	}

	@java.lang.Override
	public <T> void visitCtThisAccess(final spoon.reflect.code.CtThisAccess<T> thisAccess) {
		spoon.reflect.code.CtThisAccess other = ((spoon.reflect.code.CtThisAccess) (stack.peek()));
		enter(thisAccess);
		biScan(thisAccess.getType(), other.getType());
		biScan(thisAccess.getTypeCasts(), other.getTypeCasts());
		biScan(thisAccess.getTarget(), other.getTarget());
		exit(thisAccess);
	}

	public <T> void visitCtAnnotationFieldAccess(final spoon.reflect.code.CtAnnotationFieldAccess<T> annotationFieldAccess) {
		spoon.reflect.code.CtAnnotationFieldAccess other = ((spoon.reflect.code.CtAnnotationFieldAccess) (stack.peek()));
		enter(annotationFieldAccess);
		biScan(annotationFieldAccess.getAnnotations(), other.getAnnotations());
		biScan(annotationFieldAccess.getType(), other.getType());
		biScan(annotationFieldAccess.getTypeCasts(), other.getTypeCasts());
		biScan(annotationFieldAccess.getTarget(), other.getTarget());
		biScan(annotationFieldAccess.getVariable(), other.getVariable());
		exit(annotationFieldAccess);
	}

	public <T> void visitCtFieldReference(final spoon.reflect.reference.CtFieldReference<T> reference) {
		spoon.reflect.reference.CtFieldReference other = ((spoon.reflect.reference.CtFieldReference) (stack.peek()));
		enter(reference);
		biScan(reference.getDeclaringType(), other.getDeclaringType());
		biScan(reference.getType(), other.getType());
		biScan(reference.getAnnotations(), other.getAnnotations());
		exit(reference);
	}

	public void visitCtFor(final spoon.reflect.code.CtFor forLoop) {
		spoon.reflect.code.CtFor other = ((spoon.reflect.code.CtFor) (stack.peek()));
		enter(forLoop);
		biScan(forLoop.getAnnotations(), other.getAnnotations());
		biScan(forLoop.getForInit(), other.getForInit());
		biScan(forLoop.getExpression(), other.getExpression());
		biScan(forLoop.getForUpdate(), other.getForUpdate());
		biScan(forLoop.getBody(), other.getBody());
		exit(forLoop);
	}

	public void visitCtForEach(final spoon.reflect.code.CtForEach foreach) {
		spoon.reflect.code.CtForEach other = ((spoon.reflect.code.CtForEach) (stack.peek()));
		enter(foreach);
		biScan(foreach.getAnnotations(), other.getAnnotations());
		biScan(foreach.getVariable(), other.getVariable());
		biScan(foreach.getExpression(), other.getExpression());
		biScan(foreach.getBody(), other.getBody());
		exit(foreach);
	}

	public void visitCtIf(final spoon.reflect.code.CtIf ifElement) {
		spoon.reflect.code.CtIf other = ((spoon.reflect.code.CtIf) (stack.peek()));
		enter(ifElement);
		biScan(ifElement.getAnnotations(), other.getAnnotations());
		biScan(ifElement.getCondition(), other.getCondition());
		biScan(((spoon.reflect.code.CtStatement) (ifElement.getThenStatement())), other.getThenStatement());
		biScan(((spoon.reflect.code.CtStatement) (ifElement.getElseStatement())), other.getElseStatement());
		exit(ifElement);
	}

	public <T> void visitCtInterface(final spoon.reflect.declaration.CtInterface<T> intrface) {
		spoon.reflect.declaration.CtInterface other = ((spoon.reflect.declaration.CtInterface) (stack.peek()));
		enter(intrface);
		biScan(intrface.getAnnotations(), other.getAnnotations());
		biScan(intrface.getSuperInterfaces(), other.getSuperInterfaces());
		biScan(intrface.getFormalCtTypeParameters(), other.getFormalCtTypeParameters());
		biScan(intrface.getTypeMembers(), other.getTypeMembers());
		exit(intrface);
	}

	public <T> void visitCtInvocation(final spoon.reflect.code.CtInvocation<T> invocation) {
		spoon.reflect.code.CtInvocation other = ((spoon.reflect.code.CtInvocation) (stack.peek()));
		enter(invocation);
		biScan(invocation.getAnnotations(), other.getAnnotations());
		biScan(invocation.getTypeCasts(), other.getTypeCasts());
		biScan(invocation.getTarget(), other.getTarget());
		biScan(invocation.getExecutable(), other.getExecutable());
		biScan(invocation.getArguments(), other.getArguments());
		exit(invocation);
	}

	public <T> void visitCtLiteral(final spoon.reflect.code.CtLiteral<T> literal) {
		spoon.reflect.code.CtLiteral other = ((spoon.reflect.code.CtLiteral) (stack.peek()));
		enter(literal);
		biScan(literal.getAnnotations(), other.getAnnotations());
		biScan(literal.getType(), other.getType());
		biScan(literal.getTypeCasts(), other.getTypeCasts());
		exit(literal);
	}

	public <T> void visitCtLocalVariable(final spoon.reflect.code.CtLocalVariable<T> localVariable) {
		spoon.reflect.code.CtLocalVariable other = ((spoon.reflect.code.CtLocalVariable) (stack.peek()));
		enter(localVariable);
		biScan(localVariable.getAnnotations(), other.getAnnotations());
		biScan(localVariable.getType(), other.getType());
		biScan(localVariable.getDefaultExpression(), other.getDefaultExpression());
		exit(localVariable);
	}

	public <T> void visitCtLocalVariableReference(final spoon.reflect.reference.CtLocalVariableReference<T> reference) {
		spoon.reflect.reference.CtLocalVariableReference other = ((spoon.reflect.reference.CtLocalVariableReference) (stack.peek()));
		enter(reference);
		biScan(reference.getType(), other.getType());
		biScan(reference.getAnnotations(), other.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtCatchVariable(final spoon.reflect.code.CtCatchVariable<T> catchVariable) {
		spoon.reflect.code.CtCatchVariable other = ((spoon.reflect.code.CtCatchVariable) (stack.peek()));
		enter(catchVariable);
		biScan(catchVariable.getAnnotations(), other.getAnnotations());
		biScan(catchVariable.getType(), other.getType());
		exit(catchVariable);
	}

	public <T> void visitCtCatchVariableReference(final spoon.reflect.reference.CtCatchVariableReference<T> reference) {
		spoon.reflect.reference.CtCatchVariableReference other = ((spoon.reflect.reference.CtCatchVariableReference) (stack.peek()));
		enter(reference);
		biScan(reference.getType(), other.getType());
		biScan(reference.getAnnotations(), other.getAnnotations());
		exit(reference);
	}

	public <T> void visitCtMethod(final spoon.reflect.declaration.CtMethod<T> m) {
		spoon.reflect.declaration.CtMethod other = ((spoon.reflect.declaration.CtMethod) (stack.peek()));
		enter(m);
		biScan(m.getAnnotations(), other.getAnnotations());
		biScan(m.getFormalCtTypeParameters(), other.getFormalCtTypeParameters());
		biScan(m.getType(), other.getType());
		biScan(m.getParameters(), other.getParameters());
		biScan(m.getThrownTypes(), other.getThrownTypes());
		biScan(m.getBody(), other.getBody());
		exit(m);
	}

	@java.lang.Override
	public <T> void visitCtAnnotationMethod(spoon.reflect.declaration.CtAnnotationMethod<T> annotationMethod) {
		spoon.reflect.declaration.CtAnnotationMethod other = ((spoon.reflect.declaration.CtAnnotationMethod) (stack.peek()));
		enter(annotationMethod);
		biScan(annotationMethod.getAnnotations(), other.getAnnotations());
		biScan(annotationMethod.getType(), other.getType());
		biScan(annotationMethod.getDefaultExpression(), other.getDefaultExpression());
		exit(annotationMethod);
	}

	public <T> void visitCtNewArray(final spoon.reflect.code.CtNewArray<T> newArray) {
		spoon.reflect.code.CtNewArray other = ((spoon.reflect.code.CtNewArray) (stack.peek()));
		enter(newArray);
		biScan(newArray.getAnnotations(), other.getAnnotations());
		biScan(newArray.getType(), other.getType());
		biScan(newArray.getTypeCasts(), other.getTypeCasts());
		biScan(newArray.getElements(), other.getElements());
		biScan(newArray.getDimensionExpressions(), other.getDimensionExpressions());
		exit(newArray);
	}

	@java.lang.Override
	public <T> void visitCtConstructorCall(final spoon.reflect.code.CtConstructorCall<T> ctConstructorCall) {
		spoon.reflect.code.CtConstructorCall other = ((spoon.reflect.code.CtConstructorCall) (stack.peek()));
		enter(ctConstructorCall);
		biScan(ctConstructorCall.getAnnotations(), other.getAnnotations());
		biScan(ctConstructorCall.getTypeCasts(), other.getTypeCasts());
		biScan(ctConstructorCall.getExecutable(), other.getExecutable());
		biScan(ctConstructorCall.getTarget(), other.getTarget());
		biScan(ctConstructorCall.getArguments(), other.getArguments());
		exit(ctConstructorCall);
	}

	public <T> void visitCtNewClass(final spoon.reflect.code.CtNewClass<T> newClass) {
		spoon.reflect.code.CtNewClass other = ((spoon.reflect.code.CtNewClass) (stack.peek()));
		enter(newClass);
		biScan(newClass.getAnnotations(), other.getAnnotations());
		biScan(newClass.getTypeCasts(), other.getTypeCasts());
		biScan(newClass.getExecutable(), other.getExecutable());
		biScan(newClass.getTarget(), other.getTarget());
		biScan(newClass.getArguments(), other.getArguments());
		biScan(newClass.getAnonymousClass(), other.getAnonymousClass());
		exit(newClass);
	}

	@java.lang.Override
	public <T> void visitCtLambda(final spoon.reflect.code.CtLambda<T> lambda) {
		spoon.reflect.code.CtLambda other = ((spoon.reflect.code.CtLambda) (stack.peek()));
		enter(lambda);
		biScan(lambda.getAnnotations(), other.getAnnotations());
		biScan(lambda.getType(), other.getType());
		biScan(lambda.getTypeCasts(), other.getTypeCasts());
		biScan(lambda.getParameters(), other.getParameters());
		biScan(lambda.getBody(), other.getBody());
		biScan(lambda.getExpression(), other.getExpression());
		exit(lambda);
	}

	@java.lang.Override
	public <T, E extends spoon.reflect.code.CtExpression<?>> void visitCtExecutableReferenceExpression(final spoon.reflect.code.CtExecutableReferenceExpression<T, E> expression) {
		spoon.reflect.code.CtExecutableReferenceExpression other = ((spoon.reflect.code.CtExecutableReferenceExpression) (stack.peek()));
		enter(expression);
		biScan(expression.getType(), other.getType());
		biScan(expression.getTypeCasts(), other.getTypeCasts());
		biScan(expression.getExecutable(), other.getExecutable());
		biScan(expression.getTarget(), other.getTarget());
		exit(expression);
	}

	public <T, A extends T> void visitCtOperatorAssignment(final spoon.reflect.code.CtOperatorAssignment<T, A> assignment) {
		spoon.reflect.code.CtOperatorAssignment other = ((spoon.reflect.code.CtOperatorAssignment) (stack.peek()));
		enter(assignment);
		biScan(assignment.getAnnotations(), other.getAnnotations());
		biScan(assignment.getType(), other.getType());
		biScan(assignment.getTypeCasts(), other.getTypeCasts());
		biScan(assignment.getAssigned(), other.getAssigned());
		biScan(assignment.getAssignment(), other.getAssignment());
		exit(assignment);
	}

	public void visitCtPackage(final spoon.reflect.declaration.CtPackage ctPackage) {
		spoon.reflect.declaration.CtPackage other = ((spoon.reflect.declaration.CtPackage) (stack.peek()));
		enter(ctPackage);
		biScan(ctPackage.getAnnotations(), other.getAnnotations());
		biScan(ctPackage.getPackages(), other.getPackages());
		biScan(ctPackage.getTypes(), other.getTypes());
		exit(ctPackage);
	}

	public void visitCtPackageReference(final spoon.reflect.reference.CtPackageReference reference) {
		spoon.reflect.reference.CtPackageReference other = ((spoon.reflect.reference.CtPackageReference) (stack.peek()));
		enter(reference);
		exit(reference);
	}

	public <T> void visitCtParameter(final spoon.reflect.declaration.CtParameter<T> parameter) {
		spoon.reflect.declaration.CtParameter other = ((spoon.reflect.declaration.CtParameter) (stack.peek()));
		enter(parameter);
		biScan(parameter.getAnnotations(), other.getAnnotations());
		biScan(parameter.getType(), other.getType());
		exit(parameter);
	}

	public <T> void visitCtParameterReference(final spoon.reflect.reference.CtParameterReference<T> reference) {
		spoon.reflect.reference.CtParameterReference other = ((spoon.reflect.reference.CtParameterReference) (stack.peek()));
		enter(reference);
		biScan(reference.getType(), other.getType());
		biScan(reference.getAnnotations(), other.getAnnotations());
		biScan(reference.getDeclaringExecutable(), other.getDeclaringExecutable());
		exit(reference);
	}

	public <R> void visitCtReturn(final spoon.reflect.code.CtReturn<R> returnStatement) {
		spoon.reflect.code.CtReturn other = ((spoon.reflect.code.CtReturn) (stack.peek()));
		enter(returnStatement);
		biScan(returnStatement.getAnnotations(), other.getAnnotations());
		biScan(returnStatement.getReturnedExpression(), other.getReturnedExpression());
		exit(returnStatement);
	}

	public <R> void visitCtStatementList(final spoon.reflect.code.CtStatementList statements) {
		spoon.reflect.code.CtStatementList other = ((spoon.reflect.code.CtStatementList) (stack.peek()));
		enter(statements);
		biScan(statements.getAnnotations(), other.getAnnotations());
		biScan(statements.getStatements(), other.getStatements());
		exit(statements);
	}

	public <S> void visitCtSwitch(final spoon.reflect.code.CtSwitch<S> switchStatement) {
		spoon.reflect.code.CtSwitch other = ((spoon.reflect.code.CtSwitch) (stack.peek()));
		enter(switchStatement);
		biScan(switchStatement.getAnnotations(), other.getAnnotations());
		biScan(switchStatement.getSelector(), other.getSelector());
		biScan(switchStatement.getCases(), other.getCases());
		exit(switchStatement);
	}

	public void visitCtSynchronized(final spoon.reflect.code.CtSynchronized synchro) {
		spoon.reflect.code.CtSynchronized other = ((spoon.reflect.code.CtSynchronized) (stack.peek()));
		enter(synchro);
		biScan(synchro.getAnnotations(), other.getAnnotations());
		biScan(synchro.getExpression(), other.getExpression());
		biScan(synchro.getBlock(), other.getBlock());
		exit(synchro);
	}

	public void visitCtThrow(final spoon.reflect.code.CtThrow throwStatement) {
		spoon.reflect.code.CtThrow other = ((spoon.reflect.code.CtThrow) (stack.peek()));
		enter(throwStatement);
		biScan(throwStatement.getAnnotations(), other.getAnnotations());
		biScan(throwStatement.getThrownExpression(), other.getThrownExpression());
		exit(throwStatement);
	}

	public void visitCtTry(final spoon.reflect.code.CtTry tryBlock) {
		spoon.reflect.code.CtTry other = ((spoon.reflect.code.CtTry) (stack.peek()));
		enter(tryBlock);
		biScan(tryBlock.getAnnotations(), other.getAnnotations());
		biScan(tryBlock.getBody(), other.getBody());
		biScan(tryBlock.getCatchers(), other.getCatchers());
		biScan(tryBlock.getFinalizer(), other.getFinalizer());
		exit(tryBlock);
	}

	@java.lang.Override
	public void visitCtTryWithResource(final spoon.reflect.code.CtTryWithResource tryWithResource) {
		spoon.reflect.code.CtTryWithResource other = ((spoon.reflect.code.CtTryWithResource) (stack.peek()));
		enter(tryWithResource);
		biScan(tryWithResource.getAnnotations(), other.getAnnotations());
		biScan(tryWithResource.getResources(), other.getResources());
		biScan(tryWithResource.getBody(), other.getBody());
		biScan(tryWithResource.getCatchers(), other.getCatchers());
		biScan(tryWithResource.getFinalizer(), other.getFinalizer());
		exit(tryWithResource);
	}

	public void visitCtTypeParameterReference(final spoon.reflect.reference.CtTypeParameterReference ref) {
		spoon.reflect.reference.CtTypeParameterReference other = ((spoon.reflect.reference.CtTypeParameterReference) (stack.peek()));
		enter(ref);
		biScan(ref.getPackage(), other.getPackage());
		biScan(ref.getDeclaringType(), other.getDeclaringType());
		biScan(ref.getAnnotations(), other.getAnnotations());
		biScan(ref.getBoundingType(), other.getBoundingType());
		exit(ref);
	}

	@java.lang.Override
	public void visitCtWildcardReference(spoon.reflect.reference.CtWildcardReference wildcardReference) {
		spoon.reflect.reference.CtWildcardReference other = ((spoon.reflect.reference.CtWildcardReference) (stack.peek()));
		enter(wildcardReference);
		biScan(wildcardReference.getAnnotations(), other.getAnnotations());
		biScan(wildcardReference.getBoundingType(), other.getBoundingType());
		exit(wildcardReference);
	}

	@java.lang.Override
	public <T> void visitCtIntersectionTypeReference(final spoon.reflect.reference.CtIntersectionTypeReference<T> reference) {
		spoon.reflect.reference.CtIntersectionTypeReference other = ((spoon.reflect.reference.CtIntersectionTypeReference) (stack.peek()));
		enter(reference);
		biScan(reference.getBounds(), other.getBounds());
		exit(reference);
	}

	public <T> void visitCtTypeReference(final spoon.reflect.reference.CtTypeReference<T> reference) {
		spoon.reflect.reference.CtTypeReference other = ((spoon.reflect.reference.CtTypeReference) (stack.peek()));
		enter(reference);
		biScan(reference.getPackage(), other.getPackage());
		biScan(reference.getDeclaringType(), other.getDeclaringType());
		biScan(reference.getAnnotations(), other.getAnnotations());
		exit(reference);
	}

	@java.lang.Override
	public <T> void visitCtTypeAccess(final spoon.reflect.code.CtTypeAccess<T> typeAccess) {
		spoon.reflect.code.CtTypeAccess other = ((spoon.reflect.code.CtTypeAccess) (stack.peek()));
		enter(typeAccess);
		biScan(typeAccess.getAnnotations(), other.getAnnotations());
		biScan(typeAccess.getTypeCasts(), other.getTypeCasts());
		biScan(typeAccess.getAccessedType(), other.getAccessedType());
		exit(typeAccess);
	}

	public <T> void visitCtUnaryOperator(final spoon.reflect.code.CtUnaryOperator<T> operator) {
		spoon.reflect.code.CtUnaryOperator other = ((spoon.reflect.code.CtUnaryOperator) (stack.peek()));
		enter(operator);
		biScan(operator.getAnnotations(), other.getAnnotations());
		biScan(operator.getType(), other.getType());
		biScan(operator.getTypeCasts(), other.getTypeCasts());
		biScan(operator.getOperand(), other.getOperand());
		exit(operator);
	}

	@java.lang.Override
	public <T> void visitCtVariableRead(final spoon.reflect.code.CtVariableRead<T> variableRead) {
		spoon.reflect.code.CtVariableRead other = ((spoon.reflect.code.CtVariableRead) (stack.peek()));
		enter(variableRead);
		biScan(variableRead.getAnnotations(), other.getAnnotations());
		biScan(variableRead.getTypeCasts(), other.getTypeCasts());
		biScan(variableRead.getVariable(), other.getVariable());
		exit(variableRead);
	}

	@java.lang.Override
	public <T> void visitCtVariableWrite(final spoon.reflect.code.CtVariableWrite<T> variableWrite) {
		spoon.reflect.code.CtVariableWrite other = ((spoon.reflect.code.CtVariableWrite) (stack.peek()));
		enter(variableWrite);
		biScan(variableWrite.getAnnotations(), other.getAnnotations());
		biScan(variableWrite.getTypeCasts(), other.getTypeCasts());
		biScan(variableWrite.getVariable(), other.getVariable());
		exit(variableWrite);
	}

	public void visitCtWhile(final spoon.reflect.code.CtWhile whileLoop) {
		spoon.reflect.code.CtWhile other = ((spoon.reflect.code.CtWhile) (stack.peek()));
		enter(whileLoop);
		biScan(whileLoop.getAnnotations(), other.getAnnotations());
		biScan(whileLoop.getLoopingExpression(), other.getLoopingExpression());
		biScan(whileLoop.getBody(), other.getBody());
		exit(whileLoop);
	}

	public <T> void visitCtCodeSnippetExpression(final spoon.reflect.code.CtCodeSnippetExpression<T> expression) {
		spoon.reflect.code.CtCodeSnippetExpression other = ((spoon.reflect.code.CtCodeSnippetExpression) (stack.peek()));
		enter(expression);
		exit(expression);
	}

	public void visitCtCodeSnippetStatement(final spoon.reflect.code.CtCodeSnippetStatement statement) {
		spoon.reflect.code.CtCodeSnippetStatement other = ((spoon.reflect.code.CtCodeSnippetStatement) (stack.peek()));
		enter(statement);
		exit(statement);
	}

	public <T> void visitCtUnboundVariableReference(final spoon.reflect.reference.CtUnboundVariableReference<T> reference) {
		spoon.reflect.reference.CtUnboundVariableReference other = ((spoon.reflect.reference.CtUnboundVariableReference) (stack.peek()));
		enter(reference);
		exit(reference);
	}

	@java.lang.Override
	public <T> void visitCtFieldRead(final spoon.reflect.code.CtFieldRead<T> fieldRead) {
		spoon.reflect.code.CtFieldRead other = ((spoon.reflect.code.CtFieldRead) (stack.peek()));
		enter(fieldRead);
		biScan(fieldRead.getAnnotations(), other.getAnnotations());
		biScan(fieldRead.getTypeCasts(), other.getTypeCasts());
		biScan(fieldRead.getTarget(), other.getTarget());
		biScan(fieldRead.getVariable(), other.getVariable());
		exit(fieldRead);
	}

	@java.lang.Override
	public <T> void visitCtFieldWrite(final spoon.reflect.code.CtFieldWrite<T> fieldWrite) {
		spoon.reflect.code.CtFieldWrite other = ((spoon.reflect.code.CtFieldWrite) (stack.peek()));
		enter(fieldWrite);
		biScan(fieldWrite.getAnnotations(), other.getAnnotations());
		biScan(fieldWrite.getTypeCasts(), other.getTypeCasts());
		biScan(fieldWrite.getTarget(), other.getTarget());
		biScan(fieldWrite.getVariable(), other.getVariable());
		exit(fieldWrite);
	}

	@java.lang.Override
	public <T> void visitCtSuperAccess(final spoon.reflect.code.CtSuperAccess<T> f) {
		spoon.reflect.code.CtSuperAccess other = ((spoon.reflect.code.CtSuperAccess) (stack.peek()));
		enter(f);
		biScan(f.getAnnotations(), other.getAnnotations());
		biScan(f.getType(), other.getType());
		biScan(f.getTypeCasts(), other.getTypeCasts());
		biScan(f.getTarget(), other.getTarget());
		exit(f);
	}

	@java.lang.Override
	public void visitCtComment(final spoon.reflect.code.CtComment comment) {
		spoon.reflect.code.CtComment other = ((spoon.reflect.code.CtComment) (stack.peek()));
		enter(comment);
		exit(comment);
	}
}

