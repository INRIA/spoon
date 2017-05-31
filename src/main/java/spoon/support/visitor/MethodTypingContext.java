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
package spoon.support.visitor;

import static spoon.support.visitor.ClassTypingContext.getTypeReferences;

import java.util.List;

import spoon.SpoonException;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * For the scope method or constructor and super type hierarchy of it's declaring type,
 * it is able to adapt type parameters.
 *
 * https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.4.4
 * Where two methods or constructors M and N have the same type parameters {@link #hasSameMethodFormalTypeParameters(CtFormalTypeDeclarer)},
 * a type mentioned in N can be adapted to the type parameters of M
 */
public class MethodTypingContext extends AbstractTypingContext {

	private CtFormalTypeDeclarer scopeMethod;
	private List<CtTypeReference<?>> actualTypeArguments;
	private ClassTypingContext classTypingContext;

	public MethodTypingContext() {
	}

	@Override
	public CtFormalTypeDeclarer getAdaptationScope() {
		return scopeMethod;
	}

	public MethodTypingContext setMethod(CtMethod<?> scopeMethod) {
		if (classTypingContext != null) {
			//assure that scopeMethod fits to required classTypingContext
			scopeMethod = classTypingContext.adaptMethod(scopeMethod);
		}
		setScopeMethod(scopeMethod);
		actualTypeArguments = getTypeReferences(scopeMethod.getFormalCtTypeParameters());
		return this;
	}

	public MethodTypingContext setConstructor(CtConstructor<?> scopeConstructor) {
		setScopeMethod(scopeConstructor);
		actualTypeArguments = getTypeReferences(scopeConstructor.getFormalCtTypeParameters());
		return this;
	}

	@Override
	public ClassTypingContext getEnclosingGenericTypeAdapter() {
		if (classTypingContext == null && scopeMethod != null) {
			classTypingContext = new ClassTypingContext(getScopeMethodDeclaringType());
		}
		return classTypingContext;
	}

	public MethodTypingContext setClassTypingContext(ClassTypingContext classTypingContext) {
		checkSameTypingContext(classTypingContext, scopeMethod);
		this.classTypingContext = classTypingContext;
		return this;
	}

	public MethodTypingContext setInvocation(CtInvocation<?> invocation) {
		if (classTypingContext == null) {
			CtExpression<?> target = invocation.getTarget();
			if (target != null) {
				CtTypeReference<?> targetTypeRef = target.getType();
				if (targetTypeRef != null) {
					classTypingContext = new ClassTypingContext(targetTypeRef);
				}
			}
		}
		setExecutableReference(invocation.getExecutable());
		return this;
	}

	public MethodTypingContext setExecutableReference(CtExecutableReference<?> execRef) {
		this.actualTypeArguments = execRef.getActualTypeArguments();
		setScopeMethod((CtFormalTypeDeclarer) execRef.getExecutableDeclaration());
		if (classTypingContext == null) {
			CtTypeReference<?> declaringTypeRef = execRef.getDeclaringType();
			if (declaringTypeRef != null) {
				classTypingContext = new ClassTypingContext(declaringTypeRef);
			}
		}
		return this;
	}

	/**
	 * Adapts `typeParam` to the {@link CtTypeReference}
	 * of scope of this {@link MethodTypingContext}
	 * In can be {@link CtTypeParameterReference} again - depending actual type arguments of this {@link MethodTypingContext}.
	 *
	 * @param typeParam to be resolved {@link CtTypeParameter}
	 * @return {@link CtTypeReference} or {@link CtTypeParameterReference} adapted to scope of this {@link MethodTypingContext}
	 *  or null if `typeParam` cannot be adapted to target `scope`
	 */
	@Override
	protected CtTypeReference<?> adaptTypeParameter(CtTypeParameter typeParam) {
		CtFormalTypeDeclarer typeParamDeclarer = typeParam.getTypeParameterDeclarer();
		if (typeParamDeclarer instanceof CtType<?>) {
			return getEnclosingGenericTypeAdapter().adaptType(typeParam);
		}
		//only method to method or constructor to constructor can be adapted
		if (typeParamDeclarer instanceof CtMethod<?>) {
			if ((scopeMethod instanceof CtMethod<?>) == false) {
				return null;
			}
		} else if (typeParamDeclarer instanceof CtConstructor<?>) {
			if ((scopeMethod instanceof CtConstructor<?>) == false) {
				return null;
			}
		} else {
			throw new SpoonException("Unexpected type parameter declarer");
		}
		//the typeParamDeclarer is method or constructor
		/*
		 *
		 * Two methods or constructors M and N have the same type parameters if both of the following are true:
		 * 1) M and N have same number of type parameters (possibly zero).
		 * 2) Where A1, ..., An are the type parameters of M and B1, ..., Bn are the type parameters of N, let T=[B1:=A1, ..., Bn:=An].
		 * Then, for all i (1 ≤ i ≤ n), the bound of Ai is the same type as T applied to the bound of Bi.
		 */
		if (hasSameMethodFormalTypeParameters(typeParamDeclarer) == false) {
			//the methods formal type parameters are different. We cannot adapt such parameters
			return null;
		}
		int typeParamPosition = typeParamDeclarer.getFormalCtTypeParameters().indexOf(typeParam);
		return actualTypeArguments.get(typeParamPosition);
	}

	/**
	 * https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.4.4
	 *
	 * Formal type parameters of method are same if
	 * 1) both methods have same number of formal type parameters
	 * 2) bounds of Formal type parameters are after adapting same types
	 *
	 * @return true if formal type parameters of method are same
	 */
	public boolean hasSameMethodFormalTypeParameters(CtFormalTypeDeclarer typeParamDeclarer) {
		List<CtTypeParameter> thisTypeParameters = scopeMethod.getFormalCtTypeParameters();
		List<CtTypeParameter> thatTypeParameters = typeParamDeclarer.getFormalCtTypeParameters();
		if (thisTypeParameters.size() != thatTypeParameters.size()) {
			return false;
		}
		//the methods has same count of formal parameters
		//check that bounds of formal type parameters are same after adapting
		for (int i = 0; i < thisTypeParameters.size(); i++) {
			if (isSameMethodFormalTypeParameter(thisTypeParameters.get(i), thatTypeParameters.get(i)) == false) {
				return false;
			}
		}
		return true;
	}

	private boolean isSameMethodFormalTypeParameter(CtTypeParameter scopeParam, CtTypeParameter superParam) {
		CtTypeReference<?> scopeBound = getBound(scopeParam);
		CtTypeReference<?> superBoundAdapted = adaptType(getBound(superParam));
		if (superBoundAdapted == null) {
			return false;
		}
		return scopeBound.getQualifiedName().equals(superBoundAdapted.getQualifiedName());
	}

	private static CtTypeReference<?> getBound(CtTypeParameter typeParam) {
		CtTypeReference<?> bound = typeParam.getSuperclass();
		if (bound == null) {
			bound = typeParam.getFactory().Type().OBJECT;
		}
		return bound;
	}

	private CtType<?> getScopeMethodDeclaringType() {
		if (scopeMethod != null) {
			return scopeMethod.getDeclaringType();
		}
		throw new SpoonException("scopeMethod is not assigned");
	}

	private void setScopeMethod(CtFormalTypeDeclarer executable) {
		checkSameTypingContext(classTypingContext, executable);
		scopeMethod = executable;
	}

	private void checkSameTypingContext(ClassTypingContext ctc, CtFormalTypeDeclarer executable) {
		if (ctc != null && executable != null) {
			CtType<?> scope = executable.getDeclaringType();
			if (scope == null) {
				throw new SpoonException("Cannot use executable without declaring type as scope of method typing context");
			}
			if (scope != ctc.getAdaptationScope()) {
				throw new SpoonException("Declaring type of executable is not same like scope of classTypingContext provided for method typing context");
			}
		}
	}
}
