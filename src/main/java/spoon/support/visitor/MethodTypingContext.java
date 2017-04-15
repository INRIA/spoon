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

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * For the `scopeMethod` and super type hierarchy of it's declaring type,
 * it is able to adapt type parameters
 * and compare method signatures.
 */
public class MethodTypingContext implements GenericTypeAdapter {

	private CtExecutable<?> scopeMethod;
	private List<CtTypeReference<?>> actualTypeArguments;
	private ClassTypingContext classTypingContext;

	public MethodTypingContext() {
	}

	public MethodTypingContext setMethod(CtMethod<?> scopeMethod) {
		this.scopeMethod = scopeMethod;
		actualTypeArguments = getTypeReferences(scopeMethod.getFormalCtTypeParameters());
		return this;
	}

	public MethodTypingContext setConstructor(CtConstructor<?> scopeConstructor) {
		this.scopeMethod = scopeConstructor;
		actualTypeArguments = getTypeReferences(scopeConstructor.getFormalCtTypeParameters());
		return this;
	}

	@Override
	public ClassTypingContext getEnclosingGenericTypeAdapter() {
		if (classTypingContext == null && scopeMethod != null) {
			classTypingContext = new ClassTypingContext(((CtTypeMember) scopeMethod).getDeclaringType());
		}
		return classTypingContext;
	}

	public MethodTypingContext setClassTypingContext(ClassTypingContext classTypingContext) {
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
		this.scopeMethod = (CtMethod<?>) execRef.getDeclaration();
		if (classTypingContext == null) {
			CtTypeReference<?> declaringTypeRef = execRef.getDeclaringType();
			if (declaringTypeRef != null) {
				classTypingContext = new ClassTypingContext(declaringTypeRef);
			}
		}
		return this;
	}

	@Override
	public CtTypeReference<?> adaptType(CtTypeInformation type) {
		if (type instanceof CtTypeReference<?>) {
			if (type instanceof CtTypeParameterReference) {
				return adaptTypeParameter(((CtTypeParameterReference) type).getDeclaration());
			}
			return (CtTypeReference<?>) type;
		}
		if (type instanceof CtTypeParameter) {
			return adaptTypeParameter((CtTypeParameter) type);
		}
		return ((CtType<?>) type).getReference();
	}

	/**
	 * @param thatMethod - to be checked method
	 * @return true if scope method overrides `thatMethod`
	 */
	public boolean isOverriding(CtMethod<?> thatMethod) {
		CtType<?> thatDeclType = thatMethod.getDeclaringType();
		if (getEnclosingGenericTypeAdapter().isSubtypeOf(thatDeclType.getReference()) == false) {
			return false;
		}
		return isSubSignature(thatMethod);
	}

	/**
	 * scope method is subsignature of thatMethod if either
	 * A) scope method is same signature like thatMethod
	 * B) scope method is same signature like type erasure of thatMethod
	 * See https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.4.2
	 *
	 * @param thatMethod - the checked method
	 * @return true if scope method is subsignature of thatMethod
	 */
	public boolean isSubSignature(CtMethod<?> thatMethod) {
		return checkSignature(thatMethod, true);
	}

	/**
	 * The same signature is the necessary condition for method A overrides method B.
	 * @param thatMethod - the checked method
	 * @return true if this method and `thatMethod` has same signature
	 */
	public boolean isSameSignature(CtMethod<?> thatMethod) {
		return checkSignature(thatMethod, false);
	}

	private boolean checkSignature(CtMethod<?> thatMethod, boolean canTypeErasure) {
		//https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.4.2
		if (mightBeSameSignature(thatMethod) == false) {
			return false;
		}
		List<CtTypeParameter> formalCtTypeParameters = ((CtFormalTypeDeclarer) scopeMethod).getFormalCtTypeParameters();
		List<CtTypeParameter> thatTypeParameters = thatMethod.getFormalCtTypeParameters();
		boolean useTypeErasure = false;
		if (formalCtTypeParameters.size() == thatTypeParameters.size()) {
			//the methods has same count of formal parameters
			//check that formal type parameters are same
			for (int i = 0; i < formalCtTypeParameters.size(); i++) {
				if (isSameMethodFormalTypeParameter(formalCtTypeParameters.get(i), thatTypeParameters.get(i)) == false) {
					return false;
				}
			}
		} else {
			//the methods has different count of formal type parameters.
			if (canTypeErasure == false) {
				//type erasure is not allowed. So not generic methods cannot match with generic methods
				return false;
			}
			//non generic method can override a generic one if type erasure is allowed
			if (formalCtTypeParameters.isEmpty() == false) {
				//scope methods has some parameters. It is generic too, it is not a subsignature of that method
				return false;
			}
			//scope method has zero formal type parameters. It is not generic.
			useTypeErasure = true;
		}
		List<CtTypeReference<?>> thisParameterTypes = getParameterTypes(scopeMethod.getParameters());
		List<CtTypeReference<?>> thatParameterTypes = getParameterTypes(thatMethod.getParameters());
		//check that parameters are same after adapted to same scope
		for (int i = 0; i < thisParameterTypes.size(); i++) {
			CtTypeReference<?> thisType = thisParameterTypes.get(i);
			CtTypeReference<?> thatType = thatParameterTypes.get(i);
			if (useTypeErasure) {
				if (thatType instanceof CtTypeParameterReference) {
					thatType = ((CtTypeParameterReference) thatType).getTypeErasure();
				}
			} else {
				thatType = adaptType(thatType);
			}
			if (thatType == null) {
				//the type cannot be adapted.
				return false;
			}
			if (thisType.equals(thatType) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if thatMethod might have same signature like scope method.
	 * Check only attributes, which does not need adapting or type erasure
	 * @param thatMethod the to be checked method
	 * @return true if `thatMethod` might have same signature like scope method
	 */
	private boolean mightBeSameSignature(CtMethod<?> thatMethod) {
		if (scopeMethod == thatMethod) {
			return true;
		}
		if ((thatMethod instanceof CtMethod) == false) {
			return false;
		}
		if (thatMethod.getSimpleName().equals(scopeMethod.getSimpleName()) == false) {
			return false;
		}
		if (scopeMethod.getParameters().size() != thatMethod.getParameters().size()) {
			//the methods has different count of parameters they cannot have same signature
			return false;
		}
		if (((CtFormalTypeDeclarer) scopeMethod).getFormalCtTypeParameters().size() != thatMethod.getFormalCtTypeParameters().size()) {
			//the methods has different count of formal type parameters they cannot have same signature
			return false;
		}
		return true;
	}

	/**
	 * adapts `typeParam` to the {@link CtTypeReference}
	 * of scope of this {@link MethodTypingContext}
	 * In can be {@link CtTypeParameterReference} again - depending actual type arguments of this {@link MethodTypingContext}.
	 *
	 * Note: this method is not checking whether declarer method is overridden by scope method,
	 * so it it may adapt parameters of potentially override equivalent methods.
	 * Use {@link #checkSignature(CtMethod, boolean)} to check if method overrides another method
	 *
	 * @param superParam to be resolved {@link CtTypeParameter}
	 * @return {@link CtTypeReference} or {@link CtTypeParameterReference} adapted to scope of this {@link MethodTypingContext}
	 *  or null if `typeParam` cannot be adapted to target `scope`
	 */
	private CtTypeReference<?> adaptTypeParameter(CtTypeParameter superParam) {
		CtFormalTypeDeclarer superDeclarer = superParam.getTypeParameterDeclarer();
		if (superDeclarer instanceof CtType<?>) {
			return getEnclosingGenericTypeAdapter().adaptType(superParam);
		}
		if (superDeclarer instanceof CtMethod) {
			CtMethod<?> superMethod = (CtMethod<?>) superDeclarer;
			/*
			 * The type parameters of generic executables are same (can be adapted to each other)
			 * 1) the methods are same or if methods overrides each other
			 * 2) they are declared on same position
			 * 3) they have same bound after adapting
			 * See https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.4.4
			 */
			if (mightBeSameSignature(superMethod) == false) {
				//the methods are different. Cannot adapt parameter
				return null;
			}
			/*
			 * we do not know 100% if thatMethod overrides scope method,
			 * but we cannot detect it here, because that check would need adapting of type parameters,
			 * which would cause StackOverflowError
			 */
			int superParamPosition = superMethod.getFormalCtTypeParameters().indexOf(superParam);
			CtTypeParameter scopeParam = ((CtFormalTypeDeclarer) scopeMethod).getFormalCtTypeParameters().get(superParamPosition);
			if (isSameMethodFormalTypeParameter(scopeParam, superParam) == false) {
				//the argument cannot be adapted if bounds are not same
				return null;
			}
			return actualTypeArguments.get(superParamPosition);
		}
		return null;
	}

	/**
	 * Formal type parameters of method are same if
	 * 1) both methods have same number of formal type parameters
	 * 2) bounds of Formal type parameters are after adapting same types
	 *
	 * This method checks only point (2). Point (1) has to be already checked by caller
	 * @return if bounds are same after adapting
	 */
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

	private static List<CtTypeReference<?>> getParameterTypes(List<CtParameter<?>> params) {
		List<CtTypeReference<?>> types = new ArrayList<>(params.size());
		for (CtParameter<?> param : params) {
			types.add(param.getType());
		}
		return types;
	}
}
