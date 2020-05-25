/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor;

import static spoon.support.visitor.ClassTypingContext.getTypeReferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import spoon.SpoonException;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
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

	public MethodTypingContext setMethod(CtMethod<?> method) {
		actualTypeArguments = getTypeReferences(method.getFormalCtTypeParameters());
		if (classTypingContext != null) {
			CtType<?> declType = method.getDeclaringType();
			if (declType == null) {
				throw new SpoonException("Cannot use method without declaring type as scope of method typing context");
			}
			if (classTypingContext.getAdaptationScope() != declType) {
				//the method is declared in different type. We have to adapt it to required classTypingContext
				if (classTypingContext.isSubtypeOf(declType.getReference()) == false) {
					throw new SpoonException("Cannot create MethodTypingContext for method declared in different ClassTypingContext");
				}
				/*
				 * The method is declared in an supertype of classTypingContext.
				 * Create virtual scope method by adapting generic types of supertype method to required scope
				 */
				Factory factory = method.getFactory();
				//create new scopeMethod, which is directly used during adaptation of it's parameters
				CtMethod<?> adaptedMethod = factory.Core().createMethod();
				adaptedMethod.setParent(classTypingContext.getAdaptationScope());
				adaptedMethod.setModifiers(method.getModifiers());
				adaptedMethod.setSimpleName(method.getSimpleName());

				for (CtTypeParameter typeParam : method.getFormalCtTypeParameters()) {
					CtTypeParameter newTypeParam = typeParam.clone();
					newTypeParam.setSuperclass(adaptTypeForNewMethod(typeParam.getSuperclass()));
					adaptedMethod.addFormalCtTypeParameter(newTypeParam);
				}
				//now the formal type parameters of the scopeMethod are defined, so we can use adaptType of this MethodTypingContext
				scopeMethod = adaptedMethod;
				for (CtTypeReference<? extends Throwable> thrownType : method.getThrownTypes()) {
					adaptedMethod.addThrownType((CtTypeReference<Throwable>) adaptType(thrownType.clone()));
				}
				//adapt return type
				adaptedMethod.setType((CtTypeReference) adaptType(method.getType()));
				//adapt parameters
				List<CtParameter<?>> adaptedParams = new ArrayList<>(method.getParameters().size());
				for (CtParameter<?> parameter : method.getParameters()) {
					adaptedParams.add(factory.Executable().createParameter(null,
							adaptType(parameter.getType()),
							parameter.getSimpleName()));
				}
				adaptedMethod.setParameters(adaptedParams);
				method = adaptedMethod;
			}
		}
		scopeMethod = method;
		return this;
	}

	public MethodTypingContext setConstructor(CtConstructor<?> constructor) {
		actualTypeArguments = getTypeReferences(constructor.getFormalCtTypeParameters());
		//TODO may be the constructors have to be adapted too, same like in setScopeMethod??
		checkSameTypingContext(classTypingContext, constructor);
		scopeMethod = constructor;
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
		if (classTypingContext == null) {
			CtTypeReference<?> declaringTypeRef = execRef.getDeclaringType();
			if (declaringTypeRef != null) {
				classTypingContext = new ClassTypingContext(declaringTypeRef);
			}
		}
		CtExecutable<?> exec = execRef.getExecutableDeclaration();
		if (exec == null) {
			throw new SpoonException("Cannot create MethodTypingContext from CtExecutable of CtExecutableReference is null");
		}
		if (exec instanceof CtMethod<?>) {
			setMethod((CtMethod<?>) exec);
		} else if (exec instanceof CtConstructor<?>) {
			setConstructor((CtConstructor<?>) exec);
		} else {
			throw new SpoonException("Cannot create MethodTypingContext from " + exec.getClass().getName());
		}
		this.actualTypeArguments = execRef.getActualTypeArguments();
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

	private Set<CtFormalTypeDeclarer> checkingFormalTypeParamsOf = Collections.newSetFromMap(new IdentityHashMap<>(1));

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
		if (checkingFormalTypeParamsOf.contains(typeParamDeclarer)) {
			//do not check isSameMethodFormalTypeParameter recursively
			return true;
		}
		try {
			checkingFormalTypeParamsOf.add(typeParamDeclarer);
			//the methods has same count of formal parameters
			//check that bounds of formal type parameters are same after adapting
			for (int i = 0; i < thisTypeParameters.size(); i++) {
				if (isSameMethodFormalTypeParameter(thisTypeParameters.get(i), thatTypeParameters.get(i)) == false) {
					return false;
				}
			}
		} finally {
			checkingFormalTypeParamsOf.remove(typeParamDeclarer);
		}
		return true;
	}

	private boolean isSameMethodFormalTypeParameter(CtTypeParameter scopeParam, CtTypeParameter superParam) {
		CtTypeReference<?> scopeBound = getBound(scopeParam);
		CtTypeReference<?> superBound = getBound(superParam);
		if (scopeBound.getActualTypeArguments().size() != superBound.getActualTypeArguments().size()) {
			return false;
		}

		CtTypeReference<?> superBoundAdapted = adaptType(superBound);
		if (superBoundAdapted == null) {
			return false;
		}
		return scopeBound.getQualifiedName().equals(superBoundAdapted.getQualifiedName());
	}

	/*
	 * @param declared
	 * @param typeRef
	 * @return index of type parameter in formal type parameters of `declarer` if typeRef is reference refers type parameter of that declarer.
	 *  Returns -1 if it is not.
	 */
	private int getIndexOfTypeParam(CtFormalTypeDeclarer declarer, CtTypeReference<?> typeRef) {
		if (typeRef instanceof CtTypeParameterReference) {
			CtTypeParameter typeParam = ((CtTypeParameterReference) typeRef).getDeclaration();
			if (typeParam != null) {
				if (declarer == typeParam.getTypeParameterDeclarer()) {
					return declarer.getFormalCtTypeParameters().indexOf(typeParam);
				}
			}
		}
		return -1;
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

	private CtTypeReference<?> adaptTypeForNewMethod(CtTypeReference<?> typeRef) {
		if (typeRef == null) {
			return null;
		}
		if (typeRef instanceof CtTypeParameterReference) {
			CtTypeParameterReference typeParamRef = (CtTypeParameterReference) typeRef;
			CtTypeParameter typeParam = typeParamRef.getDeclaration();
			if (typeParam == null) {
				throw new SpoonException("Declaration of the CtTypeParameter should not be null.");
			}

			if (typeParam.getTypeParameterDeclarer() instanceof CtExecutable) {
				//the parameter is declared in scope of Method or Constructor
				return typeRef.clone();
			}
		}
		//it is not type reference of scopeMethod. Adapt it using classTypingContext
		return classTypingContext.adaptType(typeRef);
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
