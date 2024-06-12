/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import java.lang.annotation.Annotation;

import spoon.reflect.code.*;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtReceiverParameter;
import spoon.reflect.declaration.CtRecord;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtWildcardReference;

/** Provides an empty implementation of CtVisitor.
 *  See {@link CtScanner} for a much more powerful implementation of CtVisitor.
 */
public abstract class CtAbstractVisitor implements CtVisitor {
	@Override
	public <A extends Annotation> void visitCtAnnotation(
			CtAnnotation<A> annotation) {

	}

	@Override
	public <T> void visitCtCodeSnippetExpression(
			CtCodeSnippetExpression<T> expression) {

	}

	@Override
	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {

	}

	@Override
	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {

	}

	@Override
	public void visitCtAnonymousExecutable(CtAnonymousExecutable anonymousExec) {

	}

	@Override
	public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {

	}

	@Override
	public <T> void visitCtArrayWrite(CtArrayWrite<T> arrayWrite) {

	}

	@Override
	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {

	}

	@Override
	public <T> void visitCtAssert(CtAssert<T> asserted) {

	}

	@Override
	public <T, A extends T> void visitCtAssignment(
			CtAssignment<T, A> assignement) {

	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {

	}

	@Override
	public <R> void visitCtBlock(CtBlock<R> block) {

	}

	@Override
	public void visitCtBreak(CtBreak breakStatement) {

	}

	@Override
	public <S> void visitCtCase(CtCase<S> caseStatement) {

	}

	@Override
	public void visitCtCatch(CtCatch catchBlock) {

	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {

	}

	@Override
	public void visitCtTypeParameter(CtTypeParameter typeParameter) {

	}

	@Override
	public <T> void visitCtConditional(CtConditional<T> conditional) {

	}

	@Override
	public <T> void visitCtConstructor(CtConstructor<T> c) {

	}

	@Override
	public void visitCtContinue(CtContinue continueStatement) {

	}

	@Override
	public void visitCtDo(CtDo doLoop) {

	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {

	}

	@Override
	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {

	}

	@Override
	public <T> void visitCtField(CtField<T> f) {

	}

	@Override
	public <T> void visitCtEnumValue(CtEnumValue<T> enumValue) {

	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {

	}

	@Override
	public void visitCtFor(CtFor forLoop) {

	}

	@Override
	public void visitCtForEach(CtForEach foreach) {

	}

	@Override
	public void visitCtIf(CtIf ifElement) {

	}

	@Override
	public <T> void visitCtInterface(CtInterface<T> intrface) {

	}

	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {

	}

	@Override
	public <T> void visitCtLiteral(CtLiteral<T> literal) {

	}

	@Override
	public void visitCtTextBlock(CtTextBlock ctTextBlock) {

	}

	@Override
	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {

	}

	@Override
	public <T> void visitCtLocalVariableReference(
			CtLocalVariableReference<T> reference) {

	}

	@Override
	public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {

	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {

	}

	@Override
	public <T> void visitCtAnnotationMethod(CtAnnotationMethod<T> annotationMethod) {

	}

	@Override
	public <T> void visitCtNewArray(CtNewArray<T> newArray) {

	}

	@Override
	public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {

	}

	@Override
	public <T> void visitCtNewClass(CtNewClass<T> newClass) {

	}

	@Override
	public <T> void visitCtLambda(CtLambda<T> lambda) {

	}

	@Override
	public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(
			CtExecutableReferenceExpression<T, E> expression) {

	}

	@Override
	public <T, A extends T> void visitCtOperatorAssignment(CtOperatorAssignment<T, A> assignment) {

	}

	@Override
	public void visitCtPackage(CtPackage ctPackage) {

	}

	@Override
	public void visitCtPackageReference(CtPackageReference reference) {

	}

	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {

	}

	@Override
	public <T> void visitCtParameterReference(CtParameterReference<T> reference) {

	}

	@Override
	public <R> void visitCtReturn(CtReturn<R> returnStatement) {

	}

	@Override
	public <R> void visitCtStatementList(CtStatementList statements) {

	}

	@Override
	public <S> void visitCtSwitch(CtSwitch<S> switchStatement) {

	}

	@Override
	public <T, S> void visitCtSwitchExpression(CtSwitchExpression<T, S> switchExpression) {

	}

	@Override
	public void visitCtSynchronized(CtSynchronized synchro) {

	}

	@Override
	public void visitCtThrow(CtThrow throwStatement) {

	}

	@Override
	public void visitCtTry(CtTry tryBlock) {

	}

	@Override
	public void visitCtTryWithResource(CtTryWithResource tryWithResource) {

	}

	@Override
	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {

	}

	@Override
	public void visitCtWildcardReference(CtWildcardReference wildcardReference) {

	}

	@Override
	public <T> void visitCtIntersectionTypeReference(CtIntersectionTypeReference<T> reference) {

	}

	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {

	}

	@Override
	public <T> void visitCtTypeAccess(CtTypeAccess<T> typeAccess) {

	}

	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {

	}

	@Override
	public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {

	}

	@Override
	public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {

	}

	@Override
	public void visitCtWhile(CtWhile whileLoop) {

	}

	@Override
	public <T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess<T> annotationFieldAccess) {

	}

	@Override
	public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
	}

	@Override
	public <T> void visitCtCatchVariableReference(CtCatchVariableReference<T> reference) {
	}

	@Override
	public <T> void visitCtUnboundVariableReference(CtUnboundVariableReference<T> reference) {

	}

	@Override
	public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {

	}

	@Override
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {

	}

	@Override
	public <T> void visitCtSuperAccess(CtSuperAccess<T> f) {

	}

	@Override
	public void visitCtComment(CtComment comment) {

	}

	@Override
	public void visitCtJavaDoc(CtJavaDoc javadoc) {

	}

	@Override
	public void visitCtJavaDocTag(CtJavaDocTag docTag) {

	}

	@Override
	public void visitCtImport(CtImport ctImport) {

	}

	@Override
	public void visitCtModule(CtModule module) {

	}

	@Override
	public void visitCtModuleReference(CtModuleReference moduleReference) {

	}

	@Override
	public void visitCtPackageExport(CtPackageExport moduleExport) {

	}

	@Override
	public void visitCtModuleRequirement(CtModuleRequirement moduleRequirement) {

	}

	@Override
	public void visitCtProvidedService(CtProvidedService moduleProvidedService) {

	}

	@Override
	public void visitCtUsedService(CtUsedService usedService) {

	}

	@Override
	public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {

	}

	@Override
	public void visitCtPackageDeclaration(CtPackageDeclaration packageDeclaration) {

	}

	@Override
	public void visitCtTypeMemberWildcardImportReference(CtTypeMemberWildcardImportReference wildcardReference) {

	}

	@Override
	public void visitCtYieldStatement(CtYieldStatement statement) {

	}

	@Override
	public void visitCtTypePattern(CtTypePattern pattern) {

	}

	@Override
	public void visitCtRecord(CtRecord recordType) {

	}

	@Override
	public void visitCtRecordComponent(CtRecordComponent recordComponent) {

	}

	@Override
	public void visitCtCasePattern(CtCasePattern casePattern) {

	}
	@Override
	public void visitCtReceiverParameter(CtReceiverParameter receiverParameter) {

	}

	@Override
	public void visitCtRecordPattern(CtRecordPattern recordPattern) {

	}

	@Override
	public void visitCtUnnamedPattern(CtUnnamedPattern unnamedPattern) {

	}
}
