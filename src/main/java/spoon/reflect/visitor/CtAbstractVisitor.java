/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
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
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
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

import java.lang.annotation.Annotation;

/** Provides an empty implementation of CtVIsitor.
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
}
