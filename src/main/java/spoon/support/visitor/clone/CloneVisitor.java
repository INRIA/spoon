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
package spoon.support.visitor.clone;


/**
 * Used to clone a given element.
 *
 * This class is generated automatically by the processor spoon.generating.CloneVisitorGenerator.
 */
public class CloneVisitor extends spoon.reflect.visitor.CtScanner {
	private final spoon.support.visitor.equals.CloneHelper cloneHelper;

	private final spoon.support.visitor.clone.CloneBuilder builder = new spoon.support.visitor.clone.CloneBuilder();

	private spoon.reflect.declaration.CtElement other;

	public CloneVisitor(spoon.support.visitor.equals.CloneHelper cloneHelper) {
		this.cloneHelper = cloneHelper;
	}

	public <T extends spoon.reflect.declaration.CtElement> T getClone() {
		return ((T) (other));
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <A extends java.lang.annotation.Annotation> void visitCtAnnotation(final spoon.reflect.declaration.CtAnnotation<A> annotation) {
		spoon.reflect.declaration.CtAnnotation<A> aCtAnnotation = annotation.getFactory().Core().createAnnotation();
		aCtAnnotation.setType(this.cloneHelper.clone(annotation.getType()));
		aCtAnnotation.setComments(this.cloneHelper.clone(annotation.getComments()));
		aCtAnnotation.setAnnotationType(this.cloneHelper.clone(annotation.getAnnotationType()));
		aCtAnnotation.setAnnotations(this.cloneHelper.clone(annotation.getAnnotations()));
		aCtAnnotation.setValues(this.cloneHelper.clone(annotation.getValues()));
		this.builder.copy(annotation, aCtAnnotation);
		this.cloneHelper.tailor(annotation, aCtAnnotation);
		this.other = aCtAnnotation;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <A extends java.lang.annotation.Annotation> void visitCtAnnotationType(final spoon.reflect.declaration.CtAnnotationType<A> annotationType) {
		spoon.reflect.declaration.CtAnnotationType<A> aCtAnnotationType = annotationType.getFactory().Core().createAnnotationType();
		aCtAnnotationType.setAnnotations(this.cloneHelper.clone(annotationType.getAnnotations()));
		aCtAnnotationType.setTypeMembers(this.cloneHelper.clone(annotationType.getTypeMembers()));
		aCtAnnotationType.setComments(this.cloneHelper.clone(annotationType.getComments()));
		this.builder.copy(annotationType, aCtAnnotationType);
		this.cloneHelper.tailor(annotationType, aCtAnnotationType);
		this.other = aCtAnnotationType;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtAnonymousExecutable(final spoon.reflect.declaration.CtAnonymousExecutable anonymousExec) {
		spoon.reflect.declaration.CtAnonymousExecutable aCtAnonymousExecutable = anonymousExec.getFactory().Core().createAnonymousExecutable();
		aCtAnonymousExecutable.setAnnotations(this.cloneHelper.clone(anonymousExec.getAnnotations()));
		aCtAnonymousExecutable.setBody(this.cloneHelper.clone(anonymousExec.getBody()));
		aCtAnonymousExecutable.setComments(this.cloneHelper.clone(anonymousExec.getComments()));
		this.builder.copy(anonymousExec, aCtAnonymousExecutable);
		this.cloneHelper.tailor(anonymousExec, aCtAnonymousExecutable);
		this.other = aCtAnonymousExecutable;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtArrayRead(final spoon.reflect.code.CtArrayRead<T> arrayRead) {
		spoon.reflect.code.CtArrayRead<T> aCtArrayRead = arrayRead.getFactory().Core().createArrayRead();
		aCtArrayRead.setAnnotations(this.cloneHelper.clone(arrayRead.getAnnotations()));
		aCtArrayRead.setType(this.cloneHelper.clone(arrayRead.getType()));
		aCtArrayRead.setTypeCasts(this.cloneHelper.clone(arrayRead.getTypeCasts()));
		aCtArrayRead.setTarget(this.cloneHelper.clone(arrayRead.getTarget()));
		aCtArrayRead.setIndexExpression(this.cloneHelper.clone(arrayRead.getIndexExpression()));
		aCtArrayRead.setComments(this.cloneHelper.clone(arrayRead.getComments()));
		this.builder.copy(arrayRead, aCtArrayRead);
		this.cloneHelper.tailor(arrayRead, aCtArrayRead);
		this.other = aCtArrayRead;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtArrayWrite(final spoon.reflect.code.CtArrayWrite<T> arrayWrite) {
		spoon.reflect.code.CtArrayWrite<T> aCtArrayWrite = arrayWrite.getFactory().Core().createArrayWrite();
		aCtArrayWrite.setAnnotations(this.cloneHelper.clone(arrayWrite.getAnnotations()));
		aCtArrayWrite.setType(this.cloneHelper.clone(arrayWrite.getType()));
		aCtArrayWrite.setTypeCasts(this.cloneHelper.clone(arrayWrite.getTypeCasts()));
		aCtArrayWrite.setTarget(this.cloneHelper.clone(arrayWrite.getTarget()));
		aCtArrayWrite.setIndexExpression(this.cloneHelper.clone(arrayWrite.getIndexExpression()));
		aCtArrayWrite.setComments(this.cloneHelper.clone(arrayWrite.getComments()));
		this.builder.copy(arrayWrite, aCtArrayWrite);
		this.cloneHelper.tailor(arrayWrite, aCtArrayWrite);
		this.other = aCtArrayWrite;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtArrayTypeReference(final spoon.reflect.reference.CtArrayTypeReference<T> reference) {
		spoon.reflect.reference.CtArrayTypeReference<T> aCtArrayTypeReference = reference.getFactory().Core().createArrayTypeReference();
		aCtArrayTypeReference.setPackage(this.cloneHelper.clone(reference.getPackage()));
		aCtArrayTypeReference.setDeclaringType(this.cloneHelper.clone(reference.getDeclaringType()));
		aCtArrayTypeReference.setComponentType(this.cloneHelper.clone(reference.getComponentType()));
		aCtArrayTypeReference.setActualTypeArguments(this.cloneHelper.clone(reference.getActualTypeArguments()));
		aCtArrayTypeReference.setAnnotations(this.cloneHelper.clone(reference.getAnnotations()));
		this.builder.copy(reference, aCtArrayTypeReference);
		this.cloneHelper.tailor(reference, aCtArrayTypeReference);
		this.other = aCtArrayTypeReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtAssert(final spoon.reflect.code.CtAssert<T> asserted) {
		spoon.reflect.code.CtAssert<T> aCtAssert = asserted.getFactory().Core().createAssert();
		aCtAssert.setAnnotations(this.cloneHelper.clone(asserted.getAnnotations()));
		aCtAssert.setAssertExpression(this.cloneHelper.clone(asserted.getAssertExpression()));
		aCtAssert.setExpression(this.cloneHelper.clone(asserted.getExpression()));
		aCtAssert.setComments(this.cloneHelper.clone(asserted.getComments()));
		this.builder.copy(asserted, aCtAssert);
		this.cloneHelper.tailor(asserted, aCtAssert);
		this.other = aCtAssert;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T, A extends T> void visitCtAssignment(final spoon.reflect.code.CtAssignment<T, A> assignement) {
		spoon.reflect.code.CtAssignment<T, A> aCtAssignment = assignement.getFactory().Core().createAssignment();
		aCtAssignment.setAnnotations(this.cloneHelper.clone(assignement.getAnnotations()));
		aCtAssignment.setType(this.cloneHelper.clone(assignement.getType()));
		aCtAssignment.setTypeCasts(this.cloneHelper.clone(assignement.getTypeCasts()));
		aCtAssignment.setAssigned(this.cloneHelper.clone(assignement.getAssigned()));
		aCtAssignment.setAssignment(this.cloneHelper.clone(assignement.getAssignment()));
		aCtAssignment.setComments(this.cloneHelper.clone(assignement.getComments()));
		this.builder.copy(assignement, aCtAssignment);
		this.cloneHelper.tailor(assignement, aCtAssignment);
		this.other = aCtAssignment;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtBinaryOperator(final spoon.reflect.code.CtBinaryOperator<T> operator) {
		spoon.reflect.code.CtBinaryOperator<T> aCtBinaryOperator = operator.getFactory().Core().createBinaryOperator();
		aCtBinaryOperator.setAnnotations(this.cloneHelper.clone(operator.getAnnotations()));
		aCtBinaryOperator.setType(this.cloneHelper.clone(operator.getType()));
		aCtBinaryOperator.setTypeCasts(this.cloneHelper.clone(operator.getTypeCasts()));
		aCtBinaryOperator.setLeftHandOperand(this.cloneHelper.clone(operator.getLeftHandOperand()));
		aCtBinaryOperator.setRightHandOperand(this.cloneHelper.clone(operator.getRightHandOperand()));
		aCtBinaryOperator.setComments(this.cloneHelper.clone(operator.getComments()));
		this.builder.copy(operator, aCtBinaryOperator);
		this.cloneHelper.tailor(operator, aCtBinaryOperator);
		this.other = aCtBinaryOperator;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <R> void visitCtBlock(final spoon.reflect.code.CtBlock<R> block) {
		spoon.reflect.code.CtBlock<R> aCtBlock = block.getFactory().Core().createBlock();
		aCtBlock.setAnnotations(this.cloneHelper.clone(block.getAnnotations()));
		aCtBlock.setStatements(this.cloneHelper.clone(block.getStatements()));
		aCtBlock.setComments(this.cloneHelper.clone(block.getComments()));
		this.builder.copy(block, aCtBlock);
		this.cloneHelper.tailor(block, aCtBlock);
		this.other = aCtBlock;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtBreak(final spoon.reflect.code.CtBreak breakStatement) {
		spoon.reflect.code.CtBreak aCtBreak = breakStatement.getFactory().Core().createBreak();
		aCtBreak.setAnnotations(this.cloneHelper.clone(breakStatement.getAnnotations()));
		aCtBreak.setComments(this.cloneHelper.clone(breakStatement.getComments()));
		this.builder.copy(breakStatement, aCtBreak);
		this.cloneHelper.tailor(breakStatement, aCtBreak);
		this.other = aCtBreak;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <S> void visitCtCase(final spoon.reflect.code.CtCase<S> caseStatement) {
		spoon.reflect.code.CtCase<S> aCtCase = caseStatement.getFactory().Core().createCase();
		aCtCase.setAnnotations(this.cloneHelper.clone(caseStatement.getAnnotations()));
		aCtCase.setCaseExpression(this.cloneHelper.clone(caseStatement.getCaseExpression()));
		aCtCase.setStatements(this.cloneHelper.clone(caseStatement.getStatements()));
		aCtCase.setComments(this.cloneHelper.clone(caseStatement.getComments()));
		this.builder.copy(caseStatement, aCtCase);
		this.cloneHelper.tailor(caseStatement, aCtCase);
		this.other = aCtCase;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtCatch(final spoon.reflect.code.CtCatch catchBlock) {
		spoon.reflect.code.CtCatch aCtCatch = catchBlock.getFactory().Core().createCatch();
		aCtCatch.setAnnotations(this.cloneHelper.clone(catchBlock.getAnnotations()));
		aCtCatch.setParameter(this.cloneHelper.clone(catchBlock.getParameter()));
		aCtCatch.setBody(this.cloneHelper.clone(catchBlock.getBody()));
		aCtCatch.setComments(this.cloneHelper.clone(catchBlock.getComments()));
		this.builder.copy(catchBlock, aCtCatch);
		this.cloneHelper.tailor(catchBlock, aCtCatch);
		this.other = aCtCatch;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtClass(final spoon.reflect.declaration.CtClass<T> ctClass) {
		spoon.reflect.declaration.CtClass<T> aCtClass = ctClass.getFactory().Core().createClass();
		aCtClass.setAnnotations(this.cloneHelper.clone(ctClass.getAnnotations()));
		aCtClass.setSuperclass(this.cloneHelper.clone(ctClass.getSuperclass()));
		aCtClass.setSuperInterfaces(this.cloneHelper.clone(ctClass.getSuperInterfaces()));
		aCtClass.setFormalCtTypeParameters(this.cloneHelper.clone(ctClass.getFormalCtTypeParameters()));
		aCtClass.setTypeMembers(this.cloneHelper.clone(ctClass.getTypeMembers()));
		aCtClass.setComments(this.cloneHelper.clone(ctClass.getComments()));
		this.builder.copy(ctClass, aCtClass);
		this.cloneHelper.tailor(ctClass, aCtClass);
		this.other = aCtClass;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtTypeParameter(spoon.reflect.declaration.CtTypeParameter typeParameter) {
		spoon.reflect.declaration.CtTypeParameter aCtTypeParameter = typeParameter.getFactory().Core().createTypeParameter();
		aCtTypeParameter.setAnnotations(this.cloneHelper.clone(typeParameter.getAnnotations()));
		aCtTypeParameter.setSuperclass(this.cloneHelper.clone(typeParameter.getSuperclass()));
		aCtTypeParameter.setComments(this.cloneHelper.clone(typeParameter.getComments()));
		this.builder.copy(typeParameter, aCtTypeParameter);
		this.cloneHelper.tailor(typeParameter, aCtTypeParameter);
		this.other = aCtTypeParameter;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtConditional(final spoon.reflect.code.CtConditional<T> conditional) {
		spoon.reflect.code.CtConditional<T> aCtConditional = conditional.getFactory().Core().createConditional();
		aCtConditional.setType(this.cloneHelper.clone(conditional.getType()));
		aCtConditional.setAnnotations(this.cloneHelper.clone(conditional.getAnnotations()));
		aCtConditional.setCondition(this.cloneHelper.clone(conditional.getCondition()));
		aCtConditional.setThenExpression(this.cloneHelper.clone(conditional.getThenExpression()));
		aCtConditional.setElseExpression(this.cloneHelper.clone(conditional.getElseExpression()));
		aCtConditional.setComments(this.cloneHelper.clone(conditional.getComments()));
		aCtConditional.setTypeCasts(this.cloneHelper.clone(conditional.getTypeCasts()));
		this.builder.copy(conditional, aCtConditional);
		this.cloneHelper.tailor(conditional, aCtConditional);
		this.other = aCtConditional;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtConstructor(final spoon.reflect.declaration.CtConstructor<T> c) {
		spoon.reflect.declaration.CtConstructor<T> aCtConstructor = c.getFactory().Core().createConstructor();
		aCtConstructor.setAnnotations(this.cloneHelper.clone(c.getAnnotations()));
		aCtConstructor.setParameters(this.cloneHelper.clone(c.getParameters()));
		aCtConstructor.setThrownTypes(this.cloneHelper.clone(c.getThrownTypes()));
		aCtConstructor.setFormalCtTypeParameters(this.cloneHelper.clone(c.getFormalCtTypeParameters()));
		aCtConstructor.setBody(this.cloneHelper.clone(c.getBody()));
		aCtConstructor.setComments(this.cloneHelper.clone(c.getComments()));
		this.builder.copy(c, aCtConstructor);
		this.cloneHelper.tailor(c, aCtConstructor);
		this.other = aCtConstructor;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtContinue(final spoon.reflect.code.CtContinue continueStatement) {
		spoon.reflect.code.CtContinue aCtContinue = continueStatement.getFactory().Core().createContinue();
		aCtContinue.setAnnotations(this.cloneHelper.clone(continueStatement.getAnnotations()));
		aCtContinue.setComments(this.cloneHelper.clone(continueStatement.getComments()));
		this.builder.copy(continueStatement, aCtContinue);
		this.cloneHelper.tailor(continueStatement, aCtContinue);
		this.other = aCtContinue;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtDo(final spoon.reflect.code.CtDo doLoop) {
		spoon.reflect.code.CtDo aCtDo = doLoop.getFactory().Core().createDo();
		aCtDo.setAnnotations(this.cloneHelper.clone(doLoop.getAnnotations()));
		aCtDo.setLoopingExpression(this.cloneHelper.clone(doLoop.getLoopingExpression()));
		aCtDo.setBody(this.cloneHelper.clone(doLoop.getBody()));
		aCtDo.setComments(this.cloneHelper.clone(doLoop.getComments()));
		this.builder.copy(doLoop, aCtDo);
		this.cloneHelper.tailor(doLoop, aCtDo);
		this.other = aCtDo;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T extends java.lang.Enum<?>> void visitCtEnum(final spoon.reflect.declaration.CtEnum<T> ctEnum) {
		spoon.reflect.declaration.CtEnum<T> aCtEnum = ctEnum.getFactory().Core().createEnum();
		aCtEnum.setAnnotations(this.cloneHelper.clone(ctEnum.getAnnotations()));
		aCtEnum.setSuperInterfaces(this.cloneHelper.clone(ctEnum.getSuperInterfaces()));
		aCtEnum.setTypeMembers(this.cloneHelper.clone(ctEnum.getTypeMembers()));
		aCtEnum.setEnumValues(this.cloneHelper.clone(ctEnum.getEnumValues()));
		aCtEnum.setComments(this.cloneHelper.clone(ctEnum.getComments()));
		this.builder.copy(ctEnum, aCtEnum);
		this.cloneHelper.tailor(ctEnum, aCtEnum);
		this.other = aCtEnum;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtExecutableReference(final spoon.reflect.reference.CtExecutableReference<T> reference) {
		spoon.reflect.reference.CtExecutableReference<T> aCtExecutableReference = reference.getFactory().Core().createExecutableReference();
		aCtExecutableReference.setDeclaringType(this.cloneHelper.clone(reference.getDeclaringType()));
		aCtExecutableReference.setType(this.cloneHelper.clone(reference.getType()));
		aCtExecutableReference.setParameters(this.cloneHelper.clone(reference.getParameters()));
		aCtExecutableReference.setActualTypeArguments(this.cloneHelper.clone(reference.getActualTypeArguments()));
		aCtExecutableReference.setAnnotations(this.cloneHelper.clone(reference.getAnnotations()));
		aCtExecutableReference.setComments(this.cloneHelper.clone(reference.getComments()));
		this.builder.copy(reference, aCtExecutableReference);
		this.cloneHelper.tailor(reference, aCtExecutableReference);
		this.other = aCtExecutableReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtField(final spoon.reflect.declaration.CtField<T> f) {
		spoon.reflect.declaration.CtField<T> aCtField = f.getFactory().Core().createField();
		aCtField.setAnnotations(this.cloneHelper.clone(f.getAnnotations()));
		aCtField.setType(this.cloneHelper.clone(f.getType()));
		aCtField.setDefaultExpression(this.cloneHelper.clone(f.getDefaultExpression()));
		aCtField.setComments(this.cloneHelper.clone(f.getComments()));
		this.builder.copy(f, aCtField);
		this.cloneHelper.tailor(f, aCtField);
		this.other = aCtField;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtEnumValue(final spoon.reflect.declaration.CtEnumValue<T> enumValue) {
		spoon.reflect.declaration.CtEnumValue<T> aCtEnumValue = enumValue.getFactory().Core().createEnumValue();
		aCtEnumValue.setAnnotations(this.cloneHelper.clone(enumValue.getAnnotations()));
		aCtEnumValue.setType(this.cloneHelper.clone(enumValue.getType()));
		aCtEnumValue.setDefaultExpression(this.cloneHelper.clone(enumValue.getDefaultExpression()));
		aCtEnumValue.setComments(this.cloneHelper.clone(enumValue.getComments()));
		this.builder.copy(enumValue, aCtEnumValue);
		this.cloneHelper.tailor(enumValue, aCtEnumValue);
		this.other = aCtEnumValue;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtThisAccess(final spoon.reflect.code.CtThisAccess<T> thisAccess) {
		spoon.reflect.code.CtThisAccess<T> aCtThisAccess = thisAccess.getFactory().Core().createThisAccess();
		aCtThisAccess.setComments(this.cloneHelper.clone(thisAccess.getComments()));
		aCtThisAccess.setAnnotations(this.cloneHelper.clone(thisAccess.getAnnotations()));
		aCtThisAccess.setType(this.cloneHelper.clone(thisAccess.getType()));
		aCtThisAccess.setTypeCasts(this.cloneHelper.clone(thisAccess.getTypeCasts()));
		aCtThisAccess.setTarget(this.cloneHelper.clone(thisAccess.getTarget()));
		this.builder.copy(thisAccess, aCtThisAccess);
		this.cloneHelper.tailor(thisAccess, aCtThisAccess);
		this.other = aCtThisAccess;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtAnnotationFieldAccess(final spoon.reflect.code.CtAnnotationFieldAccess<T> annotationFieldAccess) {
		spoon.reflect.code.CtAnnotationFieldAccess<T> aCtAnnotationFieldAccess = annotationFieldAccess.getFactory().Core().createAnnotationFieldAccess();
		aCtAnnotationFieldAccess.setComments(this.cloneHelper.clone(annotationFieldAccess.getComments()));
		aCtAnnotationFieldAccess.setAnnotations(this.cloneHelper.clone(annotationFieldAccess.getAnnotations()));
		aCtAnnotationFieldAccess.setTypeCasts(this.cloneHelper.clone(annotationFieldAccess.getTypeCasts()));
		aCtAnnotationFieldAccess.setTarget(this.cloneHelper.clone(annotationFieldAccess.getTarget()));
		aCtAnnotationFieldAccess.setVariable(this.cloneHelper.clone(annotationFieldAccess.getVariable()));
		this.builder.copy(annotationFieldAccess, aCtAnnotationFieldAccess);
		this.cloneHelper.tailor(annotationFieldAccess, aCtAnnotationFieldAccess);
		this.other = aCtAnnotationFieldAccess;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtFieldReference(final spoon.reflect.reference.CtFieldReference<T> reference) {
		spoon.reflect.reference.CtFieldReference<T> aCtFieldReference = reference.getFactory().Core().createFieldReference();
		aCtFieldReference.setDeclaringType(this.cloneHelper.clone(reference.getDeclaringType()));
		aCtFieldReference.setType(this.cloneHelper.clone(reference.getType()));
		aCtFieldReference.setAnnotations(this.cloneHelper.clone(reference.getAnnotations()));
		this.builder.copy(reference, aCtFieldReference);
		this.cloneHelper.tailor(reference, aCtFieldReference);
		this.other = aCtFieldReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtFor(final spoon.reflect.code.CtFor forLoop) {
		spoon.reflect.code.CtFor aCtFor = forLoop.getFactory().Core().createFor();
		aCtFor.setAnnotations(this.cloneHelper.clone(forLoop.getAnnotations()));
		aCtFor.setForInit(this.cloneHelper.clone(forLoop.getForInit()));
		aCtFor.setExpression(this.cloneHelper.clone(forLoop.getExpression()));
		aCtFor.setForUpdate(this.cloneHelper.clone(forLoop.getForUpdate()));
		aCtFor.setBody(this.cloneHelper.clone(forLoop.getBody()));
		aCtFor.setComments(this.cloneHelper.clone(forLoop.getComments()));
		this.builder.copy(forLoop, aCtFor);
		this.cloneHelper.tailor(forLoop, aCtFor);
		this.other = aCtFor;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtForEach(final spoon.reflect.code.CtForEach foreach) {
		spoon.reflect.code.CtForEach aCtForEach = foreach.getFactory().Core().createForEach();
		aCtForEach.setAnnotations(this.cloneHelper.clone(foreach.getAnnotations()));
		aCtForEach.setVariable(this.cloneHelper.clone(foreach.getVariable()));
		aCtForEach.setExpression(this.cloneHelper.clone(foreach.getExpression()));
		aCtForEach.setBody(this.cloneHelper.clone(foreach.getBody()));
		aCtForEach.setComments(this.cloneHelper.clone(foreach.getComments()));
		this.builder.copy(foreach, aCtForEach);
		this.cloneHelper.tailor(foreach, aCtForEach);
		this.other = aCtForEach;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtIf(final spoon.reflect.code.CtIf ifElement) {
		spoon.reflect.code.CtIf aCtIf = ifElement.getFactory().Core().createIf();
		aCtIf.setAnnotations(this.cloneHelper.clone(ifElement.getAnnotations()));
		aCtIf.setCondition(this.cloneHelper.clone(ifElement.getCondition()));
		aCtIf.setThenStatement(this.cloneHelper.clone(((spoon.reflect.code.CtStatement) (ifElement.getThenStatement()))));
		aCtIf.setElseStatement(this.cloneHelper.clone(((spoon.reflect.code.CtStatement) (ifElement.getElseStatement()))));
		aCtIf.setComments(this.cloneHelper.clone(ifElement.getComments()));
		this.builder.copy(ifElement, aCtIf);
		this.cloneHelper.tailor(ifElement, aCtIf);
		this.other = aCtIf;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtInterface(final spoon.reflect.declaration.CtInterface<T> intrface) {
		spoon.reflect.declaration.CtInterface<T> aCtInterface = intrface.getFactory().Core().createInterface();
		aCtInterface.setAnnotations(this.cloneHelper.clone(intrface.getAnnotations()));
		aCtInterface.setSuperInterfaces(this.cloneHelper.clone(intrface.getSuperInterfaces()));
		aCtInterface.setFormalCtTypeParameters(this.cloneHelper.clone(intrface.getFormalCtTypeParameters()));
		aCtInterface.setTypeMembers(this.cloneHelper.clone(intrface.getTypeMembers()));
		aCtInterface.setComments(this.cloneHelper.clone(intrface.getComments()));
		this.builder.copy(intrface, aCtInterface);
		this.cloneHelper.tailor(intrface, aCtInterface);
		this.other = aCtInterface;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtInvocation(final spoon.reflect.code.CtInvocation<T> invocation) {
		spoon.reflect.code.CtInvocation<T> aCtInvocation = invocation.getFactory().Core().createInvocation();
		aCtInvocation.setAnnotations(this.cloneHelper.clone(invocation.getAnnotations()));
		aCtInvocation.setTypeCasts(this.cloneHelper.clone(invocation.getTypeCasts()));
		aCtInvocation.setTarget(this.cloneHelper.clone(invocation.getTarget()));
		aCtInvocation.setExecutable(this.cloneHelper.clone(invocation.getExecutable()));
		aCtInvocation.setArguments(this.cloneHelper.clone(invocation.getArguments()));
		aCtInvocation.setComments(this.cloneHelper.clone(invocation.getComments()));
		this.builder.copy(invocation, aCtInvocation);
		this.cloneHelper.tailor(invocation, aCtInvocation);
		this.other = aCtInvocation;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtLiteral(final spoon.reflect.code.CtLiteral<T> literal) {
		spoon.reflect.code.CtLiteral<T> aCtLiteral = literal.getFactory().Core().createLiteral();
		aCtLiteral.setAnnotations(this.cloneHelper.clone(literal.getAnnotations()));
		aCtLiteral.setType(this.cloneHelper.clone(literal.getType()));
		aCtLiteral.setTypeCasts(this.cloneHelper.clone(literal.getTypeCasts()));
		aCtLiteral.setComments(this.cloneHelper.clone(literal.getComments()));
		this.builder.copy(literal, aCtLiteral);
		this.cloneHelper.tailor(literal, aCtLiteral);
		this.other = aCtLiteral;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtLocalVariable(final spoon.reflect.code.CtLocalVariable<T> localVariable) {
		spoon.reflect.code.CtLocalVariable<T> aCtLocalVariable = localVariable.getFactory().Core().createLocalVariable();
		aCtLocalVariable.setAnnotations(this.cloneHelper.clone(localVariable.getAnnotations()));
		aCtLocalVariable.setType(this.cloneHelper.clone(localVariable.getType()));
		aCtLocalVariable.setDefaultExpression(this.cloneHelper.clone(localVariable.getDefaultExpression()));
		aCtLocalVariable.setComments(this.cloneHelper.clone(localVariable.getComments()));
		this.builder.copy(localVariable, aCtLocalVariable);
		this.cloneHelper.tailor(localVariable, aCtLocalVariable);
		this.other = aCtLocalVariable;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtLocalVariableReference(final spoon.reflect.reference.CtLocalVariableReference<T> reference) {
		spoon.reflect.reference.CtLocalVariableReference<T> aCtLocalVariableReference = reference.getFactory().Core().createLocalVariableReference();
		aCtLocalVariableReference.setType(this.cloneHelper.clone(reference.getType()));
		aCtLocalVariableReference.setAnnotations(this.cloneHelper.clone(reference.getAnnotations()));
		this.builder.copy(reference, aCtLocalVariableReference);
		this.cloneHelper.tailor(reference, aCtLocalVariableReference);
		this.other = aCtLocalVariableReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtCatchVariable(final spoon.reflect.code.CtCatchVariable<T> catchVariable) {
		spoon.reflect.code.CtCatchVariable<T> aCtCatchVariable = catchVariable.getFactory().Core().createCatchVariable();
		aCtCatchVariable.setComments(this.cloneHelper.clone(catchVariable.getComments()));
		aCtCatchVariable.setAnnotations(this.cloneHelper.clone(catchVariable.getAnnotations()));
		aCtCatchVariable.setMultiTypes(this.cloneHelper.clone(catchVariable.getMultiTypes()));
		this.builder.copy(catchVariable, aCtCatchVariable);
		this.cloneHelper.tailor(catchVariable, aCtCatchVariable);
		this.other = aCtCatchVariable;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtCatchVariableReference(final spoon.reflect.reference.CtCatchVariableReference<T> reference) {
		spoon.reflect.reference.CtCatchVariableReference<T> aCtCatchVariableReference = reference.getFactory().Core().createCatchVariableReference();
		aCtCatchVariableReference.setType(this.cloneHelper.clone(reference.getType()));
		aCtCatchVariableReference.setAnnotations(this.cloneHelper.clone(reference.getAnnotations()));
		this.builder.copy(reference, aCtCatchVariableReference);
		this.cloneHelper.tailor(reference, aCtCatchVariableReference);
		this.other = aCtCatchVariableReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtMethod(final spoon.reflect.declaration.CtMethod<T> m) {
		spoon.reflect.declaration.CtMethod<T> aCtMethod = m.getFactory().Core().createMethod();
		aCtMethod.setAnnotations(this.cloneHelper.clone(m.getAnnotations()));
		aCtMethod.setFormalCtTypeParameters(this.cloneHelper.clone(m.getFormalCtTypeParameters()));
		aCtMethod.setType(this.cloneHelper.clone(m.getType()));
		aCtMethod.setParameters(this.cloneHelper.clone(m.getParameters()));
		aCtMethod.setThrownTypes(this.cloneHelper.clone(m.getThrownTypes()));
		aCtMethod.setBody(this.cloneHelper.clone(m.getBody()));
		aCtMethod.setComments(this.cloneHelper.clone(m.getComments()));
		this.builder.copy(m, aCtMethod);
		this.cloneHelper.tailor(m, aCtMethod);
		this.other = aCtMethod;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtAnnotationMethod(spoon.reflect.declaration.CtAnnotationMethod<T> annotationMethod) {
		spoon.reflect.declaration.CtAnnotationMethod<T> aCtAnnotationMethod = annotationMethod.getFactory().Core().createAnnotationMethod();
		aCtAnnotationMethod.setAnnotations(this.cloneHelper.clone(annotationMethod.getAnnotations()));
		aCtAnnotationMethod.setType(this.cloneHelper.clone(annotationMethod.getType()));
		aCtAnnotationMethod.setDefaultExpression(this.cloneHelper.clone(annotationMethod.getDefaultExpression()));
		aCtAnnotationMethod.setComments(this.cloneHelper.clone(annotationMethod.getComments()));
		this.builder.copy(annotationMethod, aCtAnnotationMethod);
		this.cloneHelper.tailor(annotationMethod, aCtAnnotationMethod);
		this.other = aCtAnnotationMethod;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtNewArray(final spoon.reflect.code.CtNewArray<T> newArray) {
		spoon.reflect.code.CtNewArray<T> aCtNewArray = newArray.getFactory().Core().createNewArray();
		aCtNewArray.setAnnotations(this.cloneHelper.clone(newArray.getAnnotations()));
		aCtNewArray.setType(this.cloneHelper.clone(newArray.getType()));
		aCtNewArray.setTypeCasts(this.cloneHelper.clone(newArray.getTypeCasts()));
		aCtNewArray.setElements(this.cloneHelper.clone(newArray.getElements()));
		aCtNewArray.setDimensionExpressions(this.cloneHelper.clone(newArray.getDimensionExpressions()));
		aCtNewArray.setComments(this.cloneHelper.clone(newArray.getComments()));
		this.builder.copy(newArray, aCtNewArray);
		this.cloneHelper.tailor(newArray, aCtNewArray);
		this.other = aCtNewArray;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtConstructorCall(final spoon.reflect.code.CtConstructorCall<T> ctConstructorCall) {
		spoon.reflect.code.CtConstructorCall<T> aCtConstructorCall = ctConstructorCall.getFactory().Core().createConstructorCall();
		aCtConstructorCall.setAnnotations(this.cloneHelper.clone(ctConstructorCall.getAnnotations()));
		aCtConstructorCall.setTypeCasts(this.cloneHelper.clone(ctConstructorCall.getTypeCasts()));
		aCtConstructorCall.setExecutable(this.cloneHelper.clone(ctConstructorCall.getExecutable()));
		aCtConstructorCall.setTarget(this.cloneHelper.clone(ctConstructorCall.getTarget()));
		aCtConstructorCall.setArguments(this.cloneHelper.clone(ctConstructorCall.getArguments()));
		aCtConstructorCall.setComments(this.cloneHelper.clone(ctConstructorCall.getComments()));
		this.builder.copy(ctConstructorCall, aCtConstructorCall);
		this.cloneHelper.tailor(ctConstructorCall, aCtConstructorCall);
		this.other = aCtConstructorCall;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtNewClass(final spoon.reflect.code.CtNewClass<T> newClass) {
		spoon.reflect.code.CtNewClass<T> aCtNewClass = newClass.getFactory().Core().createNewClass();
		aCtNewClass.setAnnotations(this.cloneHelper.clone(newClass.getAnnotations()));
		aCtNewClass.setTypeCasts(this.cloneHelper.clone(newClass.getTypeCasts()));
		aCtNewClass.setExecutable(this.cloneHelper.clone(newClass.getExecutable()));
		aCtNewClass.setTarget(this.cloneHelper.clone(newClass.getTarget()));
		aCtNewClass.setArguments(this.cloneHelper.clone(newClass.getArguments()));
		aCtNewClass.setAnonymousClass(this.cloneHelper.clone(newClass.getAnonymousClass()));
		aCtNewClass.setComments(this.cloneHelper.clone(newClass.getComments()));
		this.builder.copy(newClass, aCtNewClass);
		this.cloneHelper.tailor(newClass, aCtNewClass);
		this.other = aCtNewClass;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtLambda(final spoon.reflect.code.CtLambda<T> lambda) {
		spoon.reflect.code.CtLambda<T> aCtLambda = lambda.getFactory().Core().createLambda();
		aCtLambda.setAnnotations(this.cloneHelper.clone(lambda.getAnnotations()));
		aCtLambda.setType(this.cloneHelper.clone(lambda.getType()));
		aCtLambda.setTypeCasts(this.cloneHelper.clone(lambda.getTypeCasts()));
		aCtLambda.setParameters(this.cloneHelper.clone(lambda.getParameters()));
		aCtLambda.setBody(this.cloneHelper.clone(lambda.getBody()));
		aCtLambda.setExpression(this.cloneHelper.clone(lambda.getExpression()));
		aCtLambda.setComments(this.cloneHelper.clone(lambda.getComments()));
		this.builder.copy(lambda, aCtLambda);
		this.cloneHelper.tailor(lambda, aCtLambda);
		this.other = aCtLambda;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T, E extends spoon.reflect.code.CtExpression<?>> void visitCtExecutableReferenceExpression(final spoon.reflect.code.CtExecutableReferenceExpression<T, E> expression) {
		spoon.reflect.code.CtExecutableReferenceExpression<T, E> aCtExecutableReferenceExpression = expression.getFactory().Core().createExecutableReferenceExpression();
		aCtExecutableReferenceExpression.setComments(this.cloneHelper.clone(expression.getComments()));
		aCtExecutableReferenceExpression.setAnnotations(this.cloneHelper.clone(expression.getAnnotations()));
		aCtExecutableReferenceExpression.setType(this.cloneHelper.clone(expression.getType()));
		aCtExecutableReferenceExpression.setTypeCasts(this.cloneHelper.clone(expression.getTypeCasts()));
		aCtExecutableReferenceExpression.setExecutable(this.cloneHelper.clone(expression.getExecutable()));
		aCtExecutableReferenceExpression.setTarget(this.cloneHelper.clone(expression.getTarget()));
		this.builder.copy(expression, aCtExecutableReferenceExpression);
		this.cloneHelper.tailor(expression, aCtExecutableReferenceExpression);
		this.other = aCtExecutableReferenceExpression;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T, A extends T> void visitCtOperatorAssignment(final spoon.reflect.code.CtOperatorAssignment<T, A> assignment) {
		spoon.reflect.code.CtOperatorAssignment<T, A> aCtOperatorAssignment = assignment.getFactory().Core().createOperatorAssignment();
		aCtOperatorAssignment.setAnnotations(this.cloneHelper.clone(assignment.getAnnotations()));
		aCtOperatorAssignment.setType(this.cloneHelper.clone(assignment.getType()));
		aCtOperatorAssignment.setTypeCasts(this.cloneHelper.clone(assignment.getTypeCasts()));
		aCtOperatorAssignment.setAssigned(this.cloneHelper.clone(assignment.getAssigned()));
		aCtOperatorAssignment.setAssignment(this.cloneHelper.clone(assignment.getAssignment()));
		aCtOperatorAssignment.setComments(this.cloneHelper.clone(assignment.getComments()));
		this.builder.copy(assignment, aCtOperatorAssignment);
		this.cloneHelper.tailor(assignment, aCtOperatorAssignment);
		this.other = aCtOperatorAssignment;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtPackage(final spoon.reflect.declaration.CtPackage ctPackage) {
		spoon.reflect.declaration.CtPackage aCtPackage = ctPackage.getFactory().Core().createPackage();
		aCtPackage.setAnnotations(this.cloneHelper.clone(ctPackage.getAnnotations()));
		aCtPackage.setPackages(this.cloneHelper.clone(ctPackage.getPackages()));
		aCtPackage.setTypes(this.cloneHelper.clone(ctPackage.getTypes()));
		aCtPackage.setComments(this.cloneHelper.clone(ctPackage.getComments()));
		this.builder.copy(ctPackage, aCtPackage);
		this.cloneHelper.tailor(ctPackage, aCtPackage);
		this.other = aCtPackage;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtPackageReference(final spoon.reflect.reference.CtPackageReference reference) {
		spoon.reflect.reference.CtPackageReference aCtPackageReference = reference.getFactory().Core().createPackageReference();
		aCtPackageReference.setAnnotations(this.cloneHelper.clone(reference.getAnnotations()));
		this.builder.copy(reference, aCtPackageReference);
		this.cloneHelper.tailor(reference, aCtPackageReference);
		this.other = aCtPackageReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtParameter(final spoon.reflect.declaration.CtParameter<T> parameter) {
		spoon.reflect.declaration.CtParameter<T> aCtParameter = parameter.getFactory().Core().createParameter();
		aCtParameter.setAnnotations(this.cloneHelper.clone(parameter.getAnnotations()));
		aCtParameter.setType(this.cloneHelper.clone(parameter.getType()));
		aCtParameter.setComments(this.cloneHelper.clone(parameter.getComments()));
		this.builder.copy(parameter, aCtParameter);
		this.cloneHelper.tailor(parameter, aCtParameter);
		this.other = aCtParameter;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtParameterReference(final spoon.reflect.reference.CtParameterReference<T> reference) {
		spoon.reflect.reference.CtParameterReference<T> aCtParameterReference = reference.getFactory().Core().createParameterReference();
		aCtParameterReference.setType(this.cloneHelper.clone(reference.getType()));
		aCtParameterReference.setAnnotations(this.cloneHelper.clone(reference.getAnnotations()));
		this.builder.copy(reference, aCtParameterReference);
		this.cloneHelper.tailor(reference, aCtParameterReference);
		this.other = aCtParameterReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <R> void visitCtReturn(final spoon.reflect.code.CtReturn<R> returnStatement) {
		spoon.reflect.code.CtReturn<R> aCtReturn = returnStatement.getFactory().Core().createReturn();
		aCtReturn.setAnnotations(this.cloneHelper.clone(returnStatement.getAnnotations()));
		aCtReturn.setReturnedExpression(this.cloneHelper.clone(returnStatement.getReturnedExpression()));
		aCtReturn.setComments(this.cloneHelper.clone(returnStatement.getComments()));
		this.builder.copy(returnStatement, aCtReturn);
		this.cloneHelper.tailor(returnStatement, aCtReturn);
		this.other = aCtReturn;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <R> void visitCtStatementList(final spoon.reflect.code.CtStatementList statements) {
		spoon.reflect.code.CtStatementList aCtStatementList = statements.getFactory().Core().createStatementList();
		aCtStatementList.setAnnotations(this.cloneHelper.clone(statements.getAnnotations()));
		aCtStatementList.setStatements(this.cloneHelper.clone(statements.getStatements()));
		aCtStatementList.setComments(this.cloneHelper.clone(statements.getComments()));
		this.builder.copy(statements, aCtStatementList);
		this.cloneHelper.tailor(statements, aCtStatementList);
		this.other = aCtStatementList;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <S> void visitCtSwitch(final spoon.reflect.code.CtSwitch<S> switchStatement) {
		spoon.reflect.code.CtSwitch<S> aCtSwitch = switchStatement.getFactory().Core().createSwitch();
		aCtSwitch.setAnnotations(this.cloneHelper.clone(switchStatement.getAnnotations()));
		aCtSwitch.setSelector(this.cloneHelper.clone(switchStatement.getSelector()));
		aCtSwitch.setCases(this.cloneHelper.clone(switchStatement.getCases()));
		aCtSwitch.setComments(this.cloneHelper.clone(switchStatement.getComments()));
		this.builder.copy(switchStatement, aCtSwitch);
		this.cloneHelper.tailor(switchStatement, aCtSwitch);
		this.other = aCtSwitch;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtSynchronized(final spoon.reflect.code.CtSynchronized synchro) {
		spoon.reflect.code.CtSynchronized aCtSynchronized = synchro.getFactory().Core().createSynchronized();
		aCtSynchronized.setAnnotations(this.cloneHelper.clone(synchro.getAnnotations()));
		aCtSynchronized.setExpression(this.cloneHelper.clone(synchro.getExpression()));
		aCtSynchronized.setBlock(this.cloneHelper.clone(synchro.getBlock()));
		aCtSynchronized.setComments(this.cloneHelper.clone(synchro.getComments()));
		this.builder.copy(synchro, aCtSynchronized);
		this.cloneHelper.tailor(synchro, aCtSynchronized);
		this.other = aCtSynchronized;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtThrow(final spoon.reflect.code.CtThrow throwStatement) {
		spoon.reflect.code.CtThrow aCtThrow = throwStatement.getFactory().Core().createThrow();
		aCtThrow.setAnnotations(this.cloneHelper.clone(throwStatement.getAnnotations()));
		aCtThrow.setThrownExpression(this.cloneHelper.clone(throwStatement.getThrownExpression()));
		aCtThrow.setComments(this.cloneHelper.clone(throwStatement.getComments()));
		this.builder.copy(throwStatement, aCtThrow);
		this.cloneHelper.tailor(throwStatement, aCtThrow);
		this.other = aCtThrow;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtTry(final spoon.reflect.code.CtTry tryBlock) {
		spoon.reflect.code.CtTry aCtTry = tryBlock.getFactory().Core().createTry();
		aCtTry.setAnnotations(this.cloneHelper.clone(tryBlock.getAnnotations()));
		aCtTry.setBody(this.cloneHelper.clone(tryBlock.getBody()));
		aCtTry.setCatchers(this.cloneHelper.clone(tryBlock.getCatchers()));
		aCtTry.setFinalizer(this.cloneHelper.clone(tryBlock.getFinalizer()));
		aCtTry.setComments(this.cloneHelper.clone(tryBlock.getComments()));
		this.builder.copy(tryBlock, aCtTry);
		this.cloneHelper.tailor(tryBlock, aCtTry);
		this.other = aCtTry;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtTryWithResource(final spoon.reflect.code.CtTryWithResource tryWithResource) {
		spoon.reflect.code.CtTryWithResource aCtTryWithResource = tryWithResource.getFactory().Core().createTryWithResource();
		aCtTryWithResource.setAnnotations(this.cloneHelper.clone(tryWithResource.getAnnotations()));
		aCtTryWithResource.setResources(this.cloneHelper.clone(tryWithResource.getResources()));
		aCtTryWithResource.setBody(this.cloneHelper.clone(tryWithResource.getBody()));
		aCtTryWithResource.setCatchers(this.cloneHelper.clone(tryWithResource.getCatchers()));
		aCtTryWithResource.setFinalizer(this.cloneHelper.clone(tryWithResource.getFinalizer()));
		aCtTryWithResource.setComments(this.cloneHelper.clone(tryWithResource.getComments()));
		this.builder.copy(tryWithResource, aCtTryWithResource);
		this.cloneHelper.tailor(tryWithResource, aCtTryWithResource);
		this.other = aCtTryWithResource;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtTypeParameterReference(final spoon.reflect.reference.CtTypeParameterReference ref) {
		spoon.reflect.reference.CtTypeParameterReference aCtTypeParameterReference = ref.getFactory().Core().createTypeParameterReference();
		aCtTypeParameterReference.setPackage(this.cloneHelper.clone(ref.getPackage()));
		aCtTypeParameterReference.setDeclaringType(this.cloneHelper.clone(ref.getDeclaringType()));
		aCtTypeParameterReference.setAnnotations(this.cloneHelper.clone(ref.getAnnotations()));
		this.builder.copy(ref, aCtTypeParameterReference);
		this.cloneHelper.tailor(ref, aCtTypeParameterReference);
		this.other = aCtTypeParameterReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtWildcardReference(spoon.reflect.reference.CtWildcardReference wildcardReference) {
		spoon.reflect.reference.CtWildcardReference aCtWildcardReference = wildcardReference.getFactory().Core().createWildcardReference();
		aCtWildcardReference.setPackage(this.cloneHelper.clone(wildcardReference.getPackage()));
		aCtWildcardReference.setDeclaringType(this.cloneHelper.clone(wildcardReference.getDeclaringType()));
		aCtWildcardReference.setAnnotations(this.cloneHelper.clone(wildcardReference.getAnnotations()));
		aCtWildcardReference.setBoundingType(this.cloneHelper.clone(wildcardReference.getBoundingType()));
		this.builder.copy(wildcardReference, aCtWildcardReference);
		this.cloneHelper.tailor(wildcardReference, aCtWildcardReference);
		this.other = aCtWildcardReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtIntersectionTypeReference(final spoon.reflect.reference.CtIntersectionTypeReference<T> reference) {
		spoon.reflect.reference.CtIntersectionTypeReference<T> aCtIntersectionTypeReference = reference.getFactory().Core().createIntersectionTypeReference();
		aCtIntersectionTypeReference.setPackage(this.cloneHelper.clone(reference.getPackage()));
		aCtIntersectionTypeReference.setDeclaringType(this.cloneHelper.clone(reference.getDeclaringType()));
		aCtIntersectionTypeReference.setActualTypeArguments(this.cloneHelper.clone(reference.getActualTypeArguments()));
		aCtIntersectionTypeReference.setAnnotations(this.cloneHelper.clone(reference.getAnnotations()));
		aCtIntersectionTypeReference.setBounds(this.cloneHelper.clone(reference.getBounds()));
		this.builder.copy(reference, aCtIntersectionTypeReference);
		this.cloneHelper.tailor(reference, aCtIntersectionTypeReference);
		this.other = aCtIntersectionTypeReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtTypeReference(final spoon.reflect.reference.CtTypeReference<T> reference) {
		spoon.reflect.reference.CtTypeReference<T> aCtTypeReference = reference.getFactory().Core().createTypeReference();
		aCtTypeReference.setPackage(this.cloneHelper.clone(reference.getPackage()));
		aCtTypeReference.setDeclaringType(this.cloneHelper.clone(reference.getDeclaringType()));
		aCtTypeReference.setActualTypeArguments(this.cloneHelper.clone(reference.getActualTypeArguments()));
		aCtTypeReference.setAnnotations(this.cloneHelper.clone(reference.getAnnotations()));
		aCtTypeReference.setComments(this.cloneHelper.clone(reference.getComments()));
		this.builder.copy(reference, aCtTypeReference);
		this.cloneHelper.tailor(reference, aCtTypeReference);
		this.other = aCtTypeReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtTypeAccess(final spoon.reflect.code.CtTypeAccess<T> typeAccess) {
		spoon.reflect.code.CtTypeAccess<T> aCtTypeAccess = typeAccess.getFactory().Core().createTypeAccess();
		aCtTypeAccess.setAnnotations(this.cloneHelper.clone(typeAccess.getAnnotations()));
		aCtTypeAccess.setTypeCasts(this.cloneHelper.clone(typeAccess.getTypeCasts()));
		aCtTypeAccess.setAccessedType(this.cloneHelper.clone(typeAccess.getAccessedType()));
		aCtTypeAccess.setComments(this.cloneHelper.clone(typeAccess.getComments()));
		this.builder.copy(typeAccess, aCtTypeAccess);
		this.cloneHelper.tailor(typeAccess, aCtTypeAccess);
		this.other = aCtTypeAccess;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtUnaryOperator(final spoon.reflect.code.CtUnaryOperator<T> operator) {
		spoon.reflect.code.CtUnaryOperator<T> aCtUnaryOperator = operator.getFactory().Core().createUnaryOperator();
		aCtUnaryOperator.setAnnotations(this.cloneHelper.clone(operator.getAnnotations()));
		aCtUnaryOperator.setType(this.cloneHelper.clone(operator.getType()));
		aCtUnaryOperator.setTypeCasts(this.cloneHelper.clone(operator.getTypeCasts()));
		aCtUnaryOperator.setOperand(this.cloneHelper.clone(operator.getOperand()));
		aCtUnaryOperator.setComments(this.cloneHelper.clone(operator.getComments()));
		this.builder.copy(operator, aCtUnaryOperator);
		this.cloneHelper.tailor(operator, aCtUnaryOperator);
		this.other = aCtUnaryOperator;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtVariableRead(final spoon.reflect.code.CtVariableRead<T> variableRead) {
		spoon.reflect.code.CtVariableRead<T> aCtVariableRead = variableRead.getFactory().Core().createVariableRead();
		aCtVariableRead.setAnnotations(this.cloneHelper.clone(variableRead.getAnnotations()));
		aCtVariableRead.setTypeCasts(this.cloneHelper.clone(variableRead.getTypeCasts()));
		aCtVariableRead.setVariable(this.cloneHelper.clone(variableRead.getVariable()));
		aCtVariableRead.setComments(this.cloneHelper.clone(variableRead.getComments()));
		this.builder.copy(variableRead, aCtVariableRead);
		this.cloneHelper.tailor(variableRead, aCtVariableRead);
		this.other = aCtVariableRead;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtVariableWrite(final spoon.reflect.code.CtVariableWrite<T> variableWrite) {
		spoon.reflect.code.CtVariableWrite<T> aCtVariableWrite = variableWrite.getFactory().Core().createVariableWrite();
		aCtVariableWrite.setAnnotations(this.cloneHelper.clone(variableWrite.getAnnotations()));
		aCtVariableWrite.setTypeCasts(this.cloneHelper.clone(variableWrite.getTypeCasts()));
		aCtVariableWrite.setVariable(this.cloneHelper.clone(variableWrite.getVariable()));
		aCtVariableWrite.setComments(this.cloneHelper.clone(variableWrite.getComments()));
		this.builder.copy(variableWrite, aCtVariableWrite);
		this.cloneHelper.tailor(variableWrite, aCtVariableWrite);
		this.other = aCtVariableWrite;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtWhile(final spoon.reflect.code.CtWhile whileLoop) {
		spoon.reflect.code.CtWhile aCtWhile = whileLoop.getFactory().Core().createWhile();
		aCtWhile.setAnnotations(this.cloneHelper.clone(whileLoop.getAnnotations()));
		aCtWhile.setLoopingExpression(this.cloneHelper.clone(whileLoop.getLoopingExpression()));
		aCtWhile.setBody(this.cloneHelper.clone(whileLoop.getBody()));
		aCtWhile.setComments(this.cloneHelper.clone(whileLoop.getComments()));
		this.builder.copy(whileLoop, aCtWhile);
		this.cloneHelper.tailor(whileLoop, aCtWhile);
		this.other = aCtWhile;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtCodeSnippetExpression(final spoon.reflect.code.CtCodeSnippetExpression<T> expression) {
		spoon.reflect.code.CtCodeSnippetExpression<T> aCtCodeSnippetExpression = expression.getFactory().Core().createCodeSnippetExpression();
		aCtCodeSnippetExpression.setType(this.cloneHelper.clone(expression.getType()));
		aCtCodeSnippetExpression.setComments(this.cloneHelper.clone(expression.getComments()));
		aCtCodeSnippetExpression.setAnnotations(this.cloneHelper.clone(expression.getAnnotations()));
		aCtCodeSnippetExpression.setTypeCasts(this.cloneHelper.clone(expression.getTypeCasts()));
		this.builder.copy(expression, aCtCodeSnippetExpression);
		this.cloneHelper.tailor(expression, aCtCodeSnippetExpression);
		this.other = aCtCodeSnippetExpression;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtCodeSnippetStatement(final spoon.reflect.code.CtCodeSnippetStatement statement) {
		spoon.reflect.code.CtCodeSnippetStatement aCtCodeSnippetStatement = statement.getFactory().Core().createCodeSnippetStatement();
		aCtCodeSnippetStatement.setComments(this.cloneHelper.clone(statement.getComments()));
		aCtCodeSnippetStatement.setAnnotations(this.cloneHelper.clone(statement.getAnnotations()));
		this.builder.copy(statement, aCtCodeSnippetStatement);
		this.cloneHelper.tailor(statement, aCtCodeSnippetStatement);
		this.other = aCtCodeSnippetStatement;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtUnboundVariableReference(final spoon.reflect.reference.CtUnboundVariableReference<T> reference) {
		spoon.reflect.reference.CtUnboundVariableReference<T> aCtUnboundVariableReference = reference.getFactory().Core().createUnboundVariableReference();
		aCtUnboundVariableReference.setType(this.cloneHelper.clone(reference.getType()));
		this.builder.copy(reference, aCtUnboundVariableReference);
		this.cloneHelper.tailor(reference, aCtUnboundVariableReference);
		this.other = aCtUnboundVariableReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtFieldRead(final spoon.reflect.code.CtFieldRead<T> fieldRead) {
		spoon.reflect.code.CtFieldRead<T> aCtFieldRead = fieldRead.getFactory().Core().createFieldRead();
		aCtFieldRead.setAnnotations(this.cloneHelper.clone(fieldRead.getAnnotations()));
		aCtFieldRead.setTypeCasts(this.cloneHelper.clone(fieldRead.getTypeCasts()));
		aCtFieldRead.setTarget(this.cloneHelper.clone(fieldRead.getTarget()));
		aCtFieldRead.setVariable(this.cloneHelper.clone(fieldRead.getVariable()));
		aCtFieldRead.setComments(this.cloneHelper.clone(fieldRead.getComments()));
		this.builder.copy(fieldRead, aCtFieldRead);
		this.cloneHelper.tailor(fieldRead, aCtFieldRead);
		this.other = aCtFieldRead;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtFieldWrite(final spoon.reflect.code.CtFieldWrite<T> fieldWrite) {
		spoon.reflect.code.CtFieldWrite<T> aCtFieldWrite = fieldWrite.getFactory().Core().createFieldWrite();
		aCtFieldWrite.setAnnotations(this.cloneHelper.clone(fieldWrite.getAnnotations()));
		aCtFieldWrite.setTypeCasts(this.cloneHelper.clone(fieldWrite.getTypeCasts()));
		aCtFieldWrite.setTarget(this.cloneHelper.clone(fieldWrite.getTarget()));
		aCtFieldWrite.setVariable(this.cloneHelper.clone(fieldWrite.getVariable()));
		aCtFieldWrite.setComments(this.cloneHelper.clone(fieldWrite.getComments()));
		this.builder.copy(fieldWrite, aCtFieldWrite);
		this.cloneHelper.tailor(fieldWrite, aCtFieldWrite);
		this.other = aCtFieldWrite;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtSuperAccess(final spoon.reflect.code.CtSuperAccess<T> f) {
		spoon.reflect.code.CtSuperAccess<T> aCtSuperAccess = f.getFactory().Core().createSuperAccess();
		aCtSuperAccess.setComments(this.cloneHelper.clone(f.getComments()));
		aCtSuperAccess.setAnnotations(this.cloneHelper.clone(f.getAnnotations()));
		aCtSuperAccess.setTypeCasts(this.cloneHelper.clone(f.getTypeCasts()));
		aCtSuperAccess.setTarget(this.cloneHelper.clone(f.getTarget()));
		aCtSuperAccess.setVariable(this.cloneHelper.clone(f.getVariable()));
		this.builder.copy(f, aCtSuperAccess);
		this.cloneHelper.tailor(f, aCtSuperAccess);
		this.other = aCtSuperAccess;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtComment(final spoon.reflect.code.CtComment comment) {
		spoon.reflect.code.CtComment aCtComment = comment.getFactory().Core().createComment();
		aCtComment.setComments(this.cloneHelper.clone(comment.getComments()));
		aCtComment.setAnnotations(this.cloneHelper.clone(comment.getAnnotations()));
		this.builder.copy(comment, aCtComment);
		this.cloneHelper.tailor(comment, aCtComment);
		this.other = aCtComment;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtJavaDoc(final spoon.reflect.code.CtJavaDoc javaDoc) {
		spoon.reflect.code.CtJavaDoc aCtJavaDoc = javaDoc.getFactory().Core().createJavaDoc();
		aCtJavaDoc.setComments(this.cloneHelper.clone(javaDoc.getComments()));
		aCtJavaDoc.setAnnotations(this.cloneHelper.clone(javaDoc.getAnnotations()));
		aCtJavaDoc.setTags(this.cloneHelper.clone(javaDoc.getTags()));
		this.builder.copy(javaDoc, aCtJavaDoc);
		this.cloneHelper.tailor(javaDoc, aCtJavaDoc);
		this.other = aCtJavaDoc;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtJavaDocTag(final spoon.reflect.code.CtJavaDocTag docTag) {
		spoon.reflect.code.CtJavaDocTag aCtJavaDocTag = docTag.getFactory().Core().createJavaDocTag();
		aCtJavaDocTag.setComments(this.cloneHelper.clone(docTag.getComments()));
		aCtJavaDocTag.setAnnotations(this.cloneHelper.clone(docTag.getAnnotations()));
		this.builder.copy(docTag, aCtJavaDocTag);
		this.cloneHelper.tailor(docTag, aCtJavaDocTag);
		this.other = aCtJavaDocTag;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtImport(final spoon.reflect.declaration.CtImport ctImport) {
		spoon.reflect.declaration.CtImport aCtImport = ctImport.getFactory().Core().createImport();
		aCtImport.setReference(this.cloneHelper.clone(ctImport.getReference()));
		aCtImport.setAnnotations(this.cloneHelper.clone(ctImport.getAnnotations()));
		aCtImport.setComments(this.cloneHelper.clone(ctImport.getComments()));
		this.builder.copy(ctImport, aCtImport);
		this.cloneHelper.tailor(ctImport, aCtImport);
		this.other = aCtImport;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtModule(spoon.reflect.declaration.CtModule module) {
		spoon.reflect.declaration.CtModule aCtModule = module.getFactory().Core().createModule();
		aCtModule.setComments(this.cloneHelper.clone(module.getComments()));
		aCtModule.setAnnotations(this.cloneHelper.clone(module.getAnnotations()));
		aCtModule.setModuleDirectives(this.cloneHelper.clone(module.getModuleDirectives()));
		aCtModule.setRootPackage(this.cloneHelper.clone(module.getRootPackage()));
		this.builder.copy(module, aCtModule);
		this.cloneHelper.tailor(module, aCtModule);
		this.other = aCtModule;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtModuleReference(spoon.reflect.reference.CtModuleReference moduleReference) {
		spoon.reflect.reference.CtModuleReference aCtModuleReference = moduleReference.getFactory().Core().createModuleReference();
		aCtModuleReference.setAnnotations(this.cloneHelper.clone(moduleReference.getAnnotations()));
		this.builder.copy(moduleReference, aCtModuleReference);
		this.cloneHelper.tailor(moduleReference, aCtModuleReference);
		this.other = aCtModuleReference;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtPackageExport(spoon.reflect.declaration.CtPackageExport moduleExport) {
		spoon.reflect.declaration.CtPackageExport aCtPackageExport = moduleExport.getFactory().Core().createPackageExport();
		aCtPackageExport.setComments(this.cloneHelper.clone(moduleExport.getComments()));
		aCtPackageExport.setPackageReference(this.cloneHelper.clone(moduleExport.getPackageReference()));
		aCtPackageExport.setTargetExport(this.cloneHelper.clone(moduleExport.getTargetExport()));
		aCtPackageExport.setAnnotations(this.cloneHelper.clone(moduleExport.getAnnotations()));
		this.builder.copy(moduleExport, aCtPackageExport);
		this.cloneHelper.tailor(moduleExport, aCtPackageExport);
		this.other = aCtPackageExport;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtModuleRequirement(spoon.reflect.declaration.CtModuleRequirement moduleRequirement) {
		spoon.reflect.declaration.CtModuleRequirement aCtModuleRequirement = moduleRequirement.getFactory().Core().createModuleRequirement();
		aCtModuleRequirement.setComments(this.cloneHelper.clone(moduleRequirement.getComments()));
		aCtModuleRequirement.setModuleReference(this.cloneHelper.clone(moduleRequirement.getModuleReference()));
		aCtModuleRequirement.setAnnotations(this.cloneHelper.clone(moduleRequirement.getAnnotations()));
		this.builder.copy(moduleRequirement, aCtModuleRequirement);
		this.cloneHelper.tailor(moduleRequirement, aCtModuleRequirement);
		this.other = aCtModuleRequirement;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtProvidedService(spoon.reflect.declaration.CtProvidedService moduleProvidedService) {
		spoon.reflect.declaration.CtProvidedService aCtProvidedService = moduleProvidedService.getFactory().Core().createProvidedService();
		aCtProvidedService.setComments(this.cloneHelper.clone(moduleProvidedService.getComments()));
		aCtProvidedService.setServiceType(this.cloneHelper.clone(moduleProvidedService.getServiceType()));
		aCtProvidedService.setImplementationTypes(this.cloneHelper.clone(moduleProvidedService.getImplementationTypes()));
		aCtProvidedService.setAnnotations(this.cloneHelper.clone(moduleProvidedService.getAnnotations()));
		this.builder.copy(moduleProvidedService, aCtProvidedService);
		this.cloneHelper.tailor(moduleProvidedService, aCtProvidedService);
		this.other = aCtProvidedService;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtUsedService(spoon.reflect.declaration.CtUsedService usedService) {
		spoon.reflect.declaration.CtUsedService aCtUsedService = usedService.getFactory().Core().createUsedService();
		aCtUsedService.setComments(this.cloneHelper.clone(usedService.getComments()));
		aCtUsedService.setServiceType(this.cloneHelper.clone(usedService.getServiceType()));
		aCtUsedService.setAnnotations(this.cloneHelper.clone(usedService.getAnnotations()));
		this.builder.copy(usedService, aCtUsedService);
		this.cloneHelper.tailor(usedService, aCtUsedService);
		this.other = aCtUsedService;
	}
}

