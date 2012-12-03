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

package spoon.support;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import spoon.reflect.CoreFactory;
import spoon.reflect.Factory;
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
import spoon.support.reflect.code.CtArrayAccessImpl;
import spoon.support.reflect.code.CtAssertImpl;
import spoon.support.reflect.code.CtAssignmentImpl;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtBreakImpl;
import spoon.support.reflect.code.CtCaseImpl;
import spoon.support.reflect.code.CtCatchImpl;
import spoon.support.reflect.code.CtCodeSnippetExpressionImpl;
import spoon.support.reflect.code.CtCodeSnippetStatementImpl;
import spoon.support.reflect.code.CtConditionalImpl;
import spoon.support.reflect.code.CtContinueImpl;
import spoon.support.reflect.code.CtDoImpl;
import spoon.support.reflect.code.CtFieldAccessImpl;
import spoon.support.reflect.code.CtForEachImpl;
import spoon.support.reflect.code.CtForImpl;
import spoon.support.reflect.code.CtIfImpl;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtLiteralImpl;
import spoon.support.reflect.code.CtLocalVariableImpl;
import spoon.support.reflect.code.CtNewArrayImpl;
import spoon.support.reflect.code.CtNewClassImpl;
import spoon.support.reflect.code.CtOperatorAssignmentImpl;
import spoon.support.reflect.code.CtReturnImpl;
import spoon.support.reflect.code.CtStatementListImpl;
import spoon.support.reflect.code.CtSwitchImpl;
import spoon.support.reflect.code.CtSynchronizedImpl;
import spoon.support.reflect.code.CtThrowImpl;
import spoon.support.reflect.code.CtTryImpl;
import spoon.support.reflect.code.CtUnaryOperatorImpl;
import spoon.support.reflect.code.CtVariableAccessImpl;
import spoon.support.reflect.code.CtWhileImpl;
import spoon.support.reflect.cu.CompilationUnitImpl;
import spoon.support.reflect.cu.SourcePositionImpl;
import spoon.support.reflect.declaration.CompilationUnitVirtualImpl;
import spoon.support.reflect.declaration.CtAnnotationImpl;
import spoon.support.reflect.declaration.CtAnnotationTypeImpl;
import spoon.support.reflect.declaration.CtAnonymousExecutableImpl;
import spoon.support.reflect.declaration.CtClassImpl;
import spoon.support.reflect.declaration.CtConstructorImpl;
import spoon.support.reflect.declaration.CtEnumImpl;
import spoon.support.reflect.declaration.CtFieldImpl;
import spoon.support.reflect.declaration.CtInterfaceImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.support.reflect.declaration.CtPackageImpl;
import spoon.support.reflect.declaration.CtParameterImpl;
import spoon.support.reflect.declaration.CtTypeParameterImpl;
import spoon.support.reflect.reference.CtArrayTypeReferenceImpl;
import spoon.support.reflect.reference.CtExecutableReferenceImpl;
import spoon.support.reflect.reference.CtFieldReferenceImpl;
import spoon.support.reflect.reference.CtLocalVariableReferenceImpl;
import spoon.support.reflect.reference.CtPackageReferenceImpl;
import spoon.support.reflect.reference.CtParameterReferenceImpl;
import spoon.support.reflect.reference.CtTypeParameterReferenceImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;
import spoon.support.util.ChildList;
import spoon.support.util.RtHelper;

/**
 * This class implements a default core factory for Spoon's meta-model. This
 * implementation is done with regular Java classes (POJOs).
 */
public class DefaultCoreFactory implements CoreFactory, Serializable {

	private static final long serialVersionUID = 1L;

	transient Stack<CtElement> cloningContext = new Stack<CtElement>();

	Factory mainFactory;

	/**
	 * Default constructor.
	 */
	public DefaultCoreFactory() {
	}

	@SuppressWarnings("unchecked")
	public <T> T clone(T object) {
		if (object == null)
			return null;
		T result = null;
		try {
			if (!(object instanceof CtElement || object instanceof CtReference)) {
				return object;
			}
			if (object instanceof Cloneable) {
				return (T) object.getClass().getMethod("clone").invoke(object);
			}
			if (object.getClass().isEnum()) {
				return object;
			}
			// System.err.println("cloning " + object + "["
			// + object.getClass().getSimpleName() + "]");
			result = (T) object.getClass().newInstance();
			if (result instanceof CtElement) {
				if (cloningContext.isEmpty()) {
					cloningContext.push(((CtElement) result).getParent());
				}
				cloningContext.push((CtElement) result);
			}
			for (Field f : RtHelper.getAllFields(object.getClass())) {
				// if (!clonedFields.contains(f)) {
				// clonedFields.push(f);
				f.setAccessible(true);
				if (f.getName().equals("parent")) {
					// if (!cloningContext.isEmpty()) {
					((CtElement) result).setParent(cloningContext
							.get(cloningContext.size() - 2));
					// }
				} else {
					Object fieldValue = f.get(object);
					if (!Modifier.isFinal(f.getModifiers())
							&& !Modifier.isStatic(f.getModifiers())) {
						if (fieldValue instanceof Collection) {
							// System.err.println(" cloning collection " + f+" :
							// "+cloningContext.peek().getClass().getSimpleName());
							Collection c = (Collection) fieldValue.getClass()
									.getMethod("clone").invoke(fieldValue);
							c.clear();
							f.set(result, c);
							
							if (fieldValue instanceof ChildList)
								((ChildList)c).setParent(cloningContext.peek());
							
							for (Object o : (Collection) fieldValue) {
								c.add(clone(o));
							}
						} else if (fieldValue instanceof Map) {
							// System.err.println(" cloning collection " + f+" :
							// "+cloningContext.peek().getClass().getSimpleName());
							Map m = (Map) fieldValue.getClass()
									.getMethod("clone").invoke(fieldValue);
							// m.clear();
							f.set(result, m);
							for (Entry e : ((Map<?, ?>) fieldValue).entrySet()) {
								m.put(e.getKey(), clone(e.getValue()));
							}
						} else if ((object instanceof CtReference)
								&& (fieldValue instanceof CtElement)) {

							f.set(result, fieldValue);
						} else {
							// System.err.println(" cloning field " + f+" :
							// "+cloningContext.peek().getClass().getSimpleName());
							f.set(result, clone(f.get(object)));
						}
					}
				}
				// clonedFields.pop();
			}
			// }
			if (result instanceof CtElement) {
				cloningContext.pop();
				if (cloningContext.size() == 1)
					cloningContext.pop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	@SuppressWarnings("unchecked")
	public <A extends Annotation> CtAnnotation<A> createAnnotation() {
		CtAnnotation e = new CtAnnotationImpl<A>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T extends Annotation> CtAnnotationType<T> createAnnotationType() {
		CtAnnotationType<T> e = new CtAnnotationTypeImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public CtAnonymousExecutable createAnonymousExecutable() {
		CtAnonymousExecutable e = new CtAnonymousExecutableImpl();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T, E extends CtExpression<?>> CtArrayAccess<T, E> createArrayAccess() {
		CtArrayAccess<T, E> e = new CtArrayAccessImpl<T, E>();
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

	public <T> CtFieldAccess<T> createFieldAccess() {
		CtFieldAccess<T> e = new CtFieldAccessImpl<T>();
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

	public <T> CtNewClass<T> createNewClass() {
		CtNewClass<T> e = new CtNewClassImpl<T>();
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

	public <R> CtStatementList<R> createStatementList() {
		CtStatementList<R> e = new CtStatementListImpl<R>();
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

	public <T> CtUnaryOperator<T> createUnaryOperator() {
		CtUnaryOperator<T> e = new CtUnaryOperatorImpl<T>();
		e.setFactory(getMainFactory());
		return e;
	}

	public <T> CtVariableAccess<T> createVariableAccess() {
		CtVariableAccess<T> e = new CtVariableAccessImpl<T>();
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

	public SourcePosition createSourcePosition(CompilationUnit compilationUnit,
			int start, int end, int[] lineSeparatorPositions) {
		return new SourcePositionImpl(compilationUnit, start, end,
				lineSeparatorPositions);
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

}
