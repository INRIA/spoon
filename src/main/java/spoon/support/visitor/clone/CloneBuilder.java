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
 * Used to set all data in the cloned element.
 *
 * This class is generated automatically by the processor {@link spoon.generating.CloneVisitorGenerator}.
 */
public class CloneBuilder extends spoon.reflect.visitor.CtInheritanceScanner {
	public static <T extends spoon.reflect.declaration.CtElement> T build(spoon.reflect.declaration.CtElement element, spoon.reflect.declaration.CtElement other) {
		return spoon.support.visitor.clone.CloneBuilder.build(new spoon.support.visitor.clone.CloneBuilder(), element, other);
	}

	public static <T extends spoon.reflect.declaration.CtElement> T build(spoon.support.visitor.clone.CloneBuilder builder, spoon.reflect.declaration.CtElement element, spoon.reflect.declaration.CtElement other) {
		builder.setOther(other);
		builder.scan(element);
		return ((T) (builder.other));
	}

	private spoon.reflect.declaration.CtElement other;

	public void setOther(spoon.reflect.declaration.CtElement other) {
		this.other = other;
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtCodeSnippetExpression(spoon.reflect.code.CtCodeSnippetExpression<T> e) {
		((spoon.reflect.code.CtCodeSnippetExpression<T>) (other)).setValue(e.getValue());
		super.visitCtCodeSnippetExpression(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtCodeSnippetStatement(spoon.reflect.code.CtCodeSnippetStatement e) {
		((spoon.reflect.code.CtCodeSnippetStatement) (other)).setValue(e.getValue());
		super.visitCtCodeSnippetStatement(e);
	}

	/**
	 * Scans an abstract element.
	 */
	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void scanCtElement(spoon.reflect.declaration.CtElement e) {
		((spoon.reflect.declaration.CtElement) (other)).setPosition(e.getPosition());
		((spoon.reflect.declaration.CtElement) (other)).setImplicit(e.isImplicit());
		super.scanCtElement(e);
	}

	/**
	 * Scans an abstract named element.
	 */
	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void scanCtNamedElement(spoon.reflect.declaration.CtNamedElement e) {
		((spoon.reflect.declaration.CtNamedElement) (other)).setSimpleName(e.getSimpleName());
		super.scanCtNamedElement(e);
	}

	/**
	 * Scans an abstract reference.
	 */
	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void scanCtReference(spoon.reflect.reference.CtReference reference) {
		((spoon.reflect.reference.CtReference) (other)).setSimpleName(reference.getSimpleName());
		super.scanCtReference(reference);
	}

	/**
	 * Scans an abstract statement.
	 */
	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void scanCtStatement(spoon.reflect.code.CtStatement s) {
		((spoon.reflect.code.CtStatement) (other)).setLabel(s.getLabel());
		super.scanCtStatement(s);
	}

	/**
	 * Scans an abstract type.
	 */
	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void scanCtType(spoon.reflect.declaration.CtType<T> type) {
		((spoon.reflect.declaration.CtType<T>) (other)).setModifiers(type.getModifiers());
		((spoon.reflect.declaration.CtType<T>) (other)).setShadow(type.isShadow());
		super.scanCtType(type);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T, A extends T> void visitCtOperatorAssignment(spoon.reflect.code.CtOperatorAssignment<T, A> e) {
		((spoon.reflect.code.CtOperatorAssignment<T, A>) (other)).setKind(e.getKind());
		super.visitCtOperatorAssignment(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <A extends java.lang.annotation.Annotation> void visitCtAnnotation(spoon.reflect.declaration.CtAnnotation<A> e) {
		((spoon.reflect.declaration.CtAnnotation<A>) (other)).setShadow(e.isShadow());
		super.visitCtAnnotation(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtAnonymousExecutable(spoon.reflect.declaration.CtAnonymousExecutable e) {
		((spoon.reflect.declaration.CtAnonymousExecutable) (other)).setModifiers(e.getModifiers());
		super.visitCtAnonymousExecutable(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtBinaryOperator(spoon.reflect.code.CtBinaryOperator<T> e) {
		((spoon.reflect.code.CtBinaryOperator<T>) (other)).setKind(e.getKind());
		super.visitCtBinaryOperator(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtBreak(spoon.reflect.code.CtBreak e) {
		((spoon.reflect.code.CtBreak) (other)).setTargetLabel(e.getTargetLabel());
		super.visitCtBreak(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtConstructor(spoon.reflect.declaration.CtConstructor<T> e) {
		((spoon.reflect.declaration.CtConstructor<T>) (other)).setModifiers(e.getModifiers());
		((spoon.reflect.declaration.CtConstructor<T>) (other)).setShadow(e.isShadow());
		super.visitCtConstructor(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtContinue(spoon.reflect.code.CtContinue e) {
		((spoon.reflect.code.CtContinue) (other)).setTargetLabel(e.getTargetLabel());
		super.visitCtContinue(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtExecutableReference(spoon.reflect.reference.CtExecutableReference<T> e) {
		((spoon.reflect.reference.CtExecutableReference<T>) (other)).setStatic(e.isStatic());
		super.visitCtExecutableReference(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtField(spoon.reflect.declaration.CtField<T> e) {
		((spoon.reflect.declaration.CtField<T>) (other)).setModifiers(e.getModifiers());
		((spoon.reflect.declaration.CtField<T>) (other)).setShadow(e.isShadow());
		super.visitCtField(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtFieldReference(spoon.reflect.reference.CtFieldReference<T> e) {
		((spoon.reflect.reference.CtFieldReference<T>) (other)).setFinal(e.isFinal());
		((spoon.reflect.reference.CtFieldReference<T>) (other)).setStatic(e.isStatic());
		super.visitCtFieldReference(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtInvocation(spoon.reflect.code.CtInvocation<T> e) {
		((spoon.reflect.code.CtInvocation<T>) (other)).setLabel(e.getLabel());
		super.visitCtInvocation(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtLiteral(spoon.reflect.code.CtLiteral<T> e) {
		((spoon.reflect.code.CtLiteral<T>) (other)).setValue(e.getValue());
		super.visitCtLiteral(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtLocalVariable(spoon.reflect.code.CtLocalVariable<T> e) {
		((spoon.reflect.code.CtLocalVariable<T>) (other)).setSimpleName(e.getSimpleName());
		((spoon.reflect.code.CtLocalVariable<T>) (other)).setModifiers(e.getModifiers());
		super.visitCtLocalVariable(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtCatchVariable(spoon.reflect.code.CtCatchVariable<T> e) {
		((spoon.reflect.code.CtCatchVariable<T>) (other)).setSimpleName(e.getSimpleName());
		((spoon.reflect.code.CtCatchVariable<T>) (other)).setModifiers(e.getModifiers());
		super.visitCtCatchVariable(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtMethod(spoon.reflect.declaration.CtMethod<T> e) {
		((spoon.reflect.declaration.CtMethod<T>) (other)).setDefaultMethod(e.isDefaultMethod());
		((spoon.reflect.declaration.CtMethod<T>) (other)).setModifiers(e.getModifiers());
		((spoon.reflect.declaration.CtMethod<T>) (other)).setShadow(e.isShadow());
		super.visitCtMethod(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtConstructorCall(spoon.reflect.code.CtConstructorCall<T> e) {
		((spoon.reflect.code.CtConstructorCall<T>) (other)).setLabel(e.getLabel());
		super.visitCtConstructorCall(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public <T> void visitCtLambda(spoon.reflect.code.CtLambda<T> e) {
		((spoon.reflect.code.CtLambda<T>) (other)).setSimpleName(e.getSimpleName());
		super.visitCtLambda(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T, A extends T> void visitCtOperatorAssignement(spoon.reflect.code.CtOperatorAssignment<T, A> assignment) {
		((spoon.reflect.code.CtOperatorAssignment<T, A>) (other)).setKind(assignment.getKind());
		super.visitCtOperatorAssignement(assignment);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtPackage(spoon.reflect.declaration.CtPackage e) {
		((spoon.reflect.declaration.CtPackage) (other)).setShadow(e.isShadow());
		super.visitCtPackage(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtParameter(spoon.reflect.declaration.CtParameter<T> e) {
		((spoon.reflect.declaration.CtParameter<T>) (other)).setVarArgs(e.isVarArgs());
		((spoon.reflect.declaration.CtParameter<T>) (other)).setModifiers(e.getModifiers());
		((spoon.reflect.declaration.CtParameter<T>) (other)).setShadow(e.isShadow());
		super.visitCtParameter(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public void visitCtTypeParameterReference(spoon.reflect.reference.CtTypeParameterReference e) {
		((spoon.reflect.reference.CtTypeParameterReference) (other)).setUpper(e.isUpper());
		super.visitCtTypeParameterReference(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtTypeReference(spoon.reflect.reference.CtTypeReference<T> e) {
		((spoon.reflect.reference.CtTypeReference<T>) (other)).setShadow(e.isShadow());
		super.visitCtTypeReference(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	public <T> void visitCtUnaryOperator(spoon.reflect.code.CtUnaryOperator<T> e) {
		((spoon.reflect.code.CtUnaryOperator<T>) (other)).setKind(e.getKind());
		((spoon.reflect.code.CtUnaryOperator<T>) (other)).setLabel(e.getLabel());
		super.visitCtUnaryOperator(e);
	}

	// auto-generated, see spoon.generating.CloneVisitorGenerator
	@java.lang.Override
	public void visitCtComment(spoon.reflect.code.CtComment e) {
		((spoon.reflect.code.CtComment) (other)).setContent(e.getContent());
		((spoon.reflect.code.CtComment) (other)).setCommentType(e.getCommentType());
		super.visitCtComment(e);
	}
}

