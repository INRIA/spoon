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

package spoon.reflect.factory;

import java.lang.annotation.Annotation;

import spoon.reflect.code.CtAnnotationFieldAccess;
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
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtThisAccess;
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
	 *
	 * @param <A> the type of the annotation
	 *
	 * @return a new annotation
	 */
	<A extends Annotation> CtAnnotation<A> createAnnotation();

	/**
	 * Creates an annotation type.
	 *
	 * @param <T> the type of the actual annotation
	 *
	 * @return a new annotation type
	 */
	<T extends Annotation> CtAnnotationType<T> createAnnotationType();

	/**
	 * Creates an anonymous executable.
	 *
	 * @return a new anonymous executable
	 */
	CtAnonymousExecutable createAnonymousExecutable();

	/**
	 * Creates an array access expression.
	 *
	 * @param <T> the return type of the access
	 * @param <E> the type of the target expression
	 *
	 * @return a new array access
	 */
	<T, E extends CtExpression<?>> CtArrayAccess<T, E> createArrayAccess();

	/**
	 * Creates an array type reference.
	 *
	 * @param <T> the type of the referenced array
	 *
	 * @return a new array type reference
	 */
	<T> CtArrayTypeReference<T> createArrayTypeReference();

	/**
	 * Creates an <code>assert</code> statement.
	 *
	 * @param <T> the type of the asserted expression
	 *
	 * @return a new {@code assert}
	 */
	<T> CtAssert<T> createAssert();

	/**
	 * Creates an assignment expression.
	 *
	 * @param <T> the type of the variable
	 * @param <A> the type of the assigned expression
	 *
	 * @return a new assignment
	 */
	<T, A extends T> CtAssignment<T, A> createAssignment();

	/**
	 * Creates a binary operator.
	 *
	 * @param <T> the return type of the operator
	 *
	 * @return a new binary operator
	 */
	<T> CtBinaryOperator<T> createBinaryOperator();

	/**
	 * Creates a block.
	 *
	 * @param <R> the return type of the block
	 *
	 * @return a new block
	 */
	<R> CtBlock<R> createBlock();

	/**
	 * Creates a <code>break</code> statement.
	 *
	 * @return a new {@code break}
	 */
	CtBreak createBreak();

	/**
	 * Creates a <code>case</code> clause.
	 *
	 * @param <S> the type of the switch selector expression
	 *
	 * @return a new case
	 */
	<S> CtCase<S> createCase();

	/**
	 * Creates a <code>catch</code> clause.
	 *
	 * @return a new {@code catch}
	 */
	CtCatch createCatch();

	/**
	 * Creates a class.
	 *
	 * @param <T> the actual runtime type of the class
	 *
	 * @return a new class
	 */
	<T> CtClass<T> createClass();

	/**
	 * Creates a conditional expression (<code>boolExpr?ifTrue:ifFalse</code>).
	 *
	 * @param <T> the return type of the expression
	 *
	 * @return a new conditional expression
	 */
	<T> CtConditional<T> createConditional();

	/**
	 * Creates a constructor.
	 *
	 * @param <T> the type of the constructor's class
	 *
	 * @return a new constructor
	 */
	<T> CtConstructor<T> createConstructor();

	/**
	 * Creates a <code>continue</code> statement.
	 *
	 * @return a new {@code continue}
	 */
	CtContinue createContinue();

	/**
	 * Creates a <code>do</code> loop.
	 *
	 * @return a new {@code do}
	 */
	CtDo createDo();

	/**
	 * Creates an enum.
	 *
	 * @param <T> the actual runtime type of the enum
	 *
	 * @return a new enum
	 */
	<T extends Enum<?>> CtEnum<T> createEnum();

	/**
	 * Creates an executable reference.
	 *
	 * @param <T> the return type of the executable
	 *
	 * @return a new executable reference
	 */
	<T> CtExecutableReference<T> createExecutableReference();

	/**
	 * Creates a field.
	 *
	 * @param <T> the type of the field
	 *
	 * @return a new field
	 */
	<T> CtField<T> createField();

	/**
	 * Creates a field access expression.
	 *
	 * @param <T> the type of the field
	 *
	 * @return a new field access
	 */
	<T> CtFieldAccess<T> createFieldAccess();
	

	/**
	 * Creates an access expression to this.
	 *
	 * @param <T> the runtime type of {@code this}
	 *
	 * @return a new {@code this} access
	 */
	<T> CtThisAccess<T> createThisAccess();
	
	/**
	 * Creates an access expression to super.
	 *
	 * @param <T> the type of the super class
	 *
	 * @return a new super access
	 */
	<T> CtSuperAccess<T> createSuperAccess();

	/**
	 * Creates a field reference.
	 *
	 * @param <T> the type of the field
	 *
	 * @return a new field reference
	 */
	<T> CtFieldReference<T> createFieldReference();

	/**
	 * Creates a <code>for</code> loop.
	 *
	 * @return a new for
	 */
	CtFor createFor();

	/**
	 * Creates a <code>foreach</code> loop.
	 *
	 * @return a new foreach
	 */
	CtForEach createForEach();

	/**
	 * Creates an <code>if</code> statement.
	 *
	 * @return a new if
	 */
	CtIf createIf();

	/**
	 * Creates an interface.
	 *
	 * @param <T> the actual runtime type of the interface
	 *
	 * @return a new interface
	 */
	<T> CtInterface<T> createInterface();

	/**
	 * Creates an invocation expression.
	 *
	 * @param <T> the return type of the invocation
	 *
	 * @return a new invocation
	 */
	<T> CtInvocation<T> createInvocation();

	/**
	 * Creates a literal expression.
	 *
	 * @param <T> the type of the literal
	 *
	 * @return a new literal
	 */
	<T> CtLiteral<T> createLiteral();

	/**
	 * Creates a local variable declaration statement.
	 *
	 * @param <T> the type of the local variable
	 *
	 * @return a new local variable declaration
	 */
	<T> CtLocalVariable<T> createLocalVariable();

	/**
	 * Creates a local variable reference.
	 *
	 * @param <T> the local variable's type
	 *
	 * @return a new local variable reference
	 */
	<T> CtLocalVariableReference<T> createLocalVariableReference();

	/**
	 * Creates a method.
	 *
	 * @param <T> the method's return type
	 *
	 * @return a new method
	 */
	<T> CtMethod<T> createMethod();

	/**
	 * Creates a new array expression.
	 *
	 * @param <T> the array's type
	 *
	 * @return a new array creation
	 */
	<T> CtNewArray<T> createNewArray();

	/**
	 * Creates a new anonymous class expression.
	 *
	 * @param <T> the type of the class
	 *
	 * @return a new anonymous class creation
	 */
	<T> CtNewClass<T> createNewClass();

	/**
	 * Creates a new operator assignment (like +=).
	 *
	 * @param <T> the variable type
	 * @param <A> the type of the expression to assign
	 *
	 * @return a new operator assignment
	 */
	<T, A extends T> CtOperatorAssignment<T, A> createOperatorAssignment();

	/**
	 * Creates a package.
	 *
	 * @return a new package
	 */
	CtPackage createPackage();

	/**
	 * Creates a package reference.
	 *
	 * @return  a new package reference
	 */
	CtPackageReference createPackageReference();

	/**
	 * Creates a parameter.
	 *
	 * @param <T> the parameter's type
	 *
	 * @return a new parameter
	 */
	<T> CtParameter<T> createParameter();

	/**
	 * Creates a parameter reference.
	 *
	 * @param <T> the referenced parameter's type
	 *
	 * @return a new parameter reference
	 */
	<T> CtParameterReference<T> createParameterReference();

	/**
	 * Creates a <code>return</code> statement.
	 *
	 * @param <R> the return expression's type
	 *
	 * @return a new return statement
	 */
	<R> CtReturn<R> createReturn();

	/**
	 * Creates a source position.
	 *
	 * @param compilationUnit the compilation unit
	 * @param start the start position
	 * @param end the end position
	 * @param lineSeparatorPositions an array of line separator positions
	 *
	 * @return a new source position
	 */
	SourcePosition createSourcePosition(CompilationUnit compilationUnit,
			int start, int end, int[] lineSeparatorPositions);

	/**
	 * Creates a statement list.
	 *
	 * @param <R> the statement list's type
	 *
	 * @return a new statement list
	 */
	<R> CtStatementList createStatementList();

	/**
	 * Creates a <code>switch</code> statement.
	 *
	 * @param <S> the selector expression's type
	 *
	 * @return a new switch statement
	 */
	<S> CtSwitch<S> createSwitch();

	/**
	 * Creates a <code>synchronized</code> statement.
	 *
	 * @return a new synchronized statement
	 */
	CtSynchronized createSynchronized();

	/**
	 * Creates a <code>throw</code> statement.
	 *
	 * @return a new throw block
	 */
	CtThrow createThrow();

	/**
	 * Creates a <code>try</code> block.
	 *
	 * @return a new try block
	 */
	CtTry createTry();

	/**
	 * Creates a type parameter.
	 *
	 * @return a new type parameter
	 */
	CtTypeParameter createTypeParameter();

	/**
	 * Creates a type parameter reference.
	 *
	 * @return a new type parameter reference
	 */
	CtTypeParameterReference createTypeParameterReference();

	/**
	 * Creates a type reference.
	 *
	 * @param <T> the referenced type
	 *
	 * @return a new type reference
	 */
	<T> CtTypeReference<T> createTypeReference();

	/**
	 * Creates a unary operator expression.
	 *
	 * @param <T> the return type of the operator
	 *
	 * @return a new unary operator
	 */
	<T> CtUnaryOperator<T> createUnaryOperator();

	/**
	 * Creates a variable access expression.
	 *
	 * @param <T> the variable type
	 *
	 * @return a new variable access
	 */
	<T> CtVariableAccess<T> createVariableAccess();

	/**
	 * Creates a <code>while</code> loop.
	 *
	 * @return a new while
	 */
	CtWhile createWhile();

	/**
	 * Creates a code snippet expression.
	 *
	 * @param <T> the return type of the expression
	 *
	 * @return a new code snippet expression
	 */
	<T> CtCodeSnippetExpression<T> createCodeSnippetExpression();

	/**
	 * Creates a code snippet statement.
	 *
	 * @return a new code snippet statement
	 */
	CtCodeSnippetStatement createCodeSnippetStatement();

	/**
	 * Gets the main factory of that core factory (cannot be <code>null</code>).
	 *
	 * @return the current main factory
	 */
	Factory getMainFactory();

	/**
	 * Sets the main factory of that core factory.
	 *
	 * @param mainFactory the main factory
	 */
	void setMainFactory(Factory mainFactory);

	/**
	 * Creates a compilation unit.
	 *
	 * @return a new compilation unit
	 */
	CompilationUnit createCompilationUnit();

	/**
	 * Creates a virtual compilation unit.
	 *
	 * @return a new virtual compilation unit
	 */
	CompilationUnit createVirtualCompilationUnit();

	/**
	 * Create an access to annotation value
	 *
	 * @param <T> the type of the field
	 * 
	 * @return an annotation field access
	 */
	<T> CtAnnotationFieldAccess<T> createAnnotationFieldAccess();

}
