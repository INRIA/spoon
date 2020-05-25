/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support;


import java.lang.annotation.Annotation;
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
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.BodyHolderSourcePosition;
import spoon.reflect.cu.position.CompoundSourcePosition;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.experimental.CtUnresolvedImport;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.SubFactory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.support.reflect.code.CtAnnotationFieldAccessImpl;
import spoon.support.reflect.code.CtArrayReadImpl;
import spoon.support.reflect.code.CtArrayWriteImpl;
import spoon.support.reflect.code.CtAssertImpl;
import spoon.support.reflect.code.CtAssignmentImpl;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtBreakImpl;
import spoon.support.reflect.code.CtCaseImpl;
import spoon.support.reflect.code.CtCatchImpl;
import spoon.support.reflect.code.CtCatchVariableImpl;
import spoon.support.reflect.code.CtCodeSnippetExpressionImpl;
import spoon.support.reflect.code.CtCodeSnippetStatementImpl;
import spoon.support.reflect.code.CtCommentImpl;
import spoon.support.reflect.code.CtConditionalImpl;
import spoon.support.reflect.code.CtConstructorCallImpl;
import spoon.support.reflect.code.CtContinueImpl;
import spoon.support.reflect.code.CtDoImpl;
import spoon.support.reflect.code.CtExecutableReferenceExpressionImpl;
import spoon.support.reflect.code.CtFieldReadImpl;
import spoon.support.reflect.code.CtFieldWriteImpl;
import spoon.support.reflect.code.CtForEachImpl;
import spoon.support.reflect.code.CtForImpl;
import spoon.support.reflect.code.CtIfImpl;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtJavaDocImpl;
import spoon.support.reflect.code.CtJavaDocTagImpl;
import spoon.support.reflect.code.CtLambdaImpl;
import spoon.support.reflect.code.CtLiteralImpl;
import spoon.support.reflect.code.CtLocalVariableImpl;
import spoon.support.reflect.code.CtNewArrayImpl;
import spoon.support.reflect.code.CtNewClassImpl;
import spoon.support.reflect.code.CtOperatorAssignmentImpl;
import spoon.support.reflect.code.CtReturnImpl;
import spoon.support.reflect.code.CtStatementListImpl;
import spoon.support.reflect.code.CtSuperAccessImpl;
import spoon.support.reflect.code.CtSwitchExpressionImpl;
import spoon.support.reflect.code.CtSwitchImpl;
import spoon.support.reflect.code.CtSynchronizedImpl;
import spoon.support.reflect.code.CtThisAccessImpl;
import spoon.support.reflect.code.CtThrowImpl;
import spoon.support.reflect.code.CtTryImpl;
import spoon.support.reflect.code.CtTryWithResourceImpl;
import spoon.support.reflect.code.CtTypeAccessImpl;
import spoon.support.reflect.code.CtUnaryOperatorImpl;
import spoon.support.reflect.code.CtVariableReadImpl;
import spoon.support.reflect.code.CtVariableWriteImpl;
import spoon.support.reflect.code.CtWhileImpl;
import spoon.support.reflect.code.CtYieldStatementImpl;
import spoon.support.reflect.cu.CompilationUnitImpl;
import spoon.support.reflect.cu.position.BodyHolderSourcePositionImpl;
import spoon.support.reflect.cu.position.CompoundSourcePositionImpl;
import spoon.support.reflect.cu.position.DeclarationSourcePositionImpl;
import spoon.support.reflect.cu.position.SourcePositionImpl;
import spoon.support.reflect.declaration.CtAnnotationImpl;
import spoon.support.reflect.declaration.CtAnnotationMethodImpl;
import spoon.support.reflect.declaration.CtAnnotationTypeImpl;
import spoon.support.reflect.declaration.CtAnonymousExecutableImpl;
import spoon.support.reflect.declaration.CtClassImpl;
import spoon.support.reflect.declaration.CtConstructorImpl;
import spoon.support.reflect.declaration.CtEnumImpl;
import spoon.support.reflect.declaration.CtEnumValueImpl;
import spoon.support.reflect.declaration.CtFieldImpl;
import spoon.support.reflect.declaration.CtImportImpl;
import spoon.support.reflect.declaration.CtInterfaceImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.support.reflect.declaration.CtModuleImpl;
import spoon.support.reflect.declaration.CtModuleRequirementImpl;
import spoon.support.reflect.declaration.CtPackageDeclarationImpl;
import spoon.support.reflect.declaration.CtPackageExportImpl;
import spoon.support.reflect.declaration.CtPackageImpl;
import spoon.support.reflect.declaration.CtParameterImpl;
import spoon.support.reflect.declaration.CtProvidedServiceImpl;
import spoon.support.reflect.declaration.CtTypeParameterImpl;
import spoon.support.reflect.declaration.CtUsedServiceImpl;
import spoon.support.reflect.declaration.InvisibleArrayConstructorImpl;
import spoon.support.reflect.reference.CtArrayTypeReferenceImpl;
import spoon.support.reflect.reference.CtCatchVariableReferenceImpl;
import spoon.support.reflect.reference.CtExecutableReferenceImpl;
import spoon.support.reflect.reference.CtFieldReferenceImpl;
import spoon.support.reflect.reference.CtIntersectionTypeReferenceImpl;
import spoon.support.reflect.reference.CtLocalVariableReferenceImpl;
import spoon.support.reflect.reference.CtModuleReferenceImpl;
import spoon.support.reflect.reference.CtPackageReferenceImpl;
import spoon.support.reflect.reference.CtParameterReferenceImpl;
import spoon.support.reflect.reference.CtTypeParameterReferenceImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;
import spoon.support.reflect.reference.CtUnboundVariableReferenceImpl;
import spoon.support.reflect.reference.CtWildcardReferenceImpl;
import spoon.support.reflect.reference.CtTypeMemberWildcardImportReferenceImpl;
import spoon.support.visitor.equals.CloneHelper;

/**
 * This class implements a default core factory for Spoon's meta-model. This
 * implementation is done with regular Java classes (POJOs).
 */
public class DefaultCoreFactory extends SubFactory implements CoreFactory {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public DefaultCoreFactory() {
		super(null);
	}

	@Override
	public <T extends CtElement> T clone(T object) {
		return CloneHelper.INSTANCE.clone(object);
	}

	@Override
	public <A extends Annotation> CtAnnotation<A> createAnnotation() {
		CtAnnotation<A> e = new CtAnnotationImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T extends Annotation> CtAnnotationType<T> createAnnotationType() {
		CtAnnotationType<T> e = new CtAnnotationTypeImpl<>();
		e.setFactory(getMainFactory());
		e.setParent(getMainFactory().Package().getRootPackage());
		return e;
	}

	@Override
	public CtAnonymousExecutable createAnonymousExecutable() {
		CtAnonymousExecutable e = new CtAnonymousExecutableImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtArrayRead<T> createArrayRead() {
		CtArrayRead<T> e = new CtArrayReadImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtArrayWrite<T> createArrayWrite() {
		CtArrayWrite<T> e = new CtArrayWriteImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtArrayTypeReference<T> createArrayTypeReference() {
		CtArrayTypeReference<T> e = new CtArrayTypeReferenceImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtAssert<T> createAssert() {
		CtAssert<T> e = new CtAssertImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T, A extends T> CtAssignment<T, A> createAssignment() {
		CtAssignment<T, A> e = new CtAssignmentImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtBinaryOperator<T> createBinaryOperator() {
		CtBinaryOperator<T> e = new CtBinaryOperatorImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <R> CtBlock<R> createBlock() {
		CtBlock<R> e = new CtBlockImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtBreak createBreak() {
		CtBreak e = new CtBreakImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <S> CtCase<S> createCase() {
		CtCase<S> e = new CtCaseImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtCatch createCatch() {
		CtCatch e = new CtCatchImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtClass<T> createClass() {
		CtClass<T> e = new CtClassImpl<>();
		e.setFactory(getMainFactory());
		e.setParent(getMainFactory().Package().getRootPackage());
		return e;
	}

	@Override
	public CtTypeParameter createTypeParameter() {
		CtTypeParameter e = new CtTypeParameterImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtConditional<T> createConditional() {
		CtConditional<T> e = new CtConditionalImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtConstructor<T> createConstructor() {
		CtConstructor<T> e = new CtConstructorImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtConstructor<T> createInvisibleArrayConstructor() {
		CtConstructor<T> e = new InvisibleArrayConstructorImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtContinue createContinue() {
		CtContinue e = new CtContinueImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtDo createDo() {
		CtDo e = new CtDoImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T extends Enum<?>> CtEnum<T> createEnum() {
		CtEnum<T> e = new CtEnumImpl<>();
		e.setFactory(getMainFactory());
		e.setParent(getMainFactory().Package().getRootPackage());
		return e;
	}

	@Override
	public <T> CtExecutableReference<T> createExecutableReference() {
		CtExecutableReference<T> e = new CtExecutableReferenceImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtField<T> createField() {
		CtField<T> e = new CtFieldImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtEnumValue<T> createEnumValue() {
		CtEnumValue<T> e = new CtEnumValueImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtFieldRead<T> createFieldRead() {
		CtFieldRead<T> e = new CtFieldReadImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtFieldWrite<T> createFieldWrite() {
		CtFieldWrite<T> e = new CtFieldWriteImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtAnnotationFieldAccess<T> createAnnotationFieldAccess() {
		CtAnnotationFieldAccess<T> e = new CtAnnotationFieldAccessImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtUnboundVariableReference<T> createUnboundVariableReference() {
		CtUnboundVariableReference e = new CtUnboundVariableReferenceImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtFieldReference<T> createFieldReference() {
		CtFieldReference<T> e = new CtFieldReferenceImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtFor createFor() {
		CtFor e = new CtForImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtForEach createForEach() {
		CtForEach e = new CtForEachImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtIf createIf() {
		CtIf e = new CtIfImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtInterface<T> createInterface() {
		CtInterface<T> e = new CtInterfaceImpl<>();
		e.setFactory(getMainFactory());
		e.setParent(getMainFactory().Package().getRootPackage());
		return e;
	}

	@Override
	public <T> CtInvocation<T> createInvocation() {
		CtInvocation<T> e = new CtInvocationImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtLiteral<T> createLiteral() {
		CtLiteral<T> e = new CtLiteralImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtLocalVariable<T> createLocalVariable() {
		CtLocalVariable<T> e = new CtLocalVariableImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtLocalVariableReference<T> createLocalVariableReference() {
		CtLocalVariableReference<T> e = new CtLocalVariableReferenceImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtCatchVariable<T> createCatchVariable() {
		CtCatchVariable<T> e = new CtCatchVariableImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtCatchVariableReference<T> createCatchVariableReference() {
		CtCatchVariableReference<T> e = new CtCatchVariableReferenceImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtMethod<T> createMethod() {
		CtMethod<T> e = new CtMethodImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtAnnotationMethod<T> createAnnotationMethod() {
		CtAnnotationMethod<T> e = new CtAnnotationMethodImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtNewArray<T> createNewArray() {
		CtNewArray<T> e = new CtNewArrayImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtConstructorCall<T> createConstructorCall() {
		CtConstructorCall<T> e = new CtConstructorCallImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtNewClass<T> createNewClass() {
		CtNewClass<T> e = new CtNewClassImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtLambda<T> createLambda() {
		CtLambda<T> e = new CtLambdaImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T, E extends CtExpression<?>> CtExecutableReferenceExpression<T, E> createExecutableReferenceExpression() {
		CtExecutableReferenceExpression<T, E> e = new CtExecutableReferenceExpressionImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T, A extends T> CtOperatorAssignment<T, A> createOperatorAssignment() {
		CtOperatorAssignment<T, A> e = new CtOperatorAssignmentImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtPackage createPackage() {
		CtPackage e = new CtPackageImpl();
		e.setFactory(getMainFactory());
		e.setParent(getMainFactory().Package().getRootPackage());
		return e;
	}

	@Override
	public CtPackageReference createPackageReference() {
		CtPackageReference e = new CtPackageReferenceImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtParameter<T> createParameter() {
		CtParameter<T> e = new CtParameterImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtParameterReference<T> createParameterReference() {
		CtParameterReference<T> e = new CtParameterReferenceImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <R> CtReturn<R> createReturn() {
		CtReturn<R> e = new CtReturnImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <R> CtStatementList createStatementList() {
		CtStatementList e = new CtStatementListImpl<R>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <S> CtSwitch<S> createSwitch() {
		CtSwitch<S> e = new CtSwitchImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T, S> CtSwitchExpression<T, S> createSwitchExpression() {
		CtSwitchExpression<T, S> e = new CtSwitchExpressionImpl<T, S>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtSynchronized createSynchronized() {
		CtSynchronized e = new CtSynchronizedImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtThrow createThrow() {
		CtThrow e = new CtThrowImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtTry createTry() {
		CtTry e = new CtTryImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtTryWithResource createTryWithResource() {
		CtTryWithResource e = new CtTryWithResourceImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtTypeParameterReference createTypeParameterReference() {
		CtTypeParameterReference e = new CtTypeParameterReferenceImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtWildcardReference createWildcardReference() {
		CtWildcardReference e = new CtWildcardReferenceImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtIntersectionTypeReference<T> createIntersectionTypeReference() {
		CtIntersectionTypeReference<T> e = new CtIntersectionTypeReferenceImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtTypeReference<T> createTypeReference() {
		CtTypeReference<T> e = new CtTypeReferenceImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtTypeAccess<T> createTypeAccess() {
		CtTypeAccess<T> e = new CtTypeAccessImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtUnaryOperator<T> createUnaryOperator() {
		CtUnaryOperator<T> e = new CtUnaryOperatorImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtVariableRead<T> createVariableRead() {
		CtVariableRead<T> e = new CtVariableReadImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtVariableWrite<T> createVariableWrite() {
		CtVariableWrite<T> e = new CtVariableWriteImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtCodeSnippetExpression<T> createCodeSnippetExpression() {
		CtCodeSnippetExpression<T> e = new CtCodeSnippetExpressionImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtCodeSnippetStatement createCodeSnippetStatement() {
		CtCodeSnippetStatement e = new CtCodeSnippetStatementImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtComment createComment() {
		CtComment e = new CtCommentImpl();
		e.setFactory(getMainFactory());
		e.setCommentType(CtComment.CommentType.BLOCK);
		e.setContent("");
		return e;
	}

	@Override
	public CtJavaDoc createJavaDoc() {
		CtJavaDoc e = new CtJavaDocImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtJavaDocTag createJavaDocTag() {
		CtJavaDocTag e = new CtJavaDocTagImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtWhile createWhile() {
		CtWhile e = new CtWhileImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtImport createImport() {
		CtImport e = new CtImportImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtImport createUnresolvedImport() {
		CtImport e = new CtUnresolvedImport();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtPackageDeclaration createPackageDeclaration() {
		CtPackageDeclaration e = new CtPackageDeclarationImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public Factory getMainFactory() {
		return factory;
	}

	@Override
	public void setMainFactory(Factory mainFactory) {
		this.factory = mainFactory;
	}

	@Override
	public SourcePosition createSourcePosition(CompilationUnit compilationUnit, int startSource, int end, int[] lineSeparatorPositions) {
		return new SourcePositionImpl(compilationUnit, startSource, end, lineSeparatorPositions);
	}

	@Override
	public SourcePosition createPartialSourcePosition(CompilationUnit compilationUnit) {
		return ((CompilationUnitImpl) compilationUnit).getOrCreatePartialSourcePosition();
	}

	@Override
	public CompoundSourcePosition createCompoundSourcePosition(CompilationUnit compilationUnit, int startSource, int end, int declarationStart, int declarationEnd, int[] lineSeparatorPositions) {
		return new CompoundSourcePositionImpl(compilationUnit, startSource, end, declarationStart, declarationEnd, lineSeparatorPositions);
	}

	@Override
	public DeclarationSourcePosition createDeclarationSourcePosition(CompilationUnit compilationUnit, int startSource, int end, int modifierStart, int modifierEnd, int declarationStart, int declarationEnd, int[] lineSeparatorPositions) {
		return new DeclarationSourcePositionImpl(compilationUnit, startSource, end, modifierStart, modifierEnd, declarationStart, declarationEnd, lineSeparatorPositions);
	}

	@Override
	public BodyHolderSourcePosition createBodyHolderSourcePosition(CompilationUnit compilationUnit, int startSource, int end, int modifierStart, int modifierEnd, int declarationStart, int declarationEnd, int bodyStart, int bodyEnd, int[] lineSeparatorPositions) {
		return new BodyHolderSourcePositionImpl(compilationUnit,
				startSource, end,
				modifierStart, modifierEnd,
				declarationStart, declarationEnd,
				bodyStart, bodyEnd,
				lineSeparatorPositions);
	}

	@Override
	public CompilationUnit createCompilationUnit() {
		CompilationUnit cu = new CompilationUnitImpl();
		cu.setFactory(getMainFactory());
		return cu;
	}

	@Override
	public <T> CtThisAccess<T> createThisAccess() {
		CtThisAccess<T> e = new CtThisAccessImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtSuperAccess<T> createSuperAccess() {
		CtSuperAccess<T> e = new CtSuperAccessImpl<>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public CtElement create(Class<? extends CtElement> klass) {
		if (klass.equals(spoon.reflect.code.CtAnnotationFieldAccess.class)) {
			return createAnnotationFieldAccess();
		}
		if (klass.equals(spoon.reflect.code.CtArrayRead.class)) {
			return createArrayRead();
		}
		if (klass.equals(spoon.reflect.code.CtArrayWrite.class)) {
			return createArrayWrite();
		}
		if (klass.equals(spoon.reflect.code.CtAssert.class)) {
			return createAssert();
		}
		if (klass.equals(spoon.reflect.code.CtAssignment.class)) {
			return createAssignment();
		}
		if (klass.equals(spoon.reflect.code.CtBinaryOperator.class)) {
			return createBinaryOperator();
		}
		if (klass.equals(spoon.reflect.code.CtBlock.class)) {
			return createBlock();
		}
		if (klass.equals(spoon.reflect.code.CtBreak.class)) {
			return createBreak();
		}
		if (klass.equals(spoon.reflect.code.CtCase.class)) {
			return createCase();
		}
		if (klass.equals(spoon.reflect.code.CtCatch.class)) {
			return createCatch();
		}
		if (klass.equals(spoon.reflect.code.CtCatchVariable.class)) {
			return createCatchVariable();
		}
		if (klass.equals(spoon.reflect.code.CtCodeSnippetExpression.class)) {
			return createCodeSnippetExpression();
		}
		if (klass.equals(spoon.reflect.code.CtCodeSnippetStatement.class)) {
			return createCodeSnippetStatement();
		}
		if (klass.equals(spoon.reflect.code.CtComment.class)) {
			return createComment();
		}
		if (klass.equals(spoon.reflect.code.CtJavaDoc.class)) {
			return createJavaDoc();
		}
		if (klass.equals(spoon.reflect.code.CtJavaDocTag.class)) {
			return createJavaDocTag();
		}
		if (klass.equals(spoon.reflect.code.CtConditional.class)) {
			return createConditional();
		}
		if (klass.equals(spoon.reflect.code.CtConstructorCall.class)) {
			return createConstructorCall();
		}
		if (klass.equals(spoon.reflect.code.CtContinue.class)) {
			return createContinue();
		}
		if (klass.equals(spoon.reflect.code.CtDo.class)) {
			return createDo();
		}
		if (klass.equals(spoon.reflect.code.CtExecutableReferenceExpression.class)) {
			return createExecutableReferenceExpression();
		}
		if (klass.equals(spoon.reflect.code.CtFieldRead.class)) {
			return createFieldRead();
		}
		if (klass.equals(spoon.reflect.code.CtFieldWrite.class)) {
			return createFieldWrite();
		}
		if (klass.equals(spoon.reflect.code.CtForEach.class)) {
			return createForEach();
		}
		if (klass.equals(spoon.reflect.code.CtFor.class)) {
			return createFor();
		}
		if (klass.equals(spoon.reflect.code.CtIf.class)) {
			return createIf();
		}
		if (klass.equals(spoon.reflect.code.CtInvocation.class)) {
			return createInvocation();
		}
		if (klass.equals(spoon.reflect.code.CtLambda.class)) {
			return createLambda();
		}
		if (klass.equals(spoon.reflect.code.CtLiteral.class)) {
			return createLiteral();
		}
		if (klass.equals(spoon.reflect.code.CtLocalVariable.class)) {
			return createLocalVariable();
		}
		if (klass.equals(spoon.reflect.code.CtNewArray.class)) {
			return createNewArray();
		}
		if (klass.equals(spoon.reflect.code.CtNewClass.class)) {
			return createNewClass();
		}
		if (klass.equals(spoon.reflect.code.CtOperatorAssignment.class)) {
			return createOperatorAssignment();
		}
		if (klass.equals(spoon.reflect.code.CtReturn.class)) {
			return createReturn();
		}
		if (klass.equals(spoon.reflect.code.CtStatementList.class)) {
			return createStatementList();
		}
		if (klass.equals(spoon.reflect.code.CtSuperAccess.class)) {
			return createSuperAccess();
		}
		if (klass.equals(spoon.reflect.code.CtSwitch.class)) {
			return createSwitch();
		}
		if (klass.equals(spoon.reflect.code.CtSwitchExpression.class)) {
			return createSwitchExpression();
		}
		if (klass.equals(spoon.reflect.code.CtSynchronized.class)) {
			return createSynchronized();
		}
		if (klass.equals(spoon.reflect.code.CtThisAccess.class)) {
			return createThisAccess();
		}
		if (klass.equals(spoon.reflect.code.CtThrow.class)) {
			return createThrow();
		}
		if (klass.equals(spoon.reflect.code.CtTry.class)) {
			return createTry();
		}
		if (klass.equals(spoon.reflect.code.CtTryWithResource.class)) {
			return createTryWithResource();
		}
		if (klass.equals(spoon.reflect.code.CtTypeAccess.class)) {
			return createTypeAccess();
		}
		if (klass.equals(spoon.reflect.code.CtUnaryOperator.class)) {
			return createUnaryOperator();
		}
		if (klass.equals(spoon.reflect.code.CtVariableRead.class)) {
			return createVariableRead();
		}
		if (klass.equals(spoon.reflect.code.CtVariableWrite.class)) {
			return createVariableWrite();
		}
		if (klass.equals(spoon.reflect.code.CtWhile.class)) {
			return createWhile();
		}
		if (klass.equals(spoon.reflect.declaration.CtAnnotation.class)) {
			return createAnnotation();
		}
		if (klass.equals(spoon.reflect.declaration.CtAnnotationMethod.class)) {
			return createAnnotationMethod();
		}
		if (klass.equals(spoon.reflect.declaration.CtAnnotationType.class)) {
			return createAnnotationType();
		}
		if (klass.equals(spoon.reflect.declaration.CtAnonymousExecutable.class)) {
			return createAnonymousExecutable();
		}
		if (klass.equals(spoon.reflect.declaration.CtClass.class)) {
			return createClass();
		}
		if (klass.equals(spoon.reflect.declaration.CtConstructor.class)) {
			return createConstructor();
		}
		if (klass.equals(spoon.reflect.declaration.CtEnum.class)) {
			return createEnum();
		}
		if (klass.equals(spoon.reflect.declaration.CtEnumValue.class)) {
			return createEnumValue();
		}
		if (klass.equals(spoon.reflect.declaration.CtField.class)) {
			return createField();
		}
		if (klass.equals(spoon.reflect.declaration.CtInterface.class)) {
			return createInterface();
		}
		if (klass.equals(spoon.reflect.declaration.CtMethod.class)) {
			return createMethod();
		}
		if (klass.equals(spoon.reflect.declaration.CtPackage.class)) {
			return createPackage();
		}
		if (klass.equals(spoon.reflect.declaration.CtParameter.class)) {
			return createParameter();
		}
		if (klass.equals(spoon.reflect.declaration.CtTypeParameter.class)) {
			return createTypeParameter();
		}
		if (klass.equals(spoon.reflect.reference.CtArrayTypeReference.class)) {
			return createArrayTypeReference();
		}
		if (klass.equals(spoon.reflect.reference.CtCatchVariableReference.class)) {
			return createCatchVariableReference();
		}
		if (klass.equals(spoon.reflect.reference.CtExecutableReference.class)) {
			return createExecutableReference();
		}
		if (klass.equals(spoon.reflect.reference.CtFieldReference.class)) {
			return createFieldReference();
		}
		if (klass.equals(spoon.reflect.reference.CtIntersectionTypeReference.class)) {
			return createIntersectionTypeReference();
		}
		if (klass.equals(spoon.reflect.reference.CtLocalVariableReference.class)) {
			return createLocalVariableReference();
		}
		if (klass.equals(spoon.reflect.reference.CtPackageReference.class)) {
			return createPackageReference();
		}
		if (klass.equals(spoon.reflect.reference.CtParameterReference.class)) {
			return createParameterReference();
		}
		if (klass.equals(spoon.reflect.reference.CtTypeParameterReference.class)) {
			return createTypeParameterReference();
		}
		if (klass.equals(spoon.reflect.reference.CtTypeReference.class)) {
			return createTypeReference();
		}
		if (klass.equals(spoon.reflect.reference.CtUnboundVariableReference.class)) {
			return createUnboundVariableReference();
		}
		if (klass.equals(spoon.reflect.reference.CtWildcardReference.class)) {
			return createWildcardReference();
		}
		if (klass.equals(spoon.reflect.declaration.CtImport.class)) {
			return createImport();
		}
		if (klass.equals(spoon.reflect.reference.CtModuleReference.class)) {
			return createModuleReference();
		}
		if (klass.equals(spoon.reflect.reference.CtTypeMemberWildcardImportReference.class)) {
			return createTypeMemberWildcardImportReference();
		}
		if (klass.equals(spoon.reflect.declaration.CtModule.class)) {
			return createModule();
		}
		if (klass.equals(spoon.reflect.declaration.CtModuleRequirement.class)) {
			return createModuleRequirement();
		}
		if (klass.equals(spoon.reflect.declaration.CtPackageExport.class)) {
			return createPackageExport();
		}
		if (klass.equals(spoon.reflect.declaration.CtProvidedService.class)) {
			return createProvidedService();
		}
		if (klass.equals(spoon.reflect.declaration.CtUsedService.class)) {
			return createUsedService();
		}
		if (klass.equals(spoon.reflect.declaration.CtCompilationUnit.class)) {
			return createCompilationUnit();
		}
		if (klass.equals(spoon.reflect.declaration.CtPackageDeclaration.class)) {
			return createPackageDeclaration();
		}
		if (klass.equals(spoon.reflect.code.CtYieldStatement.class)) {
			return createYieldStatement();
		}
		throw new IllegalArgumentException("not instantiable by CoreFactory(): " + klass);
	}

	@Override
	public CtTypeMemberWildcardImportReference createTypeMemberWildcardImportReference() {
		CtTypeMemberWildcardImportReference result = new CtTypeMemberWildcardImportReferenceImpl();
		result.setFactory(getMainFactory());
		return result;
	}

	@Override
	public CtModule createModule() {
		CtModule module = new CtModuleImpl();
		module.setFactory(getMainFactory());
		this.getMainFactory().Module().getUnnamedModule().addModule(module);
		return module;
	}

	@Override
	public CtModuleReference createModuleReference() {
		CtModuleReference moduleReference = new CtModuleReferenceImpl();
		moduleReference.setFactory(getMainFactory());
		return moduleReference;
	}

	@Override
	public CtModuleRequirement createModuleRequirement() {
		CtModuleRequirement moduleRequirement = new CtModuleRequirementImpl();
		moduleRequirement.setFactory(getMainFactory());
		return moduleRequirement;
	}

	@Override
	public CtPackageExport createPackageExport() {
		CtPackageExport moduleExport = new CtPackageExportImpl();
		moduleExport.setFactory(getMainFactory());
		return moduleExport;
	}

	@Override
	public CtProvidedService createProvidedService() {
		CtProvidedService moduleProvidedService = new CtProvidedServiceImpl();
		moduleProvidedService.setFactory(getMainFactory());
		return moduleProvidedService;
	}

	@Override
	public CtUsedService createUsedService() {
		CtUsedService ctUsedService = new CtUsedServiceImpl();
		ctUsedService.setFactory(getMainFactory());
		return ctUsedService;
	}

	@Override
	public CtYieldStatement createYieldStatement() {
		CtYieldStatement e = new CtYieldStatementImpl();
		e.setFactory(getMainFactory());
		return e;
	}
}

