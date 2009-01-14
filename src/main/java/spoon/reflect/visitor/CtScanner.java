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

import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
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
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
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
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * This visitor implements a deep-search scan on the metamodel.
 */
public class CtScanner implements CtVisitor {
	/**
	 * Default constructor.
	 */
	public CtScanner() {
		super();
	}

	/**
	 * This method is upcalled by the scanner when entering a scanned element.
	 * To be overriden to implement specific scanners.
	 */
	protected void enter(CtElement e) {
	}

	/**
	 * This method is upcalled by the scanner when entering a scanned element
	 * reference. To be overriden to implement specific scanners.
	 */
	protected void enterReference(CtReference e) {
	}

	/**
	 * This method is upcalled by the scanner when exiting a scanned element. To
	 * be overriden to implement specific scanners.
	 */
	protected void exit(CtElement e) {
	}

	/**
	 * This method is upcalled by the scanner when exiting a scanned element
	 * reference. To be overriden to implement specific scanners.
	 */
	protected void exitReference(CtReference e) {
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
		if ((element != null))
			element.accept(this);

	}

	/**
	 * Generically scans a meta-model element reference.
	 */
	public void scan(CtReference reference) {
		if ((reference != null))
			reference.accept(this);

	}

	/**
	 * Generically scans a collection of meta-model references.
	 */
	public void scanReferences(Collection<? extends CtReference> references) {
		if ((references != null)) {
			for (CtReference r : references) {
				scan(r);
			}
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
		if (o instanceof CtElement)
			scan((CtElement) o);
		if (o instanceof CtReference)
			scan((CtReference) o);
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

	public <T, E extends CtExpression<?>> void visitCtArrayAccess(
			CtArrayAccess<T, E> arrayAccess) {
		enter(arrayAccess);
		scan(arrayAccess.getAnnotations());
		scan(arrayAccess.getType());
		scanReferences(arrayAccess.getTypeCasts());
		scan(arrayAccess.getTarget());
		scan(arrayAccess.getIndexExpression());
		exit(arrayAccess);
	}

	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		enterReference(reference);
		scan(reference.getDeclaringType());
		scan(reference.getPackage());
		scan(reference.getComponentType());
		scanReferences(reference.getActualTypeArguments());
		exitReference(reference);
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
		scanReferences(assignement.getTypeCasts());
		scan(assignement.getAssigned());
		scan(assignement.getAssignment());
		exit(assignement);
	}

	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		enter(operator);
		scan(operator.getAnnotations());
		scan(operator.getType());
		scanReferences(operator.getTypeCasts());
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
		scanReferences(ctClass.getSuperInterfaces());
		scanReferences(ctClass.getFormalTypeParameters());
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
		scanReferences(c.getThrownTypes());
		scanReferences(c.getFormalTypeParameters());
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
		scan(ctEnum.getFields());
		scan(ctEnum.getMethods());
		scan(ctEnum.getNestedTypes());		
		exit(ctEnum);
	}

	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {
		enterReference(reference);
		scan(reference.getDeclaringType());
		scan(reference.getType());
		scanReferences(reference.getActualTypeArguments());
		scanReferences(reference.getParameterTypes());
		exitReference(reference);
	}

	public <T> void visitCtField(CtField<T> f) {
		enter(f);
		scan(f.getAnnotations());
		scan(f.getType());
		scan(f.getDefaultExpression());
		exit(f);
	}

	public <T> void visitCtFieldAccess(CtFieldAccess<T> fieldAccess) {
		enter(fieldAccess);
		scan(fieldAccess.getAnnotations());
		scan(fieldAccess.getType());
		scanReferences(fieldAccess.getTypeCasts());
		scan(fieldAccess.getTarget());
		scan(fieldAccess.getVariable());
		exit(fieldAccess);
	}

	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		enterReference(reference);
		scan(reference.getDeclaringType());
		scan(reference.getType());
		exitReference(reference);
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
		scan(ifElement.getThenStatement());
		scan(ifElement.getElseStatement());
		exit(ifElement);
	}

	public <T> void visitCtInterface(CtInterface<T> intrface) {
		enter(intrface);
		scan(intrface.getAnnotations());
		scanReferences(intrface.getSuperInterfaces());
		scanReferences(intrface.getFormalTypeParameters());
		scan(intrface.getNestedTypes());
		scan(intrface.getFields());
		scan(intrface.getMethods());
		exit(intrface);
	}

	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		enter(invocation);
		scan(invocation.getAnnotations());
		scan(invocation.getType());
		scanReferences(invocation.getTypeCasts());
		scan(invocation.getTarget());
		scan(invocation.getExecutable());
		scan(invocation.getArguments());
		exit(invocation);
	}

	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		enter(literal);
		scan(literal.getAnnotations());
		scan(literal.getType());
		scanReferences(literal.getTypeCasts());
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
		enterReference(reference);
		scan(reference.getType());
		exitReference(reference);
	}

	public <T> void visitCtMethod(CtMethod<T> m) {
		enter(m);
		scan(m.getAnnotations());
		scan(m.getType());
		scan(m.getParameters());
		scanReferences(m.getThrownTypes());
		scanReferences(m.getFormalTypeParameters());
		scan(m.getBody());
		exit(m);
	}

	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		enter(newArray);
		scan(newArray.getAnnotations());
		scan(newArray.getType());
		scanReferences(newArray.getTypeCasts());
		scan(newArray.getElements());
		scan(newArray.getDimensionExpressions());
		exit(newArray);
	}

	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		enter(newClass);
		scan(newClass.getAnnotations());
		scan(newClass.getType());
		scanReferences(newClass.getTypeCasts());
		scan(newClass.getExecutable());
		scan(newClass.getTarget());
		scan(newClass.getArguments());
		scan(newClass.getAnonymousClass());
		exit(newClass);
	}

	public <T, A extends T> void visitCtOperatorAssignement(
			CtOperatorAssignment<T, A> assignment) {
		enter(assignment);
		scan(assignment.getAnnotations());
		scan(assignment.getType());
		scanReferences(assignment.getTypeCasts());
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
		enterReference(reference);
		exitReference(reference);
	}

	public <T> void visitCtParameter(CtParameter<T> parameter) {
		enter(parameter);
		scan(parameter.getAnnotations());
		scan(parameter.getType());
		scan(parameter.getDefaultExpression());
		exit(parameter);
	}

	public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
		enterReference(reference);
		scan(reference.getType());
		exitReference(reference);
	}

	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		enter(returnStatement);
		scan(returnStatement.getAnnotations());
		scan(returnStatement.getReturnedExpression());
		exit(returnStatement);
	}

	public <R> void visitCtStatementList(CtStatementList<R> statements) {
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

	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		enter(typeParameter);
		scan(typeParameter.getAnnotations());
		scanReferences(typeParameter.getBounds());
		exit(typeParameter);
	}

	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
		enterReference(ref);
		scan(ref.getPackage());
		scan(ref.getDeclaringType());
		scanReferences(ref.getActualTypeArguments());
		scanReferences(ref.getBounds());
		exitReference(ref);
	}

	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		enterReference(reference);
		scan(reference.getPackage());
		scan(reference.getDeclaringType());
		scanReferences(reference.getActualTypeArguments());
		exitReference(reference);
	}

	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		enter(operator);
		scan(operator.getAnnotations());
		scan(operator.getType());
		scanReferences(operator.getTypeCasts());
		scan(operator.getOperand());
		exit(operator);
	}

	public <T> void visitCtVariableAccess(CtVariableAccess<T> variableAccess) {
		enter(variableAccess);
		scan(variableAccess.getAnnotations());
		scan(variableAccess.getType());
		scanReferences(variableAccess.getTypeCasts());
		scan(variableAccess.getVariable());
		exit(variableAccess);
	}

	public void visitCtWhile(CtWhile whileLoop) {
		enter(whileLoop);
		scan(whileLoop.getAnnotations());
		scan(whileLoop.getLoopingExpression());
		scan(whileLoop.getBody());
		exit(whileLoop);
	}

	public <T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression<T> expression) {
	}

	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
	}

}
