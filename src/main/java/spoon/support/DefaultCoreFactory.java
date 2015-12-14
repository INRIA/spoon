/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.support;

import spoon.Launcher;
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
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
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
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
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
import spoon.support.reflect.code.CtLambdaImpl;
import spoon.support.reflect.code.CtLiteralImpl;
import spoon.support.reflect.code.CtLocalVariableImpl;
import spoon.support.reflect.code.CtNewArrayImpl;
import spoon.support.reflect.code.CtNewClassImpl;
import spoon.support.reflect.code.CtOperatorAssignmentImpl;
import spoon.support.reflect.code.CtReturnImpl;
import spoon.support.reflect.code.CtStatementListImpl;
import spoon.support.reflect.code.CtSuperAccessImpl;
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
import spoon.support.reflect.cu.CompilationUnitImpl;
import spoon.support.reflect.cu.SourcePositionImpl;
import spoon.support.reflect.declaration.CompilationUnitVirtualImpl;
import spoon.support.reflect.declaration.CtAnnotationImpl;
import spoon.support.reflect.declaration.CtAnnotationTypeImpl;
import spoon.support.reflect.declaration.CtAnonymousExecutableImpl;
import spoon.support.reflect.declaration.CtClassImpl;
import spoon.support.reflect.declaration.CtConstructorImpl;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.reflect.declaration.CtEnumImpl;
import spoon.support.reflect.declaration.CtFieldImpl;
import spoon.support.reflect.declaration.CtInterfaceImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.support.reflect.declaration.CtPackageImpl;
import spoon.support.reflect.declaration.CtParameterImpl;
import spoon.support.reflect.declaration.CtTypeParameterImpl;
import spoon.support.reflect.reference.CtArrayTypeReferenceImpl;
import spoon.support.reflect.reference.CtCatchVariableReferenceImpl;
import spoon.support.reflect.reference.CtExecutableReferenceImpl;
import spoon.support.reflect.reference.CtFieldReferenceImpl;
import spoon.support.reflect.reference.CtLocalVariableReferenceImpl;
import spoon.support.reflect.reference.CtPackageReferenceImpl;
import spoon.support.reflect.reference.CtParameterReferenceImpl;
import spoon.support.reflect.reference.CtTypeParameterReferenceImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;
import spoon.support.util.RtHelper;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

/**
 * This class implements a default core factory for Spoon's meta-model. This
 * implementation is done with regular Java classes (POJOs).
 */
public class DefaultCoreFactory implements CoreFactory, Serializable {

	private static final long serialVersionUID = 1L;

	// transient Stack<CtElement> cloningContext = new Stack<CtElement>();

	Factory mainFactory;

	/**
	 * Default constructor.
	 */
	public DefaultCoreFactory() {
	}

	public <T> T clone(T object) {
		return clone(object, new Stack<CtElement>());
	}

	@SuppressWarnings("unchecked")
	private <T> T clone(T object, Stack<CtElement> cloningContext) {
		if (object == null) {
			return null;
		}
		T result = null;
		try {
			if (!(object instanceof CtElement)) {
				return object;
			}
			// RP: this should be done first or removed?
			if (object instanceof Cloneable) {
				return (T) object.getClass().getMethod("clone").invoke(object);
			}
			// RP: never called?
			if (object.getClass().isEnum()) {
				return object;
			}
			result = (T) object.getClass().newInstance();
			if (result instanceof CtElement) {
				cloningContext.push((CtElement) result);
			}
			for (Field f : RtHelper.getAllFields(object.getClass())) {
				f.setAccessible(true);
				if (!f.getName().equals("parent")) {
					Object fieldValue = f.get(object);
					if (!Modifier.isFinal(f.getModifiers()) && !Modifier.isStatic(f.getModifiers())) {
						if (fieldValue instanceof Collection) {
							Collection<Object> c;
							if (fieldValue == CtElementImpl.emptyCollection() || fieldValue == CtElementImpl.emptySet()) {
								c = (Collection<Object>) fieldValue;
							} else {
								c = (Collection<Object>) fieldValue.getClass().getMethod("clone").invoke(fieldValue);
								c.clear();
								for (Object o : (Collection<Object>) fieldValue) {
									c.add(clone(o, cloningContext));
								}
							}
							f.set(result, c);

						} else if (fieldValue instanceof Map) {
							Map<Object, Object> m = (Map<Object, Object>) fieldValue.getClass().getMethod("clone").invoke(fieldValue);
							f.set(result, m);
							for (Entry<?, ?> e : ((Map<?, ?>) fieldValue).entrySet()) {
								m.put(e.getKey(), clone(e.getValue(), cloningContext));
							}
						} else if ((object instanceof CtReference) && (fieldValue instanceof CtElement) && !(fieldValue instanceof CtReference)) {
							f.set(result, fieldValue);
						} else {
							f.set(result, clone(f.get(object), cloningContext));
						}
					}
				}
			}
			if (result instanceof CtElement) {
				cloningContext.pop();
				if (cloningContext.isEmpty()) {
					((CtElement) result).setParent(null);
				} else {
					((CtElement) result).setParent(cloningContext.peek());
				}
			}
		} catch (Exception e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}
		return result;

	}

	public <A extends Annotation> CtAnnotation<A> createAnnotation() {
		CtAnnotation<A> e = new CtAnnotationImpl<A>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T extends Annotation> CtAnnotationType<T> createAnnotationType() {
		CtAnnotationType<T> e = new CtAnnotationTypeImpl<T>();
		e.setFactory(getMainFactory());
		e.setParent(getMainFactory().Package().getRootPackage());
		return e;
	}

	public CtAnonymousExecutable createAnonymousExecutable() {
		CtAnonymousExecutable e = new CtAnonymousExecutableImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtArrayRead<T> createArrayRead() {
		CtArrayRead<T> e = new CtArrayReadImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtArrayWrite<T> createArrayWrite() {
		CtArrayWrite<T> e = new CtArrayWriteImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtArrayTypeReference<T> createArrayTypeReference() {
		CtArrayTypeReference<T> e = new CtArrayTypeReferenceImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtAssert<T> createAssert() {
		CtAssert<T> e = new CtAssertImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T, A extends T> CtAssignment<T, A> createAssignment() {
		CtAssignment<T, A> e = new CtAssignmentImpl<T, A>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtBinaryOperator<T> createBinaryOperator() {
		CtBinaryOperator<T> e = new CtBinaryOperatorImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <R> CtBlock<R> createBlock() {
		CtBlock<R> e = new CtBlockImpl<R>();
		e.setFactory(getMainFactory());
		return e;
	}

	public CtBreak createBreak() {
		CtBreak e = new CtBreakImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	public <S> CtCase<S> createCase() {
		CtCase<S> e = new CtCaseImpl<S>();
		e.setFactory(getMainFactory());
		return e;
	}

	public CtCatch createCatch() {
		CtCatch e = new CtCatchImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtClass<T> createClass() {
		CtClass<T> e = new CtClassImpl<T>();
		e.setFactory(getMainFactory());
		e.setParent(getMainFactory().Package().getRootPackage());
		return e;
	}

	public <T> CtConditional<T> createConditional() {
		CtConditional<T> e = new CtConditionalImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtConstructor<T> createConstructor() {
		CtConstructor<T> e = new CtConstructorImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public CtContinue createContinue() {
		CtContinue e = new CtContinueImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	public CtDo createDo() {
		CtDo e = new CtDoImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T extends Enum<?>> CtEnum<T> createEnum() {
		CtEnum<T> e = new CtEnumImpl<T>();
		e.setFactory(getMainFactory());
		e.setParent(getMainFactory().Package().getRootPackage());
		return e;
	}

	public <T> CtExecutableReference<T> createExecutableReference() {
		CtExecutableReference<T> e = new CtExecutableReferenceImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtField<T> createField() {
		CtField<T> e = new CtFieldImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtFieldRead<T> createFieldRead() {
		CtFieldRead<T> e = new CtFieldReadImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtFieldWrite<T> createFieldWrite() {
		CtFieldWrite<T> e = new CtFieldWriteImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtAnnotationFieldAccess<T> createAnnotationFieldAccess() {
		CtAnnotationFieldAccess<T> e = new CtAnnotationFieldAccessImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtFieldReference<T> createFieldReference() {
		CtFieldReference<T> e = new CtFieldReferenceImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public CtFor createFor() {
		CtFor e = new CtForImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	public CtForEach createForEach() {
		CtForEach e = new CtForEachImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	public CtIf createIf() {
		CtIf e = new CtIfImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtInterface<T> createInterface() {
		CtInterface<T> e = new CtInterfaceImpl<T>();
		e.setFactory(getMainFactory());
		e.setParent(getMainFactory().Package().getRootPackage());
		return e;
	}

	public <T> CtInvocation<T> createInvocation() {
		CtInvocation<T> e = new CtInvocationImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtLiteral<T> createLiteral() {
		CtLiteral<T> e = new CtLiteralImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtLocalVariable<T> createLocalVariable() {
		CtLocalVariable<T> e = new CtLocalVariableImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtLocalVariableReference<T> createLocalVariableReference() {
		CtLocalVariableReference<T> e = new CtLocalVariableReferenceImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtCatchVariable<T> createCatchVariable() {
		CtCatchVariable<T> e = new CtCatchVariableImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtCatchVariableReference<T> createCatchVariableReference() {
		CtCatchVariableReference<T> e = new CtCatchVariableReferenceImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtMethod<T> createMethod() {
		CtMethod<T> e = new CtMethodImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtNewArray<T> createNewArray() {
		CtNewArray<T> e = new CtNewArrayImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtConstructorCall<T> createConstructorCall() {
		CtConstructorCall<T> e = new CtConstructorCallImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtNewClass<T> createNewClass() {
		CtNewClass<T> e = new CtNewClassImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtLambda<T> createLambda() {
		CtLambda<T> e = new CtLambdaImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T, E extends CtExpression<?>> CtExecutableReferenceExpression<T, E> createExecutableReferenceExpression() {
		CtExecutableReferenceExpression<T, E> e = new CtExecutableReferenceExpressionImpl<T, E>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T, A extends T> CtOperatorAssignment<T, A> createOperatorAssignment() {
		CtOperatorAssignment<T, A> e = new CtOperatorAssignmentImpl<T, A>();
		e.setFactory(getMainFactory());
		return e;
	}

	public CtPackage createPackage() {
		CtPackage e = new CtPackageImpl();
		e.setFactory(getMainFactory());
		e.setParent(getMainFactory().Package().getRootPackage());
		return e;
	}

	public CtPackageReference createPackageReference() {
		CtPackageReference e = new CtPackageReferenceImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtParameter<T> createParameter() {
		CtParameter<T> e = new CtParameterImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtParameterReference<T> createParameterReference() {
		CtParameterReference<T> e = new CtParameterReferenceImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <R> CtReturn<R> createReturn() {
		CtReturn<R> e = new CtReturnImpl<R>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <R> CtStatementList createStatementList() {
		CtStatementList e = new CtStatementListImpl<R>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <S> CtSwitch<S> createSwitch() {
		CtSwitch<S> e = new CtSwitchImpl<S>();
		e.setFactory(getMainFactory());
		return e;
	}

	public CtSynchronized createSynchronized() {
		CtSynchronized e = new CtSynchronizedImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	public CtThrow createThrow() {
		CtThrow e = new CtThrowImpl();
		e.setFactory(getMainFactory());
		return e;
	}

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

	public CtTypeParameter createTypeParameter() {
		CtTypeParameter e = new CtTypeParameterImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	public CtTypeParameterReference createTypeParameterReference() {
		CtTypeParameterReference e = new CtTypeParameterReferenceImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtTypeReference<T> createTypeReference() {
		CtTypeReference<T> e = new CtTypeReferenceImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	@Override
	public <T> CtTypeAccess<T> createTypeAccess() {
		CtTypeAccess<T> e = new CtTypeAccessImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtUnaryOperator<T> createUnaryOperator() {
		CtUnaryOperator<T> e = new CtUnaryOperatorImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtVariableRead<T> createVariableRead() {
		CtVariableRead<T> e = new CtVariableReadImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtVariableWrite<T> createVariableWrite() {
		CtVariableWrite<T> e = new CtVariableWriteImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtCodeSnippetExpression<T> createCodeSnippetExpression() {
		CtCodeSnippetExpression<T> e = new CtCodeSnippetExpressionImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public CtCodeSnippetStatement createCodeSnippetStatement() {
		CtCodeSnippetStatement e = new CtCodeSnippetStatementImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	public CtWhile createWhile() {
		CtWhile e = new CtWhileImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	public Factory getMainFactory() {
		return mainFactory;
	}

	public void setMainFactory(Factory mainFactory) {
		this.mainFactory = mainFactory;
	}

	public SourcePosition createSourcePosition(CompilationUnit compilationUnit, int startDeclaration, int startSource, int end, int[] lineSeparatorPositions) {
		return new SourcePositionImpl(compilationUnit, startDeclaration, startSource, end, lineSeparatorPositions);
	}

	public CompilationUnit createCompilationUnit() {
		CompilationUnit cu = new CompilationUnitImpl();
		cu.setFactory(getMainFactory());
		return cu;
	}

	public CompilationUnit createVirtualCompilationUnit() {
		CompilationUnit cu = new CompilationUnitVirtualImpl();
		cu.setFactory(getMainFactory());
		return cu;
	}

	public <T> CtThisAccess<T> createThisAccess() {
		CtThisAccess<T> e = new CtThisAccessImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtSuperAccess<T> createSuperAccess() {
		CtSuperAccess<T> e = new CtSuperAccessImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

}
