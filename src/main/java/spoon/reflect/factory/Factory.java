/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.reflect.factory;

import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.code.BinaryOperatorKind;
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
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
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
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.BodyHolderSourcePosition;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.eval.PartialEvaluator;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.support.visitor.GenericTypeAdapter;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Provides the sub-factories required by Spoon.
 *
 * Most classes provides a method getFactory() that returns the current factory.
 *
 * Otherwise FactoryImpl is a default implementation.
 */
public interface Factory {

	/** returns the Spoon model that has been built with this factory or one of its subfactories */
	CtModel getModel();

	CoreFactory Core(); // used 238 times

	TypeFactory Type(); // used 107 times

	EnumFactory Enum();

	Environment getEnvironment(); // used 71 times

	PackageFactory Package(); // used 30 times

	CodeFactory Code(); // used 28 times

	ClassFactory Class(); // used 27 times

	FieldFactory Field(); // used 9 times

	ExecutableFactory Executable(); // used 8 times

	CompilationUnitFactory CompilationUnit(); // used 7 times

	InterfaceFactory Interface();

	MethodFactory Method(); // used 5 times

	AnnotationFactory Annotation(); // used 4 times

	EvalFactory Eval(); // used 4 times

	ConstructorFactory Constructor(); // used 3 times

	QueryFactory Query();

	/**
	 *  @see CodeFactory#createAnnotation(CtTypeReference)
	 */
	<A extends Annotation> CtAnnotation<A> createAnnotation(CtTypeReference<A> annotationType);

	/**
	 *  @see CodeFactory#createVariableAssignment(CtVariableReference,boolean, CtExpression)
	 */
	<A, T extends A> CtAssignment<A, T> createVariableAssignment(CtVariableReference<A> variable, boolean isStatic, CtExpression<T> expression);

	/**
	 *  @see CodeFactory#createStatementList(CtBlock)
	 */
	<R> CtStatementList createStatementList(CtBlock<R> block);

	/**
	 *  @see CodeFactory#createCtBlock(CtStatement)
	 */
	<T extends CtStatement> CtBlock<?> createCtBlock(T element);

	/**
	 *  @see CodeFactory#createBinaryOperator(CtExpression,CtExpression, BinaryOperatorKind)
	 */
	<T> CtBinaryOperator<T> createBinaryOperator(CtExpression<?> left, CtExpression<?> right, BinaryOperatorKind kind);

	/**
	 *  @see CodeFactory#createCatchVariable(CtTypeReference,String, ModifierKind[])
	 */
	<T> CtCatchVariable<T> createCatchVariable(CtTypeReference<T> type, String name, ModifierKind... modifierKinds);

	/**
	 *  @see CodeFactory#createCodeSnippetExpression(String)
	 */
	<T> CtCodeSnippetExpression<T> createCodeSnippetExpression(String expression);

	/**
	 *  @see CodeFactory#createConstructorCall(CtTypeReference,CtExpression[])
	 */
	<T> CtConstructorCall<T> createConstructorCall(CtTypeReference<T> type, CtExpression<?>... parameters);

	/**
	 *  @see CodeFactory#createClassAccess(CtTypeReference)
	 */
	<T> CtFieldAccess<Class<T>> createClassAccess(CtTypeReference<T> type);

	/**
	 *  @see CodeFactory#createInvocation(CtExpression, CtExecutableReference, List)
	 */
	<T> CtInvocation<T> createInvocation(CtExpression<?> target, CtExecutableReference<T> executable, List<CtExpression<?>> arguments);

	/**
	 *  @see CodeFactory#createInvocation(CtExpression,CtExecutableReference,CtExpression[])
	 */
	<T> CtInvocation<T> createInvocation(CtExpression<?> target, CtExecutableReference<T> executable, CtExpression<?>... arguments);

	/**
	 *  @see CodeFactory#createLiteral(Object)
	 */
	<T> CtLiteral<T> createLiteral(T value);

	/**
	 *  @see CodeFactory#createLocalVariable(CtTypeReference,String,CtExpression)
	 */
	<T> CtLocalVariable<T> createLocalVariable(CtTypeReference<T> type, String name, CtExpression<T> defaultExpression);

	/**
	 *  @see CodeFactory#createLiteralArray(Object[])
	 */
	@SuppressWarnings(value = "unchecked")
	<T> CtNewArray<T[]> createLiteralArray(T[] value);

	/**
	 *  @see CodeFactory#createNewClass(CtTypeReference, CtClass,CtExpression[])
	 */
	<T> CtNewClass<T> createNewClass(CtTypeReference<T> type, CtClass<?> anonymousClass, CtExpression<?>... parameters);

	/**
	 *  @see CodeFactory#createVariableAssignments(List,List)
	 */
	<T> CtStatementList createVariableAssignments(List<? extends CtVariable<T>> variables, List<? extends CtExpression<T>> expressions);

	/**
	 *  @see CodeFactory#createThisAccess(CtTypeReference)
	 */
	<T> CtThisAccess<T> createThisAccess(CtTypeReference<T> type);

	/**
	 *  @see CodeFactory#createThisAccess(CtTypeReference,boolean)
	 */
	<T> CtThisAccess<T> createThisAccess(CtTypeReference<T> type, boolean isImplicit);

	/**
	 *  @see CodeFactory#createTypeAccess(CtTypeReference)
	 */
	<T> CtTypeAccess<T> createTypeAccess(CtTypeReference<T> accessedType);

	/**
	 *  @see CodeFactory#createTypeAccess(CtTypeReference,boolean)
	 */
	<T> CtTypeAccess<T> createTypeAccess(CtTypeReference<T> accessedType, boolean isImplicit);

	/**
	 *  @see CodeFactory#createTypeAccessWithoutCloningReference(CtTypeReference)
	 */
	<T> CtTypeAccess<T> createTypeAccessWithoutCloningReference(CtTypeReference<T> accessedType);

	/**
	 *  @see CodeFactory#createVariableRead(CtVariableReference,boolean)
	 */
	<T> CtVariableAccess<T> createVariableRead(CtVariableReference<T> variable, boolean isStatic);

	/**
	 *  @see CodeFactory#createCtField(String,CtTypeReference,String,ModifierKind[])
	 */
	<T> CtField<T> createCtField(String name, CtTypeReference<T> type, String exp, ModifierKind... visibilities);

	/**
	 *  @see CodeFactory#createCatchVariableReference(CtCatchVariable)
	 */
	<T> CtCatchVariableReference<T> createCatchVariableReference(CtCatchVariable<T> catchVariable);

	/**
	 *  @see CodeFactory#createLocalVariableReference(CtLocalVariable)
	 */
	<T> CtLocalVariableReference<T> createLocalVariableReference(CtLocalVariable<T> localVariable);

	/**
	 *  @see CodeFactory#createLocalVariableReference(CtTypeReference,String)
	 */
	<T> CtLocalVariableReference<T> createLocalVariableReference(CtTypeReference<T> type, String name);

	/**
	 *  @see CodeFactory#createCtTypeReference(Class)
	 */
	<T> CtTypeReference<T> createCtTypeReference(Class<?> originalClass);

	/**
	 *  @see CodeFactory#createVariableReads(List)
	 */
	List<CtExpression<?>> createVariableReads(List<? extends CtVariable<?>> variables);

	/**
	 *  @see CodeFactory#createCtCatch(String,Class,CtBlock)
	 */
	CtCatch createCtCatch(String nameCatch, Class<? extends Throwable> exception, CtBlock<?> ctBlock);

	/**
	 *  @see CodeFactory#createCodeSnippetStatement(String)
	 */
	CtCodeSnippetStatement createCodeSnippetStatement(String statement);

	/**
	 *  @see CodeFactory#createComment(String,CtComment.CommentType)
	 */
	CtComment createComment(String content, CtComment.CommentType type);


	/**
	 *  @see CodeFactory#createJavaDocTag(String,CtJavaDocTag.TagType)
	 */
	CtJavaDocTag createJavaDocTag(String content, CtJavaDocTag.TagType type);

	/**
	 *  @see CodeFactory#createInlineComment(String)
	 */
	CtComment createInlineComment(String content);

	/**
	 *  @see CodeFactory#createCtThrow(String)
	 */
	CtThrow createCtThrow(String thrownExp);

	/**
	 *  @see CodeFactory#createCtPackageReference(Package)
	 */
	CtPackageReference createCtPackageReference(Package originalPackage);

	/**
	 *  @see ConstructorFactory#createDefault(CtClass)
	 */
	<T> CtConstructor<T> createDefault(CtClass<T> target);

	/**
	 *  @see CoreFactory#createAnnotation()
	 */
	<A extends Annotation> CtAnnotation<A> createAnnotation();

	/**
	 *  @see CoreFactory#createBlock()
	 */
	<R> CtBlock<R> createBlock();

	/**
	 *  @see CoreFactory#createReturn()
	 */
	<R> CtReturn<R> createReturn();

	/**
	 *  @see CoreFactory#createStatementList()
	 */
	<R> CtStatementList createStatementList();

	/**
	 *  @see CoreFactory#createCase()
	 */
	<S> CtCase<S> createCase();

	/**
	 *  @see CoreFactory#createSwitch()
	 */
	<S> CtSwitch<S> createSwitch();

	/**
	 *  @see CoreFactory#createEnum()
	 */
	<T extends Enum<?>> CtEnum<T> createEnum();

	/**
	 *  @see CoreFactory#createAnnotationType()
	 */
	<T extends Annotation> CtAnnotationType<T> createAnnotationType();

	/**
	 *  @see CoreFactory#createAssignment()
	 */
	<T, A extends T> CtAssignment<T, A> createAssignment();

	/**
	 *  @see CoreFactory#createOperatorAssignment()
	 */
	<T, A extends T> CtOperatorAssignment<T, A> createOperatorAssignment();

	/**
	 *  @see CoreFactory#createExecutableReferenceExpression()
	 */
	<T, E extends CtExpression<?>> CtExecutableReferenceExpression<T, E> createExecutableReferenceExpression();

	/**
	 *  @see CoreFactory#createAnnotationFieldAccess()
	 */
	<T> CtAnnotationFieldAccess<T> createAnnotationFieldAccess();

	/**
	 *  @see CoreFactory#createArrayRead()
	 */
	<T> CtArrayRead<T> createArrayRead();

	/**
	 *  @see CoreFactory#createArrayWrite()
	 */
	<T> CtArrayWrite<T> createArrayWrite();

	/**
	 *  @see CoreFactory#createAssert()
	 */
	<T> CtAssert<T> createAssert();

	/**
	 *  @see CoreFactory#createBinaryOperator()
	 */
	<T> CtBinaryOperator<T> createBinaryOperator();

	/**
	 *  @see CoreFactory#createCatchVariable()
	 */
	<T> CtCatchVariable<T> createCatchVariable();

	/**
	 *  @see CoreFactory#createCodeSnippetExpression()
	 */
	<T> CtCodeSnippetExpression<T> createCodeSnippetExpression();

	/**
	 *  @see CoreFactory#createConditional()
	 */
	<T> CtConditional<T> createConditional();

	/**
	 *  @see CoreFactory#createConstructorCall()
	 */
	<T> CtConstructorCall<T> createConstructorCall();

	/**
	 *  @see CoreFactory#createFieldRead()
	 */
	<T> CtFieldRead<T> createFieldRead();

	/**
	 *  @see CoreFactory#createFieldWrite()
	 */
	<T> CtFieldWrite<T> createFieldWrite();

	/**
	 *  @see CoreFactory#createInvocation()
	 */
	<T> CtInvocation<T> createInvocation();

	/**
	 *  @see CoreFactory#createLambda()
	 */
	<T> CtLambda<T> createLambda();

	/**
	 *  @see CoreFactory#createLiteral()
	 */
	<T> CtLiteral<T> createLiteral();

	/**
	 *  @see CoreFactory#createLocalVariable()
	 */
	<T> CtLocalVariable<T> createLocalVariable();

	/**
	 *  @see CoreFactory#createNewArray()
	 */
	<T> CtNewArray<T> createNewArray();

	/**
	 *  @see CoreFactory#createNewClass()
	 */
	<T> CtNewClass<T> createNewClass();

	/**
	 *  @see CoreFactory#createSuperAccess()
	 */
	<T> CtSuperAccess<T> createSuperAccess();

	/**
	 *  @see CoreFactory#createThisAccess()
	 */
	<T> CtThisAccess<T> createThisAccess();

	/**
	 *  @see CoreFactory#createTypeAccess()
	 */
	<T> CtTypeAccess<T> createTypeAccess();

	/**
	 *  @see CoreFactory#createUnaryOperator()
	 */
	<T> CtUnaryOperator<T> createUnaryOperator();

	/**
	 *  @see CoreFactory#createVariableRead()
	 */
	<T> CtVariableRead<T> createVariableRead();

	/**
	 *  @see CoreFactory#createVariableWrite()
	 */
	<T> CtVariableWrite<T> createVariableWrite();

	/**
	 *  @see CoreFactory#createAnnotationMethod()
	 */
	<T> CtAnnotationMethod<T> createAnnotationMethod();

	/**
	 *  @see CoreFactory#createClass()
	 */
	<T> CtClass<T> createClass();

	/**
	 *  @see CoreFactory#createConstructor()
	 */
	<T> CtConstructor<T> createConstructor();

	/**
	 *  @see CoreFactory#createEnumValue()
	 */
	<T> CtEnumValue<T> createEnumValue();

	/**
	 *  @see CoreFactory#createField()
	 */
	<T> CtField<T> createField();

	/**
	 *  @see CoreFactory#createInterface()
	 */
	<T> CtInterface<T> createInterface();

	/**
	 *  @see CoreFactory#createMethod()
	 */
	<T> CtMethod<T> createMethod();

	/**
	 *  @see CoreFactory#createParameter()
	 */
	<T> CtParameter<T> createParameter();

	/**
	 *  @see CoreFactory#createArrayTypeReference()
	 */
	<T> CtArrayTypeReference<T> createArrayTypeReference();

	/**
	 *  @see CoreFactory#createCatchVariableReference()
	 */
	<T> CtCatchVariableReference<T> createCatchVariableReference();

	/**
	 *  @see CoreFactory#createExecutableReference()
	 */
	<T> CtExecutableReference<T> createExecutableReference();

	/**
	 *  @see CoreFactory#createFieldReference()
	 */
	<T> CtFieldReference<T> createFieldReference();

	/**
	 *  @see CoreFactory#createIntersectionTypeReference()
	 */
	<T> CtIntersectionTypeReference<T> createIntersectionTypeReference();

	/**
	 *  @see CoreFactory#createLocalVariableReference()
	 */
	<T> CtLocalVariableReference<T> createLocalVariableReference();

	/**
	 *  @see CoreFactory#createParameterReference()
	 */
	<T> CtParameterReference<T> createParameterReference();

	/**
	 *  @see CoreFactory#createTypeReference()
	 */
	<T> CtTypeReference<T> createTypeReference();

	/**
	 *  @see CoreFactory#createUnboundVariableReference()
	 */
	<T> CtUnboundVariableReference<T> createUnboundVariableReference();

	/**
	 *  @see CoreFactory#createBreak()
	 */
	CtBreak createBreak();

	/**
	 *  @see CoreFactory#createCatch()
	 */
	CtCatch createCatch();

	/**
	 *  @see CoreFactory#createCodeSnippetStatement()
	 */
	CtCodeSnippetStatement createCodeSnippetStatement();

	/**
	 *  @see CoreFactory#createComment()
	 */
	CtComment createComment();

	/**
	 *  @see CoreFactory#createContinue()
	 */
	CtContinue createContinue();

	/**
	 *  @see CoreFactory#createDo()
	 */
	CtDo createDo();

	/**
	 *  @see CoreFactory#createFor()
	 */
	CtFor createFor();

	/**
	 *  @see CoreFactory#createForEach()
	 */
	CtForEach createForEach();

	/**
	 *  @see CoreFactory#createIf()
	 */
	CtIf createIf();

	/**
	 *  @see CoreFactory#createSynchronized()
	 */
	CtSynchronized createSynchronized();

	/**
	 *  @see CoreFactory#createThrow()
	 */
	CtThrow createThrow();

	/**
	 *  @see CoreFactory#createTry()
	 */
	CtTry createTry();

	/**
	 *  @see CoreFactory#createTryWithResource()
	 */
	CtTryWithResource createTryWithResource();

	/**
	 *  @see CoreFactory#createWhile()
	 */
	CtWhile createWhile();

	/**
	 *  @see CoreFactory#createCompilationUnit()
	 */
	CompilationUnit createCompilationUnit();

	/**
	 *  @see CoreFactory#createSourcePosition(CompilationUnit,int,int,int[])
	 */
	SourcePosition createSourcePosition(CompilationUnit compilationUnit, int startSource, int end, int[] lineSeparatorPositions);

	/**
	 *  @see CoreFactory#createBodyHolderSourcePosition(CompilationUnit,int,int,int,int,int,int,int,int,int[])
	 */
	BodyHolderSourcePosition createBodyHolderSourcePosition(CompilationUnit compilationUnit, int startSource, int end, int modifierStart, int modifierEnd, int declarationStart, int declarationEnd, int bodyStart, int bodyEnd, int[] lineSeparatorPositions);

	/**
	 *  @see CoreFactory#createDeclarationSourcePosition(CompilationUnit,int,int,int,int,int,int,int[])
	 */
	DeclarationSourcePosition createDeclarationSourcePosition(CompilationUnit compilationUnit, int startSource, int end, int modifierStart, int modifierEnd, int declarationStart, int declarationEnd, int[] lineSeparatorPositions);

	/**
	 *  @see CoreFactory#createAnonymousExecutable()
	 */
	CtAnonymousExecutable createAnonymousExecutable();

	/**
	 *  @see CoreFactory#createPackage()
	 */
	CtPackage createPackage();

	/**
	 *  @see CoreFactory#createTypeParameter()
	 */
	CtTypeParameter createTypeParameter();

	/**
	 *  @see CoreFactory#createPackageReference()
	 */
	CtPackageReference createPackageReference();

	/**
	 *  @see CoreFactory#createTypeParameterReference()
	 */
	CtTypeParameterReference createTypeParameterReference();

	/**
	 *  @see CoreFactory#createWildcardReference()
	 */
	CtWildcardReference createWildcardReference();

	/**
	 *  @see EvalFactory#createPartialEvaluator()
	 */
	PartialEvaluator createPartialEvaluator();

	/**
	 *  @see ExecutableFactory#createParameter(CtExecutable,CtTypeReference,String)
	 */
	<T> CtParameter<T> createParameter(CtExecutable<?> parent, CtTypeReference<T> type, String name);

	/**
	 *  @see ExecutableFactory#createParameterReference(CtParameter)
	 */
	<T> CtParameterReference<T> createParameterReference(CtParameter<T> parameter);

	/**
	 *  @see ExecutableFactory#createAnonymous(CtClass,CtBlock)
	 */
	CtAnonymousExecutable createAnonymous(CtClass<?> target, CtBlock<Void> body);

	/**
	 *  @see TypeFactory#createArrayReference(String)
	 */
	<T> CtArrayTypeReference<T> createArrayReference(String qualifiedName);

	/**
	 *  @see TypeFactory#createArrayReference(CtType)
	 */
	<T> CtArrayTypeReference<T[]> createArrayReference(CtType<T> type);

	/**
	 *  @see TypeFactory#createArrayReference(CtTypeReference)
	 */
	<T> CtArrayTypeReference<T[]> createArrayReference(CtTypeReference<T> reference);

	/**
	 *  @see TypeFactory#createIntersectionTypeReferenceWithBounds(List)
	 */
	<T> CtIntersectionTypeReference<T> createIntersectionTypeReferenceWithBounds(List<CtTypeReference<?>> bounds);

	/**
	 * @see TypeFactory#createTypeAdapter(CtFormalTypeDeclarer)
	 */
	GenericTypeAdapter createTypeAdapter(CtFormalTypeDeclarer formalTypeDeclarer);

	/**
	 *  @see TypeFactory#createReferences(List)
	 */
	List<CtTypeReference<?>> createReferences(List<Class<?>> classes);

	/**
	 *  @see TypeFactory#createArrayReference(CtTypeReference,int)
	 */
	CtArrayTypeReference<?> createArrayReference(CtTypeReference<?> reference, int n);

	/**
	 *  @see TypeFactory#createTypeParameterReference(String)
	 */
	CtTypeParameterReference createTypeParameterReference(String name);

	/**
	 *  @see QueryFactory#createQuery()
	 */
	CtQuery createQuery();

	/**
	 *  @see QueryFactory#createQuery(Object)
	 */
	CtQuery createQuery(Object input);
}
