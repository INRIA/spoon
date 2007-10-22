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

package spoon.reflect;

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
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
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
 * This interface defines the core creation methods for the meta-model (to be
 * implemented so that Spoon can manipulate other meta-model implementations).
 * <p>
 * <b>Important</b>: a required post-condition for all the created elements is
 * that the factory (see {@link spoon.processing.FactoryAccessor#getFactory()})
 * is correctly initialized with the main factory returned by
 * {@link #getMainFactory()}, which cannot be null.
 */
public interface CoreFactory {

	/**
	 * Recursively clones a given element of the metamodel and all its child
	 * elements.
	 * 
	 * @param <T>
	 *            the element's type
	 * @param element
	 *            the element
	 * @return a clone of <code>element</code>
	 */
	<T> T clone(T element);

	/**
	 * Creates an annotation.
	 */
	<A extends Annotation> CtAnnotation<A> createAnnotation();

	/**
	 * Creates an annotation type.
	 */
	<T extends Annotation> CtAnnotationType<T> createAnnotationType();

	/**
	 * Creates an anonymous executable.
	 */
	CtAnonymousExecutable createAnonymousExecutable();

	/**
	 * Creates an array access expression.
	 */
	<T, E extends CtExpression<?>> CtArrayAccess<T, E> createArrayAccess();

	/**
	 * Creates an array type reference.
	 */
	<T> CtArrayTypeReference<T> createArrayTypeReference();

	/**
	 * Creates an <code>assert</code> statement.
	 */
	<T> CtAssert<T> createAssert();

	/**
	 * Creates an assignment expression.
	 */
	<T, A extends T> CtAssignment<T, A> createAssignment();

	/**
	 * Creates a binary operator.
	 */
	<T> CtBinaryOperator<T> createBinaryOperator();

	/**
	 * Creates a block.
	 */
	<R> CtBlock<R> createBlock();

	/**
	 * Creates a <code>break</code> statement.
	 */
	CtBreak createBreak();

	/**
	 * Creates a <code>case</code> clause.
	 */
	<S> CtCase<S> createCase();

	/**
	 * Creates a <code>catch</code> clause.
	 */
	CtCatch createCatch();

	/**
	 * Creates a class.
	 */
	<T> CtClass<T> createClass();

	/**
	 * Creates a conditional expression (<code>boolExpr?ifTrue:ifFalse</code>).
	 */
	<T> CtConditional<T> createConditional();

	/**
	 * Creates a constructor.
	 */
	<T> CtConstructor<T> createConstructor();

	/**
	 * Creates a <code>continue</code> statement.
	 */
	CtContinue createContinue();

	/**
	 * Creates a <code>do</code> loop.
	 */
	CtDo createDo();

	/**
	 * Creates an enum.
	 */
	<T extends Enum<?>> CtEnum<T> createEnum();

	/**
	 * Creates an executable reference.
	 */
	<T> CtExecutableReference<T> createExecutableReference();

	/**
	 * Creates a field.
	 */
	<T> CtField<T> createField();

	/**
	 * Creates a field access expression.
	 */
	<T> CtFieldAccess<T> createFieldAccess();

	/**
	 * Creates a field reference.
	 */
	<T> CtFieldReference<T> createFieldReference();

	/**
	 * Creates a <code>for</code> loop.
	 */
	CtFor createFor();

	/**
	 * Creates a <code>foreach</code> loop.
	 */
	CtForEach createForEach();

	/**
	 * Creates an <code>if</code> statement.
	 */
	CtIf createIf();

	/**
	 * Creates an interface.
	 */
	<T> CtInterface<T> createInterface();

	/**
	 * Creates an invocation expression.
	 */
	<T> CtInvocation<T> createInvocation();

	/**
	 * Creates a literal expression.
	 */
	<T> CtLiteral<T> createLiteral();

	/**
	 * Creates a local variable declaration statement.
	 */
	<T> CtLocalVariable<T> createLocalVariable();

	/**
	 * Creates a local variable reference.
	 */
	<T> CtLocalVariableReference<T> createLocalVariableReference();

	/**
	 * Creates a method.
	 */
	<T> CtMethod<T> createMethod();

	/**
	 * Creates a new array expression.
	 */
	<T> CtNewArray<T> createNewArray();

	/**
	 * Creates a new anonymous class expression.
	 */
	<T> CtNewClass<T> createNewClass();

	/**
	 * Creates a new operator assignement (like +=).
	 */
	<T, A extends T> CtOperatorAssignment<T, A> createOperatorAssignment();

	/**
	 * Creates a package.
	 */
	CtPackage createPackage();

	/**
	 * Creates a package reference.
	 */
	CtPackageReference createPackageReference();

	/**
	 * Creates a parameter.
	 */
	<T> CtParameter<T> createParameter();

	/**
	 * Creates a parameter reference.
	 */
	<T> CtParameterReference<T> createParameterReference();

	/**
	 * Creates a <code>return</code> statement.
	 */
	<R> CtReturn<R> createReturn();

	/**
	 * Creates a source position.
	 */
	SourcePosition createSourcePosition(CompilationUnit compilationUnit,
			int start, int end, int[] lineSeparatorPositions);

	/**
	 * Creates a statement list.
	 */
	<R> CtStatementList<R> createStatementList();

	/**
	 * Creates a <code>switch</code> statement.
	 */
	<S> CtSwitch<S> createSwitch();

	/**
	 * Creates a <code>synchronized</code> statement.
	 */
	CtSynchronized createSynchronized();

	/**
	 * Creates a <code>throw</code> statement.
	 */
	CtThrow createThrow();

	/**
	 * Creates a <code>try</code> block.
	 */
	CtTry createTry();

	/**
	 * Creates a type parameter.
	 */
	CtTypeParameter createTypeParameter();

	/**
	 * Creates a type parameter reference.
	 */
	CtTypeParameterReference createTypeParameterReference();

	/**
	 * Creates a type reference.
	 */
	<T> CtTypeReference<T> createTypeReference();

	/**
	 * Creates a unary operator expression.
	 */
	<T> CtUnaryOperator<T> createUnaryOperator();

	/**
	 * Creates a variable access expression.
	 */
	<T> CtVariableAccess<T> createVariableAccess();

	/**
	 * Creates a <code>while</code> loop.
	 */
	CtWhile createWhile();

	/**
	 * Creates a code snippet expression.
	 */
	<T> CtCodeSnippetExpression<T> createCodeSnippetExpression();

	/**
	 * Creates a code snippet statement.
	 */
	CtCodeSnippetStatement createCodeSnippetStatement();

	/**
	 * Gets the main factory of that core factory (cannot be <code>null</code>).
	 */
	Factory getMainFactory();

	/**
	 * Sets the main factory of that core factory.
	 */
	void setMainFactory(Factory mainFactory);

	/**
	 * Creates a compilation unit.
	 */
	CompilationUnit createCompilationUnit();
	
	/**
	 * Creates a virtual compilation unit.
	 */
	CompilationUnit createVirtualCompilationUnit();

}
