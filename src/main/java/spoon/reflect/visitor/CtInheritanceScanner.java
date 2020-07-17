/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtAbstractSwitch;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
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
import spoon.reflect.code.CtRHSReceiver;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
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
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCodeSnippet;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleDirective;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtMultiTypedElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtActualTypeContainer;
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
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * This class provides an abstract implementation of the visitor that allows its
 * subclasses to scans the metamodel elements by recursively using their
 * (abstract) supertype scanning methods. It declares a scan method for each
 * abstract element of the AST and a visit method for each element of the AST.
 */
public abstract class CtInheritanceScanner implements CtVisitor {

	/**
	 * Default constructor.
	 */
	public CtInheritanceScanner() {
	}

	public <T> void visitCtCodeSnippetExpression(
			CtCodeSnippetExpression<T> e) {
		scanCtCodeSnippet(e);
		scanCtExpression(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement e) {
		scanCtCodeSnippet(e);
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	/**
	 * Generically scans a collection of meta-model elements.
	 */
	public void scan(Collection<? extends CtElement> elements) {
		if (elements != null) {
			for (CtElement e : elements) {
				scan(e);
			}
		}
	}

	/**
	 * Generically scans a meta-model element.
	 */
	public void scan(CtElement element) {
		if (element != null) {
			element.accept(this);
		}
	}

	/**
	 * Scans an abstract invocation.
	 */
	public <T> void scanCtAbstractInvocation(CtAbstractInvocation<T> a) {
	}

	/**
	 * Scans an abstract switch (either switch statement or switch expression).
	 */
	public <S> void scanCtAbstractSwitch(CtAbstractSwitch<S> a) {
	}

	/**
	 * Scans an abstract control flow break.
	 */
	public void scanCtCFlowBreak(CtCFlowBreak flowBreak) {
	}

	/**
	 * Scans a labelled control flow break.
	 */
	public void scanCtLabelledFlowBreak(CtLabelledFlowBreak labelledFlowBreak) {
	}

	/**
	 * Scans an abstract code element.
	 */
	public void scanCtCodeElement(CtCodeElement e) {

	}

	public void scanCtTypeMember(CtTypeMember e) {
	}

	public void scanCtModuleDirective(CtModuleDirective e) {

	}

	/**
	 * Scans an abstract element.
	 */
	public void scanCtElement(CtElement e) {
	}

	/**
	 * Scans an abstract executable.
	 */
	public <R> void scanCtExecutable(CtExecutable<R> e) {
	}

	/**
	 * Scans an abstract expression.
	 */
	public <T> void scanCtExpression(CtExpression<T> expression) {
	}

	/**
	 * Scans a formal type declarer.
	 */
	public void scanCtFormalTypeDeclarer(CtFormalTypeDeclarer e) {

	}

	public void scanCtVisitable(CtVisitable e) {

	}

	/**
	 * Scans an actual type container..
	 */
	public void scanCtActualTypeContainer(CtActualTypeContainer reference) {
	}

	/**
	 * Scans an abstract loop.
	 */
	public void scanCtLoop(CtLoop loop) {

	}

	/**
	 * Scans an abstract modifiable element.
	 */
	public void scanCtModifiable(CtModifiable m) {

	}

	/**
	 * Scans an abstract named element.
	 */
	public void scanCtNamedElement(CtNamedElement e) {
	}

	/**
	 * Scans an abstract reference.
	 */
	public void scanCtReference(CtReference reference) {

	}

	/**
	 * Scans an abstract statement.
	 */
	public void scanCtStatement(CtStatement s) {
	}

	/**
	 * Scans an abstract targeted expression.
	 */
	public <T, E extends CtExpression<?>> void scanCtTargetedExpression(
			CtTargetedExpression<T, E> targetedExpression) {
	}

	/**
	 * Scans an abstract type.
	 */
	public <T> void scanCtType(CtType<T> type) {
	}

	/**
	 * Scans an abstract typed element.
	 */
	public <T> void scanCtTypedElement(CtTypedElement<T> e) {
	}

	/**
	 * Scans an abstract variable declaration.
	 */
	public <T> void scanCtVariable(CtVariable<T> v) {
	}


	/**
	 * Scans an array access (read and write).
	 */
	public <T, E extends CtExpression<?>> void scanCtArrayAccess(CtArrayAccess<T, E> arrayAccess) {
	}

	/**
	 * Scans a field access (read and write).
	 */
	public <T> void scanCtFieldAccess(CtFieldAccess<T> fieldAccess) {
	}

	/**
	 * Scans a variable access (read and write).
	 */
	public <T> void scanCtVariableAccess(CtVariableAccess<T> variableAccess) {
	}

	/**
	 * Scans the right-hand side of an assignment
	 */
	public <T> void scanCtRHSReceiver(CtRHSReceiver<T> ctRHSReceiver) {
	}

	/**
	 * Scans a shadowable element
	 */
	public void scanCtShadowable(CtShadowable ctShadowable) {
	}

	/**
	 * Scans a body holder
	 */
	public void scanCtBodyHolder(CtBodyHolder ctBodyHolder) {
	}

	@Override
	public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
		visitCtVariableRead(fieldRead);
		scanCtFieldAccess(fieldRead);
		scanCtTargetedExpression(fieldRead);
	}

	@Override
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
		visitCtVariableWrite(fieldWrite);
		scanCtFieldAccess(fieldWrite);
		scanCtTargetedExpression(fieldWrite);
	}

	public <T> void visitCtSuperAccess(CtSuperAccess<T> f) {
		visitCtVariableRead(f);
		scanCtTargetedExpression(f);
	}

	public void scanCtMultiTypedElement(CtMultiTypedElement f) {
	}

	public <T, A extends T> void visitCtOperatorAssignment(
			CtOperatorAssignment<T, A> e) {
		visitCtAssignment(e);
	}

	/**
	 * Scans an abstract variable reference.
	 */
	public <T> void scanCtVariableReference(CtVariableReference<T> reference) {
	}

	/**
	 * Scans an abstract variable reference.
	 */
	public <T> void scanCtTypeInformation(CtTypeInformation typeInfo) {
	}

	public <A extends Annotation> void visitCtAnnotation(
			CtAnnotation<A> e) {
		scanCtExpression(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtShadowable(e);
	}

	public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> e) {
		scanCtType(e);
		scanCtNamedElement(e);
		scanCtTypeInformation(e);
		scanCtTypeMember(e);
		scanCtFormalTypeDeclarer(e);
		scanCtModifiable(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtShadowable(e);
	}

	public void visitCtAnonymousExecutable(CtAnonymousExecutable e) {
		scanCtExecutable(e);
		scanCtNamedElement(e);
		scanCtTypedElement(e);
		scanCtTypeMember(e);
		scanCtModifiable(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtBodyHolder(e);
	}

	@Override
	public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {
		scanCtArrayAccess(arrayRead);
		scanCtTargetedExpression(arrayRead);
		scanCtExpression(arrayRead);
		scanCtCodeElement(arrayRead);
		scanCtTypedElement(arrayRead);
		scanCtElement(arrayRead);
		scanCtVisitable(arrayRead);
	}

	@Override
	public <T> void visitCtArrayWrite(CtArrayWrite<T> arrayWrite) {
		scanCtArrayAccess(arrayWrite);
		scanCtTargetedExpression(arrayWrite);
		scanCtExpression(arrayWrite);
		scanCtCodeElement(arrayWrite);
		scanCtTypedElement(arrayWrite);
		scanCtElement(arrayWrite);
		scanCtVisitable(arrayWrite);
	}

	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> e) {
		visitCtTypeReference(e);
	}

	public <T> void visitCtAssert(CtAssert<T> e) {
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <T, A extends T> void visitCtAssignment(
			CtAssignment<T, A> e) {
		scanCtStatement(e);
		scanCtExpression(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtRHSReceiver(e);
	}

	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> e) {
		scanCtExpression(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <R> void visitCtBlock(CtBlock<R> e) {
		scanCtStatement(e);
		visitCtStatementList(e);
	}

	public void visitCtBreak(CtBreak e) {
		scanCtLabelledFlowBreak(e);
		scanCtCFlowBreak(e);
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <E> void visitCtCase(CtCase<E> e) {
		scanCtStatement(e);
		visitCtStatementList(e);
	}

	public void visitCtCatch(CtCatch e) {
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtBodyHolder(e);
	}

	public <T> void visitCtClass(CtClass<T> e) {
		scanCtType(e);
		scanCtStatement(e);
		scanCtTypeInformation(e);
		scanCtFormalTypeDeclarer(e);
		scanCtCodeElement(e);
		scanCtNamedElement(e);
		scanCtTypeMember(e);
		scanCtElement(e);
		scanCtModifiable(e);
		scanCtVisitable(e);
		scanCtShadowable(e);
	}

	@Override
	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		scanCtType(typeParameter);
		scanCtTypeInformation(typeParameter);
		scanCtFormalTypeDeclarer(typeParameter);
		scanCtNamedElement(typeParameter);
		scanCtTypeMember(typeParameter);
		scanCtElement(typeParameter);
		scanCtModifiable(typeParameter);
		scanCtVisitable(typeParameter);
		scanCtShadowable(typeParameter);
	}

	public <T> void visitCtConditional(CtConditional<T> e) {
		scanCtExpression(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <T> void visitCtConstructor(CtConstructor<T> e) {
		scanCtExecutable(e);
		scanCtNamedElement(e);
		scanCtFormalTypeDeclarer(e);
		scanCtTypedElement(e);
		scanCtTypeMember(e);
		scanCtModifiable(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtShadowable(e);
		scanCtBodyHolder(e);
	}

	public void visitCtContinue(CtContinue e) {
		scanCtLabelledFlowBreak(e);
		scanCtCFlowBreak(e);
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public void visitCtDo(CtDo e) {
		scanCtLoop(e);
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtBodyHolder(e);
	}

	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> e) {
		visitCtClass(e);
	}

	public <T> void visitCtExecutableReference(CtExecutableReference<T> e) {
		scanCtReference(e);
		scanCtElement(e);
		scanCtActualTypeContainer(e);
		scanCtVisitable(e);
	}

	public <T> void visitCtField(CtField<T> e) {
		scanCtNamedElement(e);
		scanCtVariable(e);
		scanCtTypeMember(e);
		scanCtModifiable(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtRHSReceiver(e);
		scanCtShadowable(e);
	}

	@Override
	public <T> void visitCtEnumValue(CtEnumValue<T> enumValue) {
		visitCtField(enumValue);
	}

	public <T> void visitCtThisAccess(CtThisAccess<T> e) {
		scanCtTargetedExpression(e);
		scanCtExpression(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <T> void visitCtFieldReference(CtFieldReference<T> e) {
		scanCtVariableReference(e);
		scanCtReference(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public void visitCtFor(CtFor e) {
		scanCtLoop(e);
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtBodyHolder(e);
	}

	public void visitCtForEach(CtForEach e) {
		scanCtLoop(e);
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtBodyHolder(e);
	}

	public void visitCtIf(CtIf e) {
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <T> void visitCtInterface(CtInterface<T> e) {
		scanCtType(e);
		scanCtTypeInformation(e);
		scanCtFormalTypeDeclarer(e);
		scanCtNamedElement(e);
		scanCtTypeMember(e);
		scanCtElement(e);
		scanCtModifiable(e);
		scanCtVisitable(e);
		scanCtShadowable(e);
	}

	public <T> void visitCtInvocation(CtInvocation<T> e) {
		scanCtAbstractInvocation(e);
		scanCtStatement(e);
		scanCtActualTypeContainer(e);
		scanCtTargetedExpression(e);
		scanCtElement(e);
		scanCtCodeElement(e);
		scanCtExpression(e);
		scanCtVisitable(e);
		scanCtTypedElement(e);
	}

	public <T> void visitCtLiteral(CtLiteral<T> e) {
		scanCtExpression(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <T> void visitCtLocalVariable(CtLocalVariable<T> e) {
		scanCtStatement(e);
		scanCtVariable(e);
		scanCtCodeElement(e);
		scanCtNamedElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtModifiable(e);
		scanCtVisitable(e);
		scanCtRHSReceiver(e);
	}

	public <T> void visitCtLocalVariableReference(
			CtLocalVariableReference<T> e) {
		scanCtVariableReference(e);
		scanCtReference(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <T> void visitCtCatchVariable(CtCatchVariable<T> e) {
		scanCtVariable(e);
		scanCtMultiTypedElement(e);
		scanCtCodeElement(e);
		scanCtNamedElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtModifiable(e);
		scanCtVisitable(e);
	}

	public <T> void visitCtCatchVariableReference(CtCatchVariableReference<T> e) {
		scanCtVariableReference(e);
		scanCtReference(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <T> void visitCtMethod(CtMethod<T> e) {
		scanCtExecutable(e);
		scanCtTypedElement(e);
		scanCtNamedElement(e);
		scanCtFormalTypeDeclarer(e);
		scanCtTypeMember(e);
		scanCtElement(e);
		scanCtModifiable(e);
		scanCtVisitable(e);
		scanCtShadowable(e);
		scanCtBodyHolder(e);
	}

	@Override
	public <T> void visitCtAnnotationMethod(CtAnnotationMethod<T> annotationMethod) {
		visitCtMethod(annotationMethod);
	}

	public <T> void visitCtNewArray(CtNewArray<T> e) {
		scanCtExpression(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	@Override
	public <T> void visitCtConstructorCall(CtConstructorCall<T> e) {
		scanCtTargetedExpression(e);
		scanCtAbstractInvocation(e);
		scanCtStatement(e);
		scanCtActualTypeContainer(e);
		scanCtExpression(e);
		scanCtElement(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtVisitable(e);
	}

	public <T> void visitCtNewClass(CtNewClass<T> e) {
		visitCtConstructorCall(e);
	}

	@Override
	public <T> void visitCtLambda(CtLambda<T> e) {
		scanCtExpression(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtExecutable(e);
		scanCtNamedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtBodyHolder(e);
	}

	@Override
	public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(
			CtExecutableReferenceExpression<T, E> e) {
		scanCtTargetedExpression(e);
		scanCtExpression(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <T, A extends T> void visitCtOperatorAssignement(
			CtOperatorAssignment<T, A> assignment) {
	}

	public void visitCtPackage(CtPackage e) {
		scanCtNamedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtShadowable(e);
	}

	public void visitCtPackageReference(CtPackageReference e) {
		scanCtReference(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <T> void visitCtParameter(CtParameter<T> e) {
		scanCtNamedElement(e);
		scanCtVariable(e);
		scanCtModifiable(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtShadowable(e);
	}

	public <T> void visitCtParameterReference(CtParameterReference<T> e) {
		scanCtVariableReference(e);
		scanCtReference(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <R> void visitCtReturn(CtReturn<R> e) {
		scanCtCFlowBreak(e);
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <R> void visitCtStatementList(CtStatementList e) {
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <E> void visitCtSwitch(CtSwitch<E> e) {
		scanCtAbstractSwitch(e);
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <T, S> void visitCtSwitchExpression(CtSwitchExpression<T, S> e) {
		scanCtAbstractSwitch(e);
		scanCtExpression(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public void visitCtSynchronized(CtSynchronized e) {
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public void visitCtThrow(CtThrow e) {
		scanCtCFlowBreak(e);
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public void visitCtTry(CtTry e) {
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtBodyHolder(e);
	}

	@Override
	public void visitCtTryWithResource(CtTryWithResource e) {
		visitCtTry(e);
	}

	public void visitCtTypeParameterReference(CtTypeParameterReference e) {
		visitCtTypeReference(e);
	}

	@Override
	public void visitCtWildcardReference(CtWildcardReference wildcardReference) {
		visitCtTypeParameterReference(wildcardReference);
	}

	@Override
	public <T> void visitCtIntersectionTypeReference(CtIntersectionTypeReference<T> e) {
		visitCtTypeReference(e);
	}

	public <T> void visitCtTypeReference(CtTypeReference<T> e) {
		scanCtReference(e);
		scanCtTypeInformation(e);
		scanCtActualTypeContainer(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtShadowable(e);
	}

	@Override
	public <T> void visitCtTypeAccess(CtTypeAccess<T> e) {
		scanCtExpression(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> e) {
		scanCtExpression(e);
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	@Override
	public <T> void visitCtVariableRead(CtVariableRead<T> e) {
		scanCtVariableAccess(e);
		scanCtExpression(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	@Override
	public <T> void visitCtVariableWrite(CtVariableWrite<T> e) {
		scanCtVariableAccess(e);
		scanCtExpression(e);
		scanCtCodeElement(e);
		scanCtTypedElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

	@Override
	public void visitCtComment(CtComment e) {
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtStatement(e);
		scanCtCodeElement(e);
	}

	@Override
	public void visitCtJavaDoc(CtJavaDoc e) {
		visitCtComment(e);
	}

	@Override
	public void visitCtJavaDocTag(CtJavaDocTag e) {
		scanCtElement(e);
		scanCtVisitable(e);
	}

	public <T> void visitCtAnnotationFieldAccess(
			CtAnnotationFieldAccess<T> e) {
		visitCtVariableRead(e);
		scanCtTargetedExpression(e);
	}

	public void visitCtWhile(CtWhile e) {
		scanCtLoop(e);
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
		scanCtBodyHolder(e);
	}

	public <T> void visitCtUnboundVariableReference(CtUnboundVariableReference<T> reference) {
		scanCtVariableReference(reference);
		scanCtReference(reference);
		scanCtElement(reference);
		scanCtVisitable(reference);
	}

	public void scanCtCodeSnippet(CtCodeSnippet snippet) {
	}

	@Override
	public void visitCtImport(CtImport ctImport) {
		scanCtElement(ctImport);
		scanCtVisitable(ctImport);
	}

	@Override
	public void visitCtModule(CtModule module) {
		scanCtNamedElement(module);
		scanCtVisitable(module);
		scanCtElement(module);
	}

	@Override
	public void visitCtModuleReference(CtModuleReference moduleReference) {
		scanCtReference(moduleReference);
		scanCtElement(moduleReference);
		scanCtVisitable(moduleReference);
	}

	@Override
	public void visitCtPackageExport(CtPackageExport moduleExport) {
		scanCtElement(moduleExport);
		scanCtVisitable(moduleExport);
		scanCtModuleDirective(moduleExport);
	}

	@Override
	public void visitCtModuleRequirement(CtModuleRequirement moduleRequirement) {
		scanCtElement(moduleRequirement);
		scanCtVisitable(moduleRequirement);
		scanCtModuleDirective(moduleRequirement);
	}

	@Override
	public void visitCtProvidedService(CtProvidedService moduleProvidedService) {
		scanCtElement(moduleProvidedService);
		scanCtVisitable(moduleProvidedService);
		scanCtModuleDirective(moduleProvidedService);
	}

	@Override
	public void visitCtUsedService(CtUsedService usedService) {
		scanCtElement(usedService);
		scanCtVisitable(usedService);
		scanCtModuleDirective(usedService);
	}

	@Override
	public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {
		scanCtElement(compilationUnit);
		scanCtVisitable(compilationUnit);
	}

	@Override
	public void visitCtPackageDeclaration(CtPackageDeclaration packageDeclaration) {
		scanCtElement(packageDeclaration);
		scanCtVisitable(packageDeclaration);
	}

	@Override
	public void visitCtTypeMemberWildcardImportReference(CtTypeMemberWildcardImportReference wildcardReference) {
		scanCtReference(wildcardReference);
		scanCtElement(wildcardReference);
		scanCtVisitable(wildcardReference);
	}
	public void visitCtYieldStatement(CtYieldStatement e) {
		scanCtCFlowBreak(e);
		scanCtStatement(e);
		scanCtCodeElement(e);
		scanCtElement(e);
		scanCtVisitable(e);
	}

}
