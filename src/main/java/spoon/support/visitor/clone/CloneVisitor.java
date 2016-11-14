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


package spoon.support.visitor.clone;


/**
 * Used to clone a given element.
 *
 * This class is generated automatically by the processor {@link spoon.generating.CloneVisitorGenerator}.
 */
public class CloneVisitor extends spoon.reflect.visitor.CtScanner {
	private final spoon.support.visitor.clone.CloneBuilder builder = new spoon.support.visitor.clone.CloneBuilder();

	private spoon.reflect.declaration.CtElement other;

	public <T extends spoon.reflect.declaration.CtElement> T getClone() {
		return ((T) (other));
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <A extends java.lang.annotation.Annotation> void visitCtAnnotation(final spoon.reflect.declaration.CtAnnotation<A> annotation) {
		spoon.reflect.declaration.CtAnnotation<A> aCtAnnotation = spoon.support.visitor.clone.CloneBuilder.build(this.builder, annotation, annotation.getFactory().Core().createAnnotation());
		aCtAnnotation.setComments(spoon.support.visitor.equals.CloneHelper.clone(annotation.getComments()));
		aCtAnnotation.setAnnotationType(spoon.support.visitor.equals.CloneHelper.clone(annotation.getAnnotationType()));
		aCtAnnotation.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(annotation.getAnnotations()));
		aCtAnnotation.setValues(spoon.support.visitor.equals.CloneHelper.clone(annotation.getValues()));
		this.other = aCtAnnotation;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <A extends java.lang.annotation.Annotation> void visitCtAnnotationType(final spoon.reflect.declaration.CtAnnotationType<A> annotationType) {
		spoon.reflect.declaration.CtAnnotationType<A> aCtAnnotationType = spoon.support.visitor.clone.CloneBuilder.build(this.builder, annotationType, annotationType.getFactory().Core().createAnnotationType());
		aCtAnnotationType.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(annotationType.getAnnotations()));
		aCtAnnotationType.setTypeMembers(spoon.support.visitor.equals.CloneHelper.clone(annotationType.getTypeMembers()));
		aCtAnnotationType.setComments(spoon.support.visitor.equals.CloneHelper.clone(annotationType.getComments()));
		this.other = aCtAnnotationType;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtAnonymousExecutable(final spoon.reflect.declaration.CtAnonymousExecutable anonymousExec) {
		spoon.reflect.declaration.CtAnonymousExecutable aCtAnonymousExecutable = spoon.support.visitor.clone.CloneBuilder.build(this.builder, anonymousExec, anonymousExec.getFactory().Core().createAnonymousExecutable());
		aCtAnonymousExecutable.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(anonymousExec.getAnnotations()));
		aCtAnonymousExecutable.setBody(spoon.support.visitor.equals.CloneHelper.clone(anonymousExec.getBody()));
		aCtAnonymousExecutable.setComments(spoon.support.visitor.equals.CloneHelper.clone(anonymousExec.getComments()));
		this.other = aCtAnonymousExecutable;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtArrayRead(final spoon.reflect.code.CtArrayRead<T> arrayRead) {
		spoon.reflect.code.CtArrayRead<T> aCtArrayRead = spoon.support.visitor.clone.CloneBuilder.build(this.builder, arrayRead, arrayRead.getFactory().Core().createArrayRead());
		aCtArrayRead.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(arrayRead.getAnnotations()));
		aCtArrayRead.setType(spoon.support.visitor.equals.CloneHelper.clone(arrayRead.getType()));
		aCtArrayRead.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(arrayRead.getTypeCasts()));
		aCtArrayRead.setTarget(spoon.support.visitor.equals.CloneHelper.clone(arrayRead.getTarget()));
		aCtArrayRead.setIndexExpression(spoon.support.visitor.equals.CloneHelper.clone(arrayRead.getIndexExpression()));
		aCtArrayRead.setComments(spoon.support.visitor.equals.CloneHelper.clone(arrayRead.getComments()));
		this.other = aCtArrayRead;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtArrayWrite(final spoon.reflect.code.CtArrayWrite<T> arrayWrite) {
		spoon.reflect.code.CtArrayWrite<T> aCtArrayWrite = spoon.support.visitor.clone.CloneBuilder.build(this.builder, arrayWrite, arrayWrite.getFactory().Core().createArrayWrite());
		aCtArrayWrite.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(arrayWrite.getAnnotations()));
		aCtArrayWrite.setType(spoon.support.visitor.equals.CloneHelper.clone(arrayWrite.getType()));
		aCtArrayWrite.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(arrayWrite.getTypeCasts()));
		aCtArrayWrite.setTarget(spoon.support.visitor.equals.CloneHelper.clone(arrayWrite.getTarget()));
		aCtArrayWrite.setIndexExpression(spoon.support.visitor.equals.CloneHelper.clone(arrayWrite.getIndexExpression()));
		aCtArrayWrite.setComments(spoon.support.visitor.equals.CloneHelper.clone(arrayWrite.getComments()));
		this.other = aCtArrayWrite;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtArrayTypeReference(final spoon.reflect.reference.CtArrayTypeReference<T> reference) {
		spoon.reflect.reference.CtArrayTypeReference<T> aCtArrayTypeReference = spoon.support.visitor.clone.CloneBuilder.build(this.builder, reference, reference.getFactory().Core().createArrayTypeReference());
		aCtArrayTypeReference.setComments(spoon.support.visitor.equals.CloneHelper.clone(reference.getComments()));
		aCtArrayTypeReference.setDeclaringType(spoon.support.visitor.equals.CloneHelper.clone(reference.getDeclaringType()));
		aCtArrayTypeReference.setComponentType(spoon.support.visitor.equals.CloneHelper.clone(reference.getComponentType()));
		aCtArrayTypeReference.setActualTypeArguments(spoon.support.visitor.equals.CloneHelper.clone(reference.getActualTypeArguments()));
		aCtArrayTypeReference.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(reference.getAnnotations()));
		this.other = aCtArrayTypeReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtAssert(final spoon.reflect.code.CtAssert<T> asserted) {
		spoon.reflect.code.CtAssert<T> aCtAssert = spoon.support.visitor.clone.CloneBuilder.build(this.builder, asserted, asserted.getFactory().Core().createAssert());
		aCtAssert.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(asserted.getAnnotations()));
		aCtAssert.setAssertExpression(spoon.support.visitor.equals.CloneHelper.clone(asserted.getAssertExpression()));
		aCtAssert.setExpression(spoon.support.visitor.equals.CloneHelper.clone(asserted.getExpression()));
		aCtAssert.setComments(spoon.support.visitor.equals.CloneHelper.clone(asserted.getComments()));
		this.other = aCtAssert;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T, A extends T> void visitCtAssignment(final spoon.reflect.code.CtAssignment<T, A> assignement) {
		spoon.reflect.code.CtAssignment<T, A> aCtAssignment = spoon.support.visitor.clone.CloneBuilder.build(this.builder, assignement, assignement.getFactory().Core().createAssignment());
		aCtAssignment.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(assignement.getAnnotations()));
		aCtAssignment.setType(spoon.support.visitor.equals.CloneHelper.clone(assignement.getType()));
		aCtAssignment.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(assignement.getTypeCasts()));
		aCtAssignment.setAssigned(spoon.support.visitor.equals.CloneHelper.clone(assignement.getAssigned()));
		aCtAssignment.setAssignment(spoon.support.visitor.equals.CloneHelper.clone(assignement.getAssignment()));
		aCtAssignment.setComments(spoon.support.visitor.equals.CloneHelper.clone(assignement.getComments()));
		this.other = aCtAssignment;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtBinaryOperator(final spoon.reflect.code.CtBinaryOperator<T> operator) {
		spoon.reflect.code.CtBinaryOperator<T> aCtBinaryOperator = spoon.support.visitor.clone.CloneBuilder.build(this.builder, operator, operator.getFactory().Core().createBinaryOperator());
		aCtBinaryOperator.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(operator.getAnnotations()));
		aCtBinaryOperator.setType(spoon.support.visitor.equals.CloneHelper.clone(operator.getType()));
		aCtBinaryOperator.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(operator.getTypeCasts()));
		aCtBinaryOperator.setLeftHandOperand(spoon.support.visitor.equals.CloneHelper.clone(operator.getLeftHandOperand()));
		aCtBinaryOperator.setRightHandOperand(spoon.support.visitor.equals.CloneHelper.clone(operator.getRightHandOperand()));
		aCtBinaryOperator.setComments(spoon.support.visitor.equals.CloneHelper.clone(operator.getComments()));
		this.other = aCtBinaryOperator;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <R> void visitCtBlock(final spoon.reflect.code.CtBlock<R> block) {
		spoon.reflect.code.CtBlock<R> aCtBlock = spoon.support.visitor.clone.CloneBuilder.build(this.builder, block, block.getFactory().Core().createBlock());
		aCtBlock.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(block.getAnnotations()));
		aCtBlock.setStatements(spoon.support.visitor.equals.CloneHelper.clone(block.getStatements()));
		aCtBlock.setComments(spoon.support.visitor.equals.CloneHelper.clone(block.getComments()));
		this.other = aCtBlock;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtBreak(final spoon.reflect.code.CtBreak breakStatement) {
		spoon.reflect.code.CtBreak aCtBreak = spoon.support.visitor.clone.CloneBuilder.build(this.builder, breakStatement, breakStatement.getFactory().Core().createBreak());
		aCtBreak.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(breakStatement.getAnnotations()));
		aCtBreak.setComments(spoon.support.visitor.equals.CloneHelper.clone(breakStatement.getComments()));
		this.other = aCtBreak;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <S> void visitCtCase(final spoon.reflect.code.CtCase<S> caseStatement) {
		spoon.reflect.code.CtCase<S> aCtCase = spoon.support.visitor.clone.CloneBuilder.build(this.builder, caseStatement, caseStatement.getFactory().Core().createCase());
		aCtCase.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(caseStatement.getAnnotations()));
		aCtCase.setCaseExpression(spoon.support.visitor.equals.CloneHelper.clone(caseStatement.getCaseExpression()));
		aCtCase.setStatements(spoon.support.visitor.equals.CloneHelper.clone(caseStatement.getStatements()));
		aCtCase.setComments(spoon.support.visitor.equals.CloneHelper.clone(caseStatement.getComments()));
		this.other = aCtCase;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtCatch(final spoon.reflect.code.CtCatch catchBlock) {
		spoon.reflect.code.CtCatch aCtCatch = spoon.support.visitor.clone.CloneBuilder.build(this.builder, catchBlock, catchBlock.getFactory().Core().createCatch());
		aCtCatch.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(catchBlock.getAnnotations()));
		aCtCatch.setParameter(spoon.support.visitor.equals.CloneHelper.clone(catchBlock.getParameter()));
		aCtCatch.setBody(spoon.support.visitor.equals.CloneHelper.clone(catchBlock.getBody()));
		aCtCatch.setComments(spoon.support.visitor.equals.CloneHelper.clone(catchBlock.getComments()));
		this.other = aCtCatch;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtClass(final spoon.reflect.declaration.CtClass<T> ctClass) {
		spoon.reflect.declaration.CtClass<T> aCtClass = spoon.support.visitor.clone.CloneBuilder.build(this.builder, ctClass, ctClass.getFactory().Core().createClass());
		aCtClass.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(ctClass.getAnnotations()));
		aCtClass.setSuperclass(spoon.support.visitor.equals.CloneHelper.clone(ctClass.getSuperclass()));
		aCtClass.setSuperInterfaces(spoon.support.visitor.equals.CloneHelper.clone(ctClass.getSuperInterfaces()));
		aCtClass.setFormalCtTypeParameters(spoon.support.visitor.equals.CloneHelper.clone(ctClass.getFormalCtTypeParameters()));
		aCtClass.setTypeMembers(spoon.support.visitor.equals.CloneHelper.clone(ctClass.getTypeMembers()));
		aCtClass.setComments(spoon.support.visitor.equals.CloneHelper.clone(ctClass.getComments()));
		this.other = aCtClass;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtTypeParameter(spoon.reflect.declaration.CtTypeParameter typeParameter) {
		spoon.reflect.declaration.CtTypeParameter aCtTypeParameter = spoon.support.visitor.clone.CloneBuilder.build(this.builder, typeParameter, typeParameter.getFactory().Core().createTypeParameter());
		aCtTypeParameter.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(typeParameter.getAnnotations()));
		aCtTypeParameter.setSuperclass(spoon.support.visitor.equals.CloneHelper.clone(typeParameter.getSuperclass()));
		aCtTypeParameter.setComments(spoon.support.visitor.equals.CloneHelper.clone(typeParameter.getComments()));
		this.other = aCtTypeParameter;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtConditional(final spoon.reflect.code.CtConditional<T> conditional) {
		spoon.reflect.code.CtConditional<T> aCtConditional = spoon.support.visitor.clone.CloneBuilder.build(this.builder, conditional, conditional.getFactory().Core().createConditional());
		aCtConditional.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(conditional.getAnnotations()));
		aCtConditional.setCondition(spoon.support.visitor.equals.CloneHelper.clone(conditional.getCondition()));
		aCtConditional.setThenExpression(spoon.support.visitor.equals.CloneHelper.clone(conditional.getThenExpression()));
		aCtConditional.setElseExpression(spoon.support.visitor.equals.CloneHelper.clone(conditional.getElseExpression()));
		aCtConditional.setComments(spoon.support.visitor.equals.CloneHelper.clone(conditional.getComments()));
		aCtConditional.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(conditional.getTypeCasts()));
		this.other = aCtConditional;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtConstructor(final spoon.reflect.declaration.CtConstructor<T> c) {
		spoon.reflect.declaration.CtConstructor<T> aCtConstructor = spoon.support.visitor.clone.CloneBuilder.build(this.builder, c, c.getFactory().Core().createConstructor());
		aCtConstructor.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(c.getAnnotations()));
		aCtConstructor.setParameters(spoon.support.visitor.equals.CloneHelper.clone(c.getParameters()));
		aCtConstructor.setThrownTypes(spoon.support.visitor.equals.CloneHelper.clone(c.getThrownTypes()));
		aCtConstructor.setFormalCtTypeParameters(spoon.support.visitor.equals.CloneHelper.clone(c.getFormalCtTypeParameters()));
		aCtConstructor.setBody(spoon.support.visitor.equals.CloneHelper.clone(c.getBody()));
		aCtConstructor.setComments(spoon.support.visitor.equals.CloneHelper.clone(c.getComments()));
		this.other = aCtConstructor;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtContinue(final spoon.reflect.code.CtContinue continueStatement) {
		spoon.reflect.code.CtContinue aCtContinue = spoon.support.visitor.clone.CloneBuilder.build(this.builder, continueStatement, continueStatement.getFactory().Core().createContinue());
		aCtContinue.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(continueStatement.getAnnotations()));
		aCtContinue.setLabelledStatement(spoon.support.visitor.equals.CloneHelper.clone(continueStatement.getLabelledStatement()));
		aCtContinue.setComments(spoon.support.visitor.equals.CloneHelper.clone(continueStatement.getComments()));
		this.other = aCtContinue;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtDo(final spoon.reflect.code.CtDo doLoop) {
		spoon.reflect.code.CtDo aCtDo = spoon.support.visitor.clone.CloneBuilder.build(this.builder, doLoop, doLoop.getFactory().Core().createDo());
		aCtDo.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(doLoop.getAnnotations()));
		aCtDo.setLoopingExpression(spoon.support.visitor.equals.CloneHelper.clone(doLoop.getLoopingExpression()));
		aCtDo.setBody(spoon.support.visitor.equals.CloneHelper.clone(doLoop.getBody()));
		aCtDo.setComments(spoon.support.visitor.equals.CloneHelper.clone(doLoop.getComments()));
		this.other = aCtDo;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T extends java.lang.Enum<?>> void visitCtEnum(final spoon.reflect.declaration.CtEnum<T> ctEnum) {
		spoon.reflect.declaration.CtEnum<T> aCtEnum = spoon.support.visitor.clone.CloneBuilder.build(this.builder, ctEnum, ctEnum.getFactory().Core().createEnum());
		aCtEnum.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(ctEnum.getAnnotations()));
		aCtEnum.setSuperInterfaces(spoon.support.visitor.equals.CloneHelper.clone(ctEnum.getSuperInterfaces()));
		aCtEnum.setTypeMembers(spoon.support.visitor.equals.CloneHelper.clone(ctEnum.getTypeMembers()));
		aCtEnum.setEnumValues(spoon.support.visitor.equals.CloneHelper.clone(ctEnum.getEnumValues()));
		aCtEnum.setComments(spoon.support.visitor.equals.CloneHelper.clone(ctEnum.getComments()));
		this.other = aCtEnum;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtExecutableReference(final spoon.reflect.reference.CtExecutableReference<T> reference) {
		spoon.reflect.reference.CtExecutableReference<T> aCtExecutableReference = spoon.support.visitor.clone.CloneBuilder.build(this.builder, reference, reference.getFactory().Core().createExecutableReference());
		aCtExecutableReference.setDeclaringType(spoon.support.visitor.equals.CloneHelper.clone(reference.getDeclaringType()));
		aCtExecutableReference.setType(spoon.support.visitor.equals.CloneHelper.clone(reference.getType()));
		aCtExecutableReference.setParameters(spoon.support.visitor.equals.CloneHelper.clone(reference.getParameters()));
		aCtExecutableReference.setActualTypeArguments(spoon.support.visitor.equals.CloneHelper.clone(reference.getActualTypeArguments()));
		aCtExecutableReference.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(reference.getAnnotations()));
		aCtExecutableReference.setComments(spoon.support.visitor.equals.CloneHelper.clone(reference.getComments()));
		this.other = aCtExecutableReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtField(final spoon.reflect.declaration.CtField<T> f) {
		spoon.reflect.declaration.CtField<T> aCtField = spoon.support.visitor.clone.CloneBuilder.build(this.builder, f, f.getFactory().Core().createField());
		aCtField.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(f.getAnnotations()));
		aCtField.setType(spoon.support.visitor.equals.CloneHelper.clone(f.getType()));
		aCtField.setDefaultExpression(spoon.support.visitor.equals.CloneHelper.clone(f.getDefaultExpression()));
		aCtField.setComments(spoon.support.visitor.equals.CloneHelper.clone(f.getComments()));
		this.other = aCtField;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtEnumValue(final spoon.reflect.declaration.CtEnumValue<T> enumValue) {
		spoon.reflect.declaration.CtEnumValue<T> aCtEnumValue = spoon.support.visitor.clone.CloneBuilder.build(this.builder, enumValue, enumValue.getFactory().Core().createEnumValue());
		aCtEnumValue.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(enumValue.getAnnotations()));
		aCtEnumValue.setType(spoon.support.visitor.equals.CloneHelper.clone(enumValue.getType()));
		aCtEnumValue.setDefaultExpression(spoon.support.visitor.equals.CloneHelper.clone(enumValue.getDefaultExpression()));
		aCtEnumValue.setComments(spoon.support.visitor.equals.CloneHelper.clone(enumValue.getComments()));
		this.other = aCtEnumValue;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtThisAccess(final spoon.reflect.code.CtThisAccess<T> thisAccess) {
		spoon.reflect.code.CtThisAccess<T> aCtThisAccess = spoon.support.visitor.clone.CloneBuilder.build(this.builder, thisAccess, thisAccess.getFactory().Core().createThisAccess());
		aCtThisAccess.setComments(spoon.support.visitor.equals.CloneHelper.clone(thisAccess.getComments()));
		aCtThisAccess.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(thisAccess.getAnnotations()));
		aCtThisAccess.setType(spoon.support.visitor.equals.CloneHelper.clone(thisAccess.getType()));
		aCtThisAccess.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(thisAccess.getTypeCasts()));
		aCtThisAccess.setTarget(spoon.support.visitor.equals.CloneHelper.clone(thisAccess.getTarget()));
		this.other = aCtThisAccess;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtAnnotationFieldAccess(final spoon.reflect.code.CtAnnotationFieldAccess<T> annotationFieldAccess) {
		spoon.reflect.code.CtAnnotationFieldAccess<T> aCtAnnotationFieldAccess = spoon.support.visitor.clone.CloneBuilder.build(this.builder, annotationFieldAccess, annotationFieldAccess.getFactory().Core().createAnnotationFieldAccess());
		aCtAnnotationFieldAccess.setComments(spoon.support.visitor.equals.CloneHelper.clone(annotationFieldAccess.getComments()));
		aCtAnnotationFieldAccess.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(annotationFieldAccess.getAnnotations()));
		aCtAnnotationFieldAccess.setType(spoon.support.visitor.equals.CloneHelper.clone(annotationFieldAccess.getType()));
		aCtAnnotationFieldAccess.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(annotationFieldAccess.getTypeCasts()));
		aCtAnnotationFieldAccess.setTarget(spoon.support.visitor.equals.CloneHelper.clone(annotationFieldAccess.getTarget()));
		aCtAnnotationFieldAccess.setVariable(spoon.support.visitor.equals.CloneHelper.clone(annotationFieldAccess.getVariable()));
		aCtAnnotationFieldAccess.setComments(spoon.support.visitor.equals.CloneHelper.clone(annotationFieldAccess.getComments()));
		this.other = aCtAnnotationFieldAccess;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtFieldReference(final spoon.reflect.reference.CtFieldReference<T> reference) {
		spoon.reflect.reference.CtFieldReference<T> aCtFieldReference = spoon.support.visitor.clone.CloneBuilder.build(this.builder, reference, reference.getFactory().Core().createFieldReference());
		aCtFieldReference.setDeclaringType(spoon.support.visitor.equals.CloneHelper.clone(reference.getDeclaringType()));
		aCtFieldReference.setType(spoon.support.visitor.equals.CloneHelper.clone(reference.getType()));
		aCtFieldReference.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(reference.getAnnotations()));
		this.other = aCtFieldReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtFor(final spoon.reflect.code.CtFor forLoop) {
		spoon.reflect.code.CtFor aCtFor = spoon.support.visitor.clone.CloneBuilder.build(this.builder, forLoop, forLoop.getFactory().Core().createFor());
		aCtFor.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(forLoop.getAnnotations()));
		aCtFor.setForInit(spoon.support.visitor.equals.CloneHelper.clone(forLoop.getForInit()));
		aCtFor.setExpression(spoon.support.visitor.equals.CloneHelper.clone(forLoop.getExpression()));
		aCtFor.setForUpdate(spoon.support.visitor.equals.CloneHelper.clone(forLoop.getForUpdate()));
		aCtFor.setBody(spoon.support.visitor.equals.CloneHelper.clone(forLoop.getBody()));
		aCtFor.setComments(spoon.support.visitor.equals.CloneHelper.clone(forLoop.getComments()));
		this.other = aCtFor;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtForEach(final spoon.reflect.code.CtForEach foreach) {
		spoon.reflect.code.CtForEach aCtForEach = spoon.support.visitor.clone.CloneBuilder.build(this.builder, foreach, foreach.getFactory().Core().createForEach());
		aCtForEach.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(foreach.getAnnotations()));
		aCtForEach.setVariable(spoon.support.visitor.equals.CloneHelper.clone(foreach.getVariable()));
		aCtForEach.setExpression(spoon.support.visitor.equals.CloneHelper.clone(foreach.getExpression()));
		aCtForEach.setBody(spoon.support.visitor.equals.CloneHelper.clone(foreach.getBody()));
		aCtForEach.setComments(spoon.support.visitor.equals.CloneHelper.clone(foreach.getComments()));
		this.other = aCtForEach;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtIf(final spoon.reflect.code.CtIf ifElement) {
		spoon.reflect.code.CtIf aCtIf = spoon.support.visitor.clone.CloneBuilder.build(this.builder, ifElement, ifElement.getFactory().Core().createIf());
		aCtIf.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(ifElement.getAnnotations()));
		aCtIf.setCondition(spoon.support.visitor.equals.CloneHelper.clone(ifElement.getCondition()));
		aCtIf.setThenStatement(spoon.support.visitor.equals.CloneHelper.clone(((spoon.reflect.code.CtStatement) (ifElement.getThenStatement()))));
		aCtIf.setElseStatement(spoon.support.visitor.equals.CloneHelper.clone(((spoon.reflect.code.CtStatement) (ifElement.getElseStatement()))));
		aCtIf.setComments(spoon.support.visitor.equals.CloneHelper.clone(ifElement.getComments()));
		this.other = aCtIf;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtInterface(final spoon.reflect.declaration.CtInterface<T> intrface) {
		spoon.reflect.declaration.CtInterface<T> aCtInterface = spoon.support.visitor.clone.CloneBuilder.build(this.builder, intrface, intrface.getFactory().Core().createInterface());
		aCtInterface.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(intrface.getAnnotations()));
		aCtInterface.setSuperInterfaces(spoon.support.visitor.equals.CloneHelper.clone(intrface.getSuperInterfaces()));
		aCtInterface.setFormalCtTypeParameters(spoon.support.visitor.equals.CloneHelper.clone(intrface.getFormalCtTypeParameters()));
		aCtInterface.setTypeMembers(spoon.support.visitor.equals.CloneHelper.clone(intrface.getTypeMembers()));
		aCtInterface.setComments(spoon.support.visitor.equals.CloneHelper.clone(intrface.getComments()));
		this.other = aCtInterface;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtInvocation(final spoon.reflect.code.CtInvocation<T> invocation) {
		spoon.reflect.code.CtInvocation<T> aCtInvocation = spoon.support.visitor.clone.CloneBuilder.build(this.builder, invocation, invocation.getFactory().Core().createInvocation());
		aCtInvocation.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(invocation.getAnnotations()));
		aCtInvocation.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(invocation.getTypeCasts()));
		aCtInvocation.setTarget(spoon.support.visitor.equals.CloneHelper.clone(invocation.getTarget()));
		aCtInvocation.setExecutable(spoon.support.visitor.equals.CloneHelper.clone(invocation.getExecutable()));
		aCtInvocation.setArguments(spoon.support.visitor.equals.CloneHelper.clone(invocation.getArguments()));
		aCtInvocation.setComments(spoon.support.visitor.equals.CloneHelper.clone(invocation.getComments()));
		this.other = aCtInvocation;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtLiteral(final spoon.reflect.code.CtLiteral<T> literal) {
		spoon.reflect.code.CtLiteral<T> aCtLiteral = spoon.support.visitor.clone.CloneBuilder.build(this.builder, literal, literal.getFactory().Core().createLiteral());
		aCtLiteral.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(literal.getAnnotations()));
		aCtLiteral.setType(spoon.support.visitor.equals.CloneHelper.clone(literal.getType()));
		aCtLiteral.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(literal.getTypeCasts()));
		aCtLiteral.setComments(spoon.support.visitor.equals.CloneHelper.clone(literal.getComments()));
		this.other = aCtLiteral;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtLocalVariable(final spoon.reflect.code.CtLocalVariable<T> localVariable) {
		spoon.reflect.code.CtLocalVariable<T> aCtLocalVariable = spoon.support.visitor.clone.CloneBuilder.build(this.builder, localVariable, localVariable.getFactory().Core().createLocalVariable());
		aCtLocalVariable.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(localVariable.getAnnotations()));
		aCtLocalVariable.setType(spoon.support.visitor.equals.CloneHelper.clone(localVariable.getType()));
		aCtLocalVariable.setDefaultExpression(spoon.support.visitor.equals.CloneHelper.clone(localVariable.getDefaultExpression()));
		aCtLocalVariable.setComments(spoon.support.visitor.equals.CloneHelper.clone(localVariable.getComments()));
		this.other = aCtLocalVariable;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtLocalVariableReference(final spoon.reflect.reference.CtLocalVariableReference<T> reference) {
		spoon.reflect.reference.CtLocalVariableReference<T> aCtLocalVariableReference = spoon.support.visitor.clone.CloneBuilder.build(this.builder, reference, reference.getFactory().Core().createLocalVariableReference());
		aCtLocalVariableReference.setType(spoon.support.visitor.equals.CloneHelper.clone(reference.getType()));
		aCtLocalVariableReference.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(reference.getAnnotations()));
		this.other = aCtLocalVariableReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtCatchVariable(final spoon.reflect.code.CtCatchVariable<T> catchVariable) {
		spoon.reflect.code.CtCatchVariable<T> aCtCatchVariable = spoon.support.visitor.clone.CloneBuilder.build(this.builder, catchVariable, catchVariable.getFactory().Core().createCatchVariable());
		aCtCatchVariable.setComments(spoon.support.visitor.equals.CloneHelper.clone(catchVariable.getComments()));
		aCtCatchVariable.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(catchVariable.getAnnotations()));
		aCtCatchVariable.setType(spoon.support.visitor.equals.CloneHelper.clone(catchVariable.getType()));
		aCtCatchVariable.setMultiTypes(spoon.support.visitor.equals.CloneHelper.clone(catchVariable.getMultiTypes()));
		this.other = aCtCatchVariable;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtCatchVariableReference(final spoon.reflect.reference.CtCatchVariableReference<T> reference) {
		spoon.reflect.reference.CtCatchVariableReference<T> aCtCatchVariableReference = spoon.support.visitor.clone.CloneBuilder.build(this.builder, reference, reference.getFactory().Core().createCatchVariableReference());
		aCtCatchVariableReference.setComments(spoon.support.visitor.equals.CloneHelper.clone(reference.getComments()));
		aCtCatchVariableReference.setType(spoon.support.visitor.equals.CloneHelper.clone(reference.getType()));
		aCtCatchVariableReference.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(reference.getAnnotations()));
		this.other = aCtCatchVariableReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtMethod(final spoon.reflect.declaration.CtMethod<T> m) {
		spoon.reflect.declaration.CtMethod<T> aCtMethod = spoon.support.visitor.clone.CloneBuilder.build(this.builder, m, m.getFactory().Core().createMethod());
		aCtMethod.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(m.getAnnotations()));
		aCtMethod.setFormalCtTypeParameters(spoon.support.visitor.equals.CloneHelper.clone(m.getFormalCtTypeParameters()));
		aCtMethod.setType(spoon.support.visitor.equals.CloneHelper.clone(m.getType()));
		aCtMethod.setParameters(spoon.support.visitor.equals.CloneHelper.clone(m.getParameters()));
		aCtMethod.setThrownTypes(spoon.support.visitor.equals.CloneHelper.clone(m.getThrownTypes()));
		aCtMethod.setBody(spoon.support.visitor.equals.CloneHelper.clone(m.getBody()));
		aCtMethod.setComments(spoon.support.visitor.equals.CloneHelper.clone(m.getComments()));
		this.other = aCtMethod;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtAnnotationMethod(spoon.reflect.declaration.CtAnnotationMethod<T> annotationMethod) {
		spoon.reflect.declaration.CtAnnotationMethod<T> aCtAnnotationMethod = spoon.support.visitor.clone.CloneBuilder.build(this.builder, annotationMethod, annotationMethod.getFactory().Core().createAnnotationMethod());
		aCtAnnotationMethod.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(annotationMethod.getAnnotations()));
		aCtAnnotationMethod.setType(spoon.support.visitor.equals.CloneHelper.clone(annotationMethod.getType()));
		aCtAnnotationMethod.setDefaultExpression(spoon.support.visitor.equals.CloneHelper.clone(annotationMethod.getDefaultExpression()));
		aCtAnnotationMethod.setComments(spoon.support.visitor.equals.CloneHelper.clone(annotationMethod.getComments()));
		this.other = aCtAnnotationMethod;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtNewArray(final spoon.reflect.code.CtNewArray<T> newArray) {
		spoon.reflect.code.CtNewArray<T> aCtNewArray = spoon.support.visitor.clone.CloneBuilder.build(this.builder, newArray, newArray.getFactory().Core().createNewArray());
		aCtNewArray.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(newArray.getAnnotations()));
		aCtNewArray.setType(spoon.support.visitor.equals.CloneHelper.clone(newArray.getType()));
		aCtNewArray.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(newArray.getTypeCasts()));
		aCtNewArray.setElements(spoon.support.visitor.equals.CloneHelper.clone(newArray.getElements()));
		aCtNewArray.setDimensionExpressions(spoon.support.visitor.equals.CloneHelper.clone(newArray.getDimensionExpressions()));
		aCtNewArray.setComments(spoon.support.visitor.equals.CloneHelper.clone(newArray.getComments()));
		this.other = aCtNewArray;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtConstructorCall(final spoon.reflect.code.CtConstructorCall<T> ctConstructorCall) {
		spoon.reflect.code.CtConstructorCall<T> aCtConstructorCall = spoon.support.visitor.clone.CloneBuilder.build(this.builder, ctConstructorCall, ctConstructorCall.getFactory().Core().createConstructorCall());
		aCtConstructorCall.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(ctConstructorCall.getAnnotations()));
		aCtConstructorCall.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(ctConstructorCall.getTypeCasts()));
		aCtConstructorCall.setExecutable(spoon.support.visitor.equals.CloneHelper.clone(ctConstructorCall.getExecutable()));
		aCtConstructorCall.setTarget(spoon.support.visitor.equals.CloneHelper.clone(ctConstructorCall.getTarget()));
		aCtConstructorCall.setArguments(spoon.support.visitor.equals.CloneHelper.clone(ctConstructorCall.getArguments()));
		aCtConstructorCall.setComments(spoon.support.visitor.equals.CloneHelper.clone(ctConstructorCall.getComments()));
		this.other = aCtConstructorCall;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtNewClass(final spoon.reflect.code.CtNewClass<T> newClass) {
		spoon.reflect.code.CtNewClass<T> aCtNewClass = spoon.support.visitor.clone.CloneBuilder.build(this.builder, newClass, newClass.getFactory().Core().createNewClass());
		aCtNewClass.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(newClass.getAnnotations()));
		aCtNewClass.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(newClass.getTypeCasts()));
		aCtNewClass.setExecutable(spoon.support.visitor.equals.CloneHelper.clone(newClass.getExecutable()));
		aCtNewClass.setTarget(spoon.support.visitor.equals.CloneHelper.clone(newClass.getTarget()));
		aCtNewClass.setArguments(spoon.support.visitor.equals.CloneHelper.clone(newClass.getArguments()));
		aCtNewClass.setAnonymousClass(spoon.support.visitor.equals.CloneHelper.clone(newClass.getAnonymousClass()));
		aCtNewClass.setComments(spoon.support.visitor.equals.CloneHelper.clone(newClass.getComments()));
		this.other = aCtNewClass;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtLambda(final spoon.reflect.code.CtLambda<T> lambda) {
		spoon.reflect.code.CtLambda<T> aCtLambda = spoon.support.visitor.clone.CloneBuilder.build(this.builder, lambda, lambda.getFactory().Core().createLambda());
		aCtLambda.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(lambda.getAnnotations()));
		aCtLambda.setType(spoon.support.visitor.equals.CloneHelper.clone(lambda.getType()));
		aCtLambda.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(lambda.getTypeCasts()));
		aCtLambda.setParameters(spoon.support.visitor.equals.CloneHelper.clone(lambda.getParameters()));
		aCtLambda.setBody(spoon.support.visitor.equals.CloneHelper.clone(lambda.getBody()));
		aCtLambda.setExpression(spoon.support.visitor.equals.CloneHelper.clone(lambda.getExpression()));
		aCtLambda.setComments(spoon.support.visitor.equals.CloneHelper.clone(lambda.getComments()));
		this.other = aCtLambda;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T, E extends spoon.reflect.code.CtExpression<?>> void visitCtExecutableReferenceExpression(final spoon.reflect.code.CtExecutableReferenceExpression<T, E> expression) {
		spoon.reflect.code.CtExecutableReferenceExpression<T, E> aCtExecutableReferenceExpression = spoon.support.visitor.clone.CloneBuilder.build(this.builder, expression, expression.getFactory().Core().createExecutableReferenceExpression());
		aCtExecutableReferenceExpression.setComments(spoon.support.visitor.equals.CloneHelper.clone(expression.getComments()));
		aCtExecutableReferenceExpression.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(expression.getAnnotations()));
		aCtExecutableReferenceExpression.setType(spoon.support.visitor.equals.CloneHelper.clone(expression.getType()));
		aCtExecutableReferenceExpression.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(expression.getTypeCasts()));
		aCtExecutableReferenceExpression.setExecutable(spoon.support.visitor.equals.CloneHelper.clone(expression.getExecutable()));
		aCtExecutableReferenceExpression.setTarget(spoon.support.visitor.equals.CloneHelper.clone(expression.getTarget()));
		this.other = aCtExecutableReferenceExpression;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T, A extends T> void visitCtOperatorAssignment(final spoon.reflect.code.CtOperatorAssignment<T, A> assignment) {
		spoon.reflect.code.CtOperatorAssignment<T, A> aCtOperatorAssignment = spoon.support.visitor.clone.CloneBuilder.build(this.builder, assignment, assignment.getFactory().Core().createOperatorAssignment());
		aCtOperatorAssignment.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(assignment.getAnnotations()));
		aCtOperatorAssignment.setType(spoon.support.visitor.equals.CloneHelper.clone(assignment.getType()));
		aCtOperatorAssignment.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(assignment.getTypeCasts()));
		aCtOperatorAssignment.setAssigned(spoon.support.visitor.equals.CloneHelper.clone(assignment.getAssigned()));
		aCtOperatorAssignment.setAssignment(spoon.support.visitor.equals.CloneHelper.clone(assignment.getAssignment()));
		aCtOperatorAssignment.setComments(spoon.support.visitor.equals.CloneHelper.clone(assignment.getComments()));
		this.other = aCtOperatorAssignment;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtPackage(final spoon.reflect.declaration.CtPackage ctPackage) {
		spoon.reflect.declaration.CtPackage aCtPackage = spoon.support.visitor.clone.CloneBuilder.build(this.builder, ctPackage, ctPackage.getFactory().Core().createPackage());
		aCtPackage.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(ctPackage.getAnnotations()));
		aCtPackage.setPackages(spoon.support.visitor.equals.CloneHelper.clone(ctPackage.getPackages()));
		aCtPackage.setTypes(spoon.support.visitor.equals.CloneHelper.clone(ctPackage.getTypes()));
		aCtPackage.setComments(spoon.support.visitor.equals.CloneHelper.clone(ctPackage.getComments()));
		this.other = aCtPackage;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtPackageReference(final spoon.reflect.reference.CtPackageReference reference) {
		spoon.reflect.reference.CtPackageReference aCtPackageReference = spoon.support.visitor.clone.CloneBuilder.build(this.builder, reference, reference.getFactory().Core().createPackageReference());
		aCtPackageReference.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(reference.getAnnotations()));
		this.other = aCtPackageReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtParameter(final spoon.reflect.declaration.CtParameter<T> parameter) {
		spoon.reflect.declaration.CtParameter<T> aCtParameter = spoon.support.visitor.clone.CloneBuilder.build(this.builder, parameter, parameter.getFactory().Core().createParameter());
		aCtParameter.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(parameter.getAnnotations()));
		aCtParameter.setType(spoon.support.visitor.equals.CloneHelper.clone(parameter.getType()));
		aCtParameter.setComments(spoon.support.visitor.equals.CloneHelper.clone(parameter.getComments()));
		this.other = aCtParameter;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtParameterReference(final spoon.reflect.reference.CtParameterReference<T> reference) {
		spoon.reflect.reference.CtParameterReference<T> aCtParameterReference = spoon.support.visitor.clone.CloneBuilder.build(this.builder, reference, reference.getFactory().Core().createParameterReference());
		aCtParameterReference.setType(spoon.support.visitor.equals.CloneHelper.clone(reference.getType()));
		aCtParameterReference.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(reference.getAnnotations()));
		aCtParameterReference.setDeclaringExecutable(spoon.support.visitor.equals.CloneHelper.clone(reference.getDeclaringExecutable()));
		this.other = aCtParameterReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <R> void visitCtReturn(final spoon.reflect.code.CtReturn<R> returnStatement) {
		spoon.reflect.code.CtReturn<R> aCtReturn = spoon.support.visitor.clone.CloneBuilder.build(this.builder, returnStatement, returnStatement.getFactory().Core().createReturn());
		aCtReturn.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(returnStatement.getAnnotations()));
		aCtReturn.setReturnedExpression(spoon.support.visitor.equals.CloneHelper.clone(returnStatement.getReturnedExpression()));
		aCtReturn.setComments(spoon.support.visitor.equals.CloneHelper.clone(returnStatement.getComments()));
		this.other = aCtReturn;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <R> void visitCtStatementList(final spoon.reflect.code.CtStatementList statements) {
		spoon.reflect.code.CtStatementList aCtStatementList = spoon.support.visitor.clone.CloneBuilder.build(this.builder, statements, statements.getFactory().Core().createStatementList());
		aCtStatementList.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(statements.getAnnotations()));
		aCtStatementList.setStatements(spoon.support.visitor.equals.CloneHelper.clone(statements.getStatements()));
		aCtStatementList.setComments(spoon.support.visitor.equals.CloneHelper.clone(statements.getComments()));
		this.other = aCtStatementList;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <S> void visitCtSwitch(final spoon.reflect.code.CtSwitch<S> switchStatement) {
		spoon.reflect.code.CtSwitch<S> aCtSwitch = spoon.support.visitor.clone.CloneBuilder.build(this.builder, switchStatement, switchStatement.getFactory().Core().createSwitch());
		aCtSwitch.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(switchStatement.getAnnotations()));
		aCtSwitch.setSelector(spoon.support.visitor.equals.CloneHelper.clone(switchStatement.getSelector()));
		aCtSwitch.setCases(spoon.support.visitor.equals.CloneHelper.clone(switchStatement.getCases()));
		aCtSwitch.setComments(spoon.support.visitor.equals.CloneHelper.clone(switchStatement.getComments()));
		this.other = aCtSwitch;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtSynchronized(final spoon.reflect.code.CtSynchronized synchro) {
		spoon.reflect.code.CtSynchronized aCtSynchronized = spoon.support.visitor.clone.CloneBuilder.build(this.builder, synchro, synchro.getFactory().Core().createSynchronized());
		aCtSynchronized.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(synchro.getAnnotations()));
		aCtSynchronized.setExpression(spoon.support.visitor.equals.CloneHelper.clone(synchro.getExpression()));
		aCtSynchronized.setBlock(spoon.support.visitor.equals.CloneHelper.clone(synchro.getBlock()));
		aCtSynchronized.setComments(spoon.support.visitor.equals.CloneHelper.clone(synchro.getComments()));
		this.other = aCtSynchronized;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtThrow(final spoon.reflect.code.CtThrow throwStatement) {
		spoon.reflect.code.CtThrow aCtThrow = spoon.support.visitor.clone.CloneBuilder.build(this.builder, throwStatement, throwStatement.getFactory().Core().createThrow());
		aCtThrow.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(throwStatement.getAnnotations()));
		aCtThrow.setThrownExpression(spoon.support.visitor.equals.CloneHelper.clone(throwStatement.getThrownExpression()));
		aCtThrow.setComments(spoon.support.visitor.equals.CloneHelper.clone(throwStatement.getComments()));
		this.other = aCtThrow;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtTry(final spoon.reflect.code.CtTry tryBlock) {
		spoon.reflect.code.CtTry aCtTry = spoon.support.visitor.clone.CloneBuilder.build(this.builder, tryBlock, tryBlock.getFactory().Core().createTry());
		aCtTry.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(tryBlock.getAnnotations()));
		aCtTry.setBody(spoon.support.visitor.equals.CloneHelper.clone(tryBlock.getBody()));
		aCtTry.setCatchers(spoon.support.visitor.equals.CloneHelper.clone(tryBlock.getCatchers()));
		aCtTry.setFinalizer(spoon.support.visitor.equals.CloneHelper.clone(tryBlock.getFinalizer()));
		aCtTry.setComments(spoon.support.visitor.equals.CloneHelper.clone(tryBlock.getComments()));
		this.other = aCtTry;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtTryWithResource(final spoon.reflect.code.CtTryWithResource tryWithResource) {
		spoon.reflect.code.CtTryWithResource aCtTryWithResource = spoon.support.visitor.clone.CloneBuilder.build(this.builder, tryWithResource, tryWithResource.getFactory().Core().createTryWithResource());
		aCtTryWithResource.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(tryWithResource.getAnnotations()));
		aCtTryWithResource.setResources(spoon.support.visitor.equals.CloneHelper.clone(tryWithResource.getResources()));
		aCtTryWithResource.setBody(spoon.support.visitor.equals.CloneHelper.clone(tryWithResource.getBody()));
		aCtTryWithResource.setCatchers(spoon.support.visitor.equals.CloneHelper.clone(tryWithResource.getCatchers()));
		aCtTryWithResource.setFinalizer(spoon.support.visitor.equals.CloneHelper.clone(tryWithResource.getFinalizer()));
		aCtTryWithResource.setComments(spoon.support.visitor.equals.CloneHelper.clone(tryWithResource.getComments()));
		this.other = aCtTryWithResource;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtTypeParameterReference(final spoon.reflect.reference.CtTypeParameterReference ref) {
		spoon.reflect.reference.CtTypeParameterReference aCtTypeParameterReference = spoon.support.visitor.clone.CloneBuilder.build(this.builder, ref, ref.getFactory().Core().createTypeParameterReference());
		aCtTypeParameterReference.setPackage(spoon.support.visitor.equals.CloneHelper.clone(ref.getPackage()));
		aCtTypeParameterReference.setDeclaringType(spoon.support.visitor.equals.CloneHelper.clone(ref.getDeclaringType()));
		aCtTypeParameterReference.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(ref.getAnnotations()));
		aCtTypeParameterReference.setBoundingType(spoon.support.visitor.equals.CloneHelper.clone(ref.getBoundingType()));
		this.other = aCtTypeParameterReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtWildcardReference(spoon.reflect.reference.CtWildcardReference wildcardReference) {
		spoon.reflect.reference.CtWildcardReference aCtWildcardReference = spoon.support.visitor.clone.CloneBuilder.build(this.builder, wildcardReference, wildcardReference.getFactory().Core().createWildcardReference());
		aCtWildcardReference.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(wildcardReference.getAnnotations()));
		aCtWildcardReference.setBoundingType(spoon.support.visitor.equals.CloneHelper.clone(wildcardReference.getBoundingType()));
		this.other = aCtWildcardReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtIntersectionTypeReference(final spoon.reflect.reference.CtIntersectionTypeReference<T> reference) {
		spoon.reflect.reference.CtIntersectionTypeReference<T> aCtIntersectionTypeReference = spoon.support.visitor.clone.CloneBuilder.build(this.builder, reference, reference.getFactory().Core().createIntersectionTypeReference());
		aCtIntersectionTypeReference.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(reference.getAnnotations()));
		aCtIntersectionTypeReference.setBounds(spoon.support.visitor.equals.CloneHelper.clone(reference.getBounds()));
		this.other = aCtIntersectionTypeReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtTypeReference(final spoon.reflect.reference.CtTypeReference<T> reference) {
		spoon.reflect.reference.CtTypeReference<T> aCtTypeReference = spoon.support.visitor.clone.CloneBuilder.build(this.builder, reference, reference.getFactory().Core().createTypeReference());
		aCtTypeReference.setPackage(spoon.support.visitor.equals.CloneHelper.clone(reference.getPackage()));
		aCtTypeReference.setDeclaringType(spoon.support.visitor.equals.CloneHelper.clone(reference.getDeclaringType()));
		aCtTypeReference.setActualTypeArguments(spoon.support.visitor.equals.CloneHelper.clone(reference.getActualTypeArguments()));
		aCtTypeReference.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(reference.getAnnotations()));
		aCtTypeReference.setComments(spoon.support.visitor.equals.CloneHelper.clone(reference.getComments()));
		this.other = aCtTypeReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtTypeAccess(final spoon.reflect.code.CtTypeAccess<T> typeAccess) {
		spoon.reflect.code.CtTypeAccess<T> aCtTypeAccess = spoon.support.visitor.clone.CloneBuilder.build(this.builder, typeAccess, typeAccess.getFactory().Core().createTypeAccess());
		aCtTypeAccess.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(typeAccess.getAnnotations()));
		aCtTypeAccess.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(typeAccess.getTypeCasts()));
		aCtTypeAccess.setAccessedType(spoon.support.visitor.equals.CloneHelper.clone(typeAccess.getAccessedType()));
		aCtTypeAccess.setComments(spoon.support.visitor.equals.CloneHelper.clone(typeAccess.getComments()));
		this.other = aCtTypeAccess;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtUnaryOperator(final spoon.reflect.code.CtUnaryOperator<T> operator) {
		spoon.reflect.code.CtUnaryOperator<T> aCtUnaryOperator = spoon.support.visitor.clone.CloneBuilder.build(this.builder, operator, operator.getFactory().Core().createUnaryOperator());
		aCtUnaryOperator.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(operator.getAnnotations()));
		aCtUnaryOperator.setType(spoon.support.visitor.equals.CloneHelper.clone(operator.getType()));
		aCtUnaryOperator.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(operator.getTypeCasts()));
		aCtUnaryOperator.setOperand(spoon.support.visitor.equals.CloneHelper.clone(operator.getOperand()));
		aCtUnaryOperator.setComments(spoon.support.visitor.equals.CloneHelper.clone(operator.getComments()));
		this.other = aCtUnaryOperator;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtVariableRead(final spoon.reflect.code.CtVariableRead<T> variableRead) {
		spoon.reflect.code.CtVariableRead<T> aCtVariableRead = spoon.support.visitor.clone.CloneBuilder.build(this.builder, variableRead, variableRead.getFactory().Core().createVariableRead());
		aCtVariableRead.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(variableRead.getAnnotations()));
		aCtVariableRead.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(variableRead.getTypeCasts()));
		aCtVariableRead.setVariable(spoon.support.visitor.equals.CloneHelper.clone(variableRead.getVariable()));
		aCtVariableRead.setComments(spoon.support.visitor.equals.CloneHelper.clone(variableRead.getComments()));
		this.other = aCtVariableRead;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtVariableWrite(final spoon.reflect.code.CtVariableWrite<T> variableWrite) {
		spoon.reflect.code.CtVariableWrite<T> aCtVariableWrite = spoon.support.visitor.clone.CloneBuilder.build(this.builder, variableWrite, variableWrite.getFactory().Core().createVariableWrite());
		aCtVariableWrite.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(variableWrite.getAnnotations()));
		aCtVariableWrite.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(variableWrite.getTypeCasts()));
		aCtVariableWrite.setVariable(spoon.support.visitor.equals.CloneHelper.clone(variableWrite.getVariable()));
		aCtVariableWrite.setComments(spoon.support.visitor.equals.CloneHelper.clone(variableWrite.getComments()));
		this.other = aCtVariableWrite;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtWhile(final spoon.reflect.code.CtWhile whileLoop) {
		spoon.reflect.code.CtWhile aCtWhile = spoon.support.visitor.clone.CloneBuilder.build(this.builder, whileLoop, whileLoop.getFactory().Core().createWhile());
		aCtWhile.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(whileLoop.getAnnotations()));
		aCtWhile.setLoopingExpression(spoon.support.visitor.equals.CloneHelper.clone(whileLoop.getLoopingExpression()));
		aCtWhile.setBody(spoon.support.visitor.equals.CloneHelper.clone(whileLoop.getBody()));
		aCtWhile.setComments(spoon.support.visitor.equals.CloneHelper.clone(whileLoop.getComments()));
		this.other = aCtWhile;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtCodeSnippetExpression(final spoon.reflect.code.CtCodeSnippetExpression<T> expression) {
		spoon.reflect.code.CtCodeSnippetExpression<T> aCtCodeSnippetExpression = spoon.support.visitor.clone.CloneBuilder.build(this.builder, expression, expression.getFactory().Core().createCodeSnippetExpression());
		aCtCodeSnippetExpression.setComments(spoon.support.visitor.equals.CloneHelper.clone(expression.getComments()));
		aCtCodeSnippetExpression.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(expression.getAnnotations()));
		aCtCodeSnippetExpression.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(expression.getTypeCasts()));
		this.other = aCtCodeSnippetExpression;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtCodeSnippetStatement(final spoon.reflect.code.CtCodeSnippetStatement statement) {
		spoon.reflect.code.CtCodeSnippetStatement aCtCodeSnippetStatement = spoon.support.visitor.clone.CloneBuilder.build(this.builder, statement, statement.getFactory().Core().createCodeSnippetStatement());
		aCtCodeSnippetStatement.setComments(spoon.support.visitor.equals.CloneHelper.clone(statement.getComments()));
		aCtCodeSnippetStatement.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(statement.getAnnotations()));
		this.other = aCtCodeSnippetStatement;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtUnboundVariableReference(final spoon.reflect.reference.CtUnboundVariableReference<T> reference) {
		spoon.reflect.reference.CtUnboundVariableReference<T> aCtUnboundVariableReference = spoon.support.visitor.clone.CloneBuilder.build(this.builder, reference, reference.getFactory().Core().createUnboundVariableReference());
		this.other = aCtUnboundVariableReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtFieldRead(final spoon.reflect.code.CtFieldRead<T> fieldRead) {
		spoon.reflect.code.CtFieldRead<T> aCtFieldRead = spoon.support.visitor.clone.CloneBuilder.build(this.builder, fieldRead, fieldRead.getFactory().Core().createFieldRead());
		aCtFieldRead.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(fieldRead.getAnnotations()));
		aCtFieldRead.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(fieldRead.getTypeCasts()));
		aCtFieldRead.setTarget(spoon.support.visitor.equals.CloneHelper.clone(fieldRead.getTarget()));
		aCtFieldRead.setVariable(spoon.support.visitor.equals.CloneHelper.clone(fieldRead.getVariable()));
		aCtFieldRead.setComments(spoon.support.visitor.equals.CloneHelper.clone(fieldRead.getComments()));
		this.other = aCtFieldRead;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtFieldWrite(final spoon.reflect.code.CtFieldWrite<T> fieldWrite) {
		spoon.reflect.code.CtFieldWrite<T> aCtFieldWrite = spoon.support.visitor.clone.CloneBuilder.build(this.builder, fieldWrite, fieldWrite.getFactory().Core().createFieldWrite());
		aCtFieldWrite.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(fieldWrite.getAnnotations()));
		aCtFieldWrite.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(fieldWrite.getTypeCasts()));
		aCtFieldWrite.setTarget(spoon.support.visitor.equals.CloneHelper.clone(fieldWrite.getTarget()));
		aCtFieldWrite.setVariable(spoon.support.visitor.equals.CloneHelper.clone(fieldWrite.getVariable()));
		aCtFieldWrite.setComments(spoon.support.visitor.equals.CloneHelper.clone(fieldWrite.getComments()));
		this.other = aCtFieldWrite;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtSuperAccess(final spoon.reflect.code.CtSuperAccess<T> f) {
		spoon.reflect.code.CtSuperAccess<T> aCtSuperAccess = spoon.support.visitor.clone.CloneBuilder.build(this.builder, f, f.getFactory().Core().createSuperAccess());
		aCtSuperAccess.setComments(spoon.support.visitor.equals.CloneHelper.clone(f.getComments()));
		aCtSuperAccess.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(f.getAnnotations()));
		aCtSuperAccess.setType(spoon.support.visitor.equals.CloneHelper.clone(f.getType()));
		aCtSuperAccess.setTypeCasts(spoon.support.visitor.equals.CloneHelper.clone(f.getTypeCasts()));
		aCtSuperAccess.setTarget(spoon.support.visitor.equals.CloneHelper.clone(f.getTarget()));
		aCtSuperAccess.setVariable(spoon.support.visitor.equals.CloneHelper.clone(f.getVariable()));
		this.other = aCtSuperAccess;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtComment(final spoon.reflect.code.CtComment comment) {
		spoon.reflect.code.CtComment aCtComment = spoon.support.visitor.clone.CloneBuilder.build(this.builder, comment, comment.getFactory().Core().createComment());
		aCtComment.setComments(spoon.support.visitor.equals.CloneHelper.clone(comment.getComments()));
		aCtComment.setAnnotations(spoon.support.visitor.equals.CloneHelper.clone(comment.getAnnotations()));
		this.other = aCtComment;
	}
}

