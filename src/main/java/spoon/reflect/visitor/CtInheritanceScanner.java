/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import java.lang.annotation.Annotation;
import java.util.Collection;

import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtGenericElement;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtGenericElementReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

/**
 * This class provides an abstract implementation of the visitor that allows its
 * subclasses to scans the metamodel elements by recursively using their
 * (abstract) supertype scanning methods.
 */
public abstract class CtInheritanceScanner implements CtVisitor {

	/**
	 * Default constructor.
	 */
	public CtInheritanceScanner() {
	}

	public <T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression<T> expression) {
	}

	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
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
		if (element != null)
			element.accept(this);
	}

	/**
	 * Generically scans a meta-model element reference.
	 */
	public void scan(CtReference reference) {
		if (reference != null)
			reference.accept(this);
	}

	/**
	 * Scans an abstract invocation.
	 */
	public <T> void scanCtAbstractInvocation(CtAbstractInvocation<T> a) {
	}

	/**
	 * Scans an abstract control flow break.
	 */
	public void scanCtCFlowBreak(CtCFlowBreak flowBreak) {
		scanCtCodeElement(flowBreak);
	}

	/**
	 * Scans an abstract code element.
	 */
	public void scanCtCodeElement(CtCodeElement e) {
		scanCtElement(e);
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
		scanCtGenericElement(e);
		scanCtNamedElement(e);
	}

	/**
	 * Scans an abstract expression.
	 */
	public <T> void scanCtExpression(CtExpression<T> expression) {
		scanCtCodeElement(expression);
		scanCtTypedElement(expression);
	}

	/**
	 * Scans an abstract generic element.
	 */
	public void scanCtGenericElement(CtGenericElement e) {
		scanCtElement(e);
	}

	/**
	 * Scans an abstract generic element reference.
	 */
	public void scanCtGenericElementReference(
			CtGenericElementReference reference) {
	}

	/**
	 * Scans an abstract loop.
	 */
	public void scanCtLoop(CtLoop loop) {
		scanCtStatement(loop);
	}

	/**
	 * Scans an abstract modifiable element.
	 */
	public void scanCtModifiable(CtModifiable m) {
		for (ModifierKind modifier : m.getModifiers()) {
			scanCtModifier(modifier);
		}
	}

	/**
	 * Scans a modifier (enumeration).
	 */
	public void scanCtModifier(ModifierKind m) {
	}

	/**
	 * Scans an abstract named element.
	 */
	public void scanCtNamedElement(CtNamedElement e) {
		scanCtElement(e);
		scanCtModifiable(e);
	}

	/**
	 * Scans an abstract reference.
	 */
	public void scanCtReference(CtReference reference) {

	}

	/**
	 * Scans an abstract simple type.
	 */
	public <T> void scanCtSimpleType(CtSimpleType<T> t) {
		scanCtNamedElement(t);
	}

	/**
	 * Scans an abstract statement.
	 */
	public void scanCtStatement(CtStatement s) {
		scanCtCodeElement(s);
	}

	/**
	 * Scans an abstract targeted expression.
	 */
	public <T, E extends CtExpression<?>> void scanCtTargetedExpression(
			CtTargetedExpression<T, E> targetedExpression) {
		scanCtExpression(targetedExpression);
	}

	/**
	 * Scans an abstract type.
	 */
	public <T> void scanCtType(CtType<T> type) {
		scanCtSimpleType(type);
		scanCtGenericElement(type);
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
		scanCtNamedElement(v);
		scanCtTypedElement(v);
	}

	/**
	 * Scans an abstract variable reference.
	 */
	public <T> void scanCtVariableReference(CtVariableReference<T> reference) {
		scanCtReference(reference);
	}

	/**
	 * Generically scans a collection of meta-model references.
	 */
	public void scanReferences(Collection<? extends CtReference> references) {
		if (references != null) {
			for (CtReference r : references) {
				scan(r);
			}
		}
	}

	public <A extends Annotation> void visitCtAnnotation(
			CtAnnotation<A> annotation) {
		scanCtElement(annotation);
	}

	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {
		scanCtSimpleType(annotationType);
	}

	public void visitCtAnonymousExecutable(CtAnonymousExecutable e) {
		scanCtElement(e);
		scanCtModifiable(e);
	}

	public <T, E extends CtExpression<?>> void visitCtArrayAccess(
			CtArrayAccess<T, E> arrayAccess) {
		scanCtTargetedExpression(arrayAccess);
	}

	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		visitCtTypeReference(reference);
	}

	public <T> void visitCtAssert(CtAssert<T> asserted) {
		scanCtStatement(asserted);
	}

	public <T, A extends T> void visitCtAssignment(
			CtAssignment<T, A> assignement) {
		scanCtExpression(assignement);
		scanCtStatement(assignement);
	}

	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		scanCtExpression(operator);
	}

	public <R> void visitCtBlock(CtBlock<R> block) {
		scanCtStatement(block);
	}

	public void visitCtBreak(CtBreak breakStatement) {
		scanCtCFlowBreak(breakStatement);
	}

	public <E> void visitCtCase(CtCase<E> caseStatement) {
		scanCtStatement(caseStatement);
	}

	public void visitCtCatch(CtCatch catchBlock) {
		scanCtCodeElement(catchBlock);
	}

	public <T> void visitCtClass(CtClass<T> ctClass) {
		scanCtType(ctClass);
	}

	public <T> void visitCtConditional(CtConditional<T> conditional) {
		scanCtExpression(conditional);
	}

	public <T> void visitCtConstructor(CtConstructor<T> c) {
		scanCtExecutable(c);
	}

	public void visitCtContinue(CtContinue continueStatement) {
		scanCtCFlowBreak(continueStatement);
	}

	public void visitCtDo(CtDo doLoop) {
		scanCtLoop(doLoop);
	}

	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		visitCtClass(ctEnum);
	}

	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {
		scanCtReference(reference);
		scanCtGenericElementReference(reference);
	}

	public <T> void visitCtField(CtField<T> f) {
		scanCtNamedElement(f);
		scanCtVariable(f);
	}

	public <T> void visitCtFieldAccess(CtFieldAccess<T> fieldAccess) {
		scanCtTargetedExpression(fieldAccess);
		visitCtVariableAccess(fieldAccess);
	}

	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		scanCtVariableReference(reference);
	}

	public void visitCtFor(CtFor forLoop) {
		scanCtLoop(forLoop);
	}

	public void visitCtForEach(CtForEach foreach) {
		scanCtLoop(foreach);
	}

	public void visitCtIf(CtIf ifElement) {
		scanCtStatement(ifElement);
	}

	public <T> void visitCtInterface(CtInterface<T> intrface) {
		scanCtType(intrface);
	}

	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		scanCtTargetedExpression(invocation);
		scanCtStatement(invocation);
		scanCtAbstractInvocation(invocation);
	}

	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		scanCtExpression(literal);
	}

	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
		scanCtVariable(localVariable);
		scanCtStatement(localVariable);
	}

	public <T> void visitCtLocalVariableReference(
			CtLocalVariableReference<T> reference) {
		scanCtVariableReference(reference);
	}

	public <T> void visitCtMethod(CtMethod<T> m) {
		scanCtExecutable(m);
		scanCtTypedElement(m);
	}

	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		scanCtExpression(newArray);
	}

	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		scanCtAbstractInvocation(newClass);
		scanCtTypedElement(newClass);
		scanCtTargetedExpression(newClass);
	}

	public <T, A extends T> void visitCtOperatorAssignement(
			CtOperatorAssignment<T, A> assignment) {
		visitCtAssignment(assignment);
	}

	public void visitCtPackage(CtPackage ctPackage) {
		scanCtNamedElement(ctPackage);
	}

	public void visitCtPackageReference(CtPackageReference reference) {
		scanCtReference(reference);
	}

	public <T> void visitCtParameter(CtParameter<T> parameter) {
		scanCtNamedElement(parameter);
		scanCtVariable(parameter);
	}

	public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
		scanCtVariableReference(reference);
	}

	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		scanCtCFlowBreak(returnStatement);
	}

	public <R> void visitCtStatementList(CtStatementList<R> statements) {
		scanCtCodeElement(statements);
	}

	public <E> void visitCtSwitch(CtSwitch<E> switchStatement) {
		scanCtStatement(switchStatement);
	}

	public void visitCtSynchronized(CtSynchronized synchro) {
		scanCtStatement(synchro);
	}

	public void visitCtThrow(CtThrow throwStatement) {
		scanCtCFlowBreak(throwStatement);
	}

	public void visitCtTry(CtTry tryBlock) {
		scanCtStatement(tryBlock);
	}

	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		scanCtElement(typeParameter);
	}

	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
		visitCtTypeReference(ref);
	}

	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		scanCtGenericElementReference(reference);
		scanCtReference(reference);
	}

	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		scanCtExpression(operator);
	}

	public <T> void visitCtVariableAccess(CtVariableAccess<T> variableAccess) {
		scanCtExpression(variableAccess);
	}

	public void visitCtWhile(CtWhile whileLoop) {
		scanCtLoop(whileLoop);
	}

}
