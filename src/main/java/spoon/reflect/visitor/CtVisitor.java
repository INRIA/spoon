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
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * This interface defines the visitor for the Spoon metamodel, as defined in
 * {@link spoon.reflect.declaration}, {@link spoon.reflect.code}, and
 * {@link spoon.reflect.reference}.
 */
public interface CtVisitor {
	/**
	 * Visits an annotation.
	 */
	<A extends Annotation> void visitCtAnnotation(CtAnnotation<A> annotation);

	/**
	 * Visits a code snippet expression.
	 */
	<T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression<T> expression);

	/**
	 * Visits a code snippet statement.
	 */
	void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement);

	/**
	 * Visits an annotation type declaration.
	 */
	<A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType);

	/**
	 * Visits an anonymous executable.
	 */
	void visitCtAnonymousExecutable(CtAnonymousExecutable anonymousExec);

	/**
	 * Visits an array access.
	 */
	<T, E extends CtExpression<?>> void visitCtArrayAccess(
			CtArrayAccess<T, E> arrayAccess);

	/**
	 * Visits a reference to an array type.
	 */
	<T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference);

	/**
	 * Visits an assert.
	 */
	<T> void visitCtAssert(CtAssert<T> asserted);

	/**
	 * Visits an assignment.
	 */
	<T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignement);

	/**
	 * Visits a binary operator.
	 */
	<T> void visitCtBinaryOperator(CtBinaryOperator<T> operator);

	/**
	 * Visits a block of code.
	 */
	<R> void visitCtBlock(CtBlock<R> block);

	/**
	 * Visits a <code>break</code> statement.
	 */
	void visitCtBreak(CtBreak breakStatement);

	/**
	 * Visits a <code>case</code> clause.
	 */
	<S> void visitCtCase(CtCase<S> caseStatement);

	/**
	 * Visits a <code>catch</code> clause.
	 */
	void visitCtCatch(CtCatch catchBlock);

	/**
	 * Visits a class declaration.
	 */
	<T> void visitCtClass(CtClass<T> ctClass);

	/**
	 * Visits a conditional expression
	 */
	<T> void visitCtConditional(CtConditional<T> conditional);

	/**
	 * Visits a constructor declaration.
	 */
	<T> void visitCtConstructor(CtConstructor<T> c);

	/**
	 * Visits a <code>continue</code> statement.
	 */
	void visitCtContinue(CtContinue continueStatement);

	/**
	 * Visits a <code>do</code> loop.
	 */
	void visitCtDo(CtDo doLoop);

	/**
	 * Visits an enumeration declaration.
	 */
	<T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum);

	/**
	 * Visits a reference to an executable.
	 */
	<T> void visitCtExecutableReference(CtExecutableReference<T> reference);

	/**
	 * Visits a field declaration.
	 */
	<T> void visitCtField(CtField<T> f);

	/**
	 * Visits a field access.
	 */
	<T> void visitCtFieldAccess(CtFieldAccess<T> fieldAccess);

	/**
	 * Visits a reference to a field.
	 */
	<T> void visitCtFieldReference(CtFieldReference<T> reference);

	/**
	 * Visits a <code>for</code> loop.
	 */
	void visitCtFor(CtFor forLoop);

	/**
	 * Visits an enhanced <code>for</code> loop.
	 */
	void visitCtForEach(CtForEach foreach);

	/**
	 * Visits an <code>if</code> statement.
	 */
	void visitCtIf(CtIf ifElement);

	/**
	 * Visits an interface declaration.
	 */
	<T> void visitCtInterface(CtInterface<T> intrface);

	/**
	 * Visits an executable invocation.
	 */
	<T> void visitCtInvocation(CtInvocation<T> invocation);

	/**
	 * Visits a literal expression.
	 */
	<T> void visitCtLiteral(CtLiteral<T> literal);

	/**
	 * Visits a local variable declaration.
	 */
	<T> void visitCtLocalVariable(CtLocalVariable<T> localVariable);

	/**
	 * Visits a reference to a local variable.
	 */
	<T> void visitCtLocalVariableReference(CtLocalVariableReference<T> reference);

	/**
	 * Visits a method declaration.
	 */
	<T> void visitCtMethod(CtMethod<T> m);

	/**
	 * Visits an array construction.
	 */
	<T> void visitCtNewArray(CtNewArray<T> newArray);

	/**
	 * Visits an anonymous class construction.
	 */
	<T> void visitCtNewClass(CtNewClass<T> newClass);

	/**
	 * Visits an operator assignment.
	 */
	<T, A extends T> void visitCtOperatorAssignement(
			CtOperatorAssignment<T, A> assignment);

	/**
	 * Visits a package declaration.
	 */
	void visitCtPackage(CtPackage ctPackage);

	/**
	 * Visits a reference to a package.
	 */
	void visitCtPackageReference(CtPackageReference reference);

	/**
	 * Visits a parameter declaration.
	 */
	<T> void visitCtParameter(CtParameter<T> parameter);

	/**
	 * Visits a reference to a parameter.
	 */
	<T> void visitCtParameterReference(CtParameterReference<T> reference);

	/**
	 * Visits a <code>return</code> statement.
	 */
	<R> void visitCtReturn(CtReturn<R> returnStatement);

	/**
	 * Visits a statement list.
	 */
	<R> void visitCtStatementList(CtStatementList<R> statements);

	/**
	 * Visits a <code>switch</code> statement.
	 */
	<S> void visitCtSwitch(CtSwitch<S> switchStatement);

	/**
	 * Visits a <code>synchronized</code> modifier.
	 */
	void visitCtSynchronized(CtSynchronized synchro);

	/**
	 * Visits a <code>throw</code> statement.
	 */
	void visitCtThrow(CtThrow throwStatement);

	/**
	 * Visits a <code>try</code> statement.
	 */
	void visitCtTry(CtTry tryBlock);

	/**
	 * Visits a type parameter declaration.
	 */
	void visitCtTypeParameter(CtTypeParameter typeParameter);

	/**
	 * Visits a reference to a type parameter.
	 */
	void visitCtTypeParameterReference(CtTypeParameterReference ref);

	/**
	 * Visits a reference to a type.
	 */
	<T> void visitCtTypeReference(CtTypeReference<T> reference);

	/**
	 * Visits a unary operator.
	 */
	<T> void visitCtUnaryOperator(CtUnaryOperator<T> operator);

	/**
	 * Visits a variable access.
	 */
	<T> void visitCtVariableAccess(CtVariableAccess<T> variableAccess);

	/**
	 * Visits a <code>while</code> loop.
	 */
	void visitCtWhile(CtWhile whileLoop);

}