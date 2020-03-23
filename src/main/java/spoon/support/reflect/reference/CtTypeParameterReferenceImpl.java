/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.reference;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Objects;

public class CtTypeParameterReferenceImpl extends CtTypeReferenceImpl<Object> implements CtTypeParameterReference {
	private static final long serialVersionUID = 1L;


	public CtTypeParameterReferenceImpl() {
	}

	@Override
	public boolean isDefaultBoundingType() {
		return (getBoundingType().equals(getFactory().Type().getDefaultBoundingType()));
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtTypeParameterReference(this);
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<Object> getActualClass() {
		return (Class<Object>) getBoundingType().getActualClass();
	}

	@Override
	@DerivedProperty
	public List<CtTypeReference<?>> getActualTypeArguments() {
		return emptyList();
	}

	@Override
	@UnsettableProperty
	public <C extends CtActualTypeContainer> C setActualTypeArguments(List<? extends CtTypeReference<?>> actualTypeArguments) {
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <C extends CtActualTypeContainer> C addActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		return false;
	}

	@Override
	@DerivedProperty
	public CtTypeReference<?> getBoundingType() {
		CtTypeParameter typeParam = getDeclaration();
		if (typeParam != null) {
			CtTypeReference<?> typeRef = typeParam.getSuperclass();
			if (typeRef != null) {
				return typeRef;
			}
		}
		return getFactory().Type().getDefaultBoundingType();
	}

	@Override
	protected AnnotatedElement getActualAnnotatedElement() {
		// this is never annotated
		return null;
	}

	@Override
	public CtTypeParameter getDeclaration() {
		if (!isParentInitialized()) {
			return null;
		}

		CtElement e = this;
		CtElement parent = getParent();

		if (parent instanceof CtTypeParameter && Objects.equals(getSimpleName(), ((CtTypeParameter) parent).getSimpleName())) {
			/*
			 * a special case of newly created (unbound) CtTypeParameterReference,
			 * whose CtTypeParameter is linked as parent - to temporary remember CtTypeParameterReference bounds
			 * See ReferenceBuilder#getTypeReference(TypeBinding)
			 */
			return (CtTypeParameter) parent;
		}

		if (parent instanceof CtTypeReference) {
			if (!parent.isParentInitialized()) {
				// we might enter in that case because of a call
				// of getSuperInterfaces() for example
				CtTypeReference typeReference = (CtTypeReference) parent;
				e = typeReference.getTypeDeclaration();
				if (e == null) {
					return null;
				}
			} else {
				parent = parent.getParent();
			}
		}

		if (parent instanceof CtExecutableReference) {
			CtExecutableReference parentExec = (CtExecutableReference) parent;
			if (Objects.nonNull(parentExec.getDeclaringType()) && !parentExec.getDeclaringType().equals(e)) {
				CtElement parent2 = parentExec.getExecutableDeclaration();
				if (parent2 instanceof CtMethod) {
					e = parent2;
				} else {
					e = e.getParent(CtFormalTypeDeclarer.class);
				}
			} else {
				e = e.getParent(CtFormalTypeDeclarer.class);
			}
		} else {
			if (!(e instanceof CtFormalTypeDeclarer)) {
				e = e.getParent(CtFormalTypeDeclarer.class);
			}
		}

		// case #1: we're a type of a method parameter, a local variable, ...
		// the strategy is to look in the parents
		// collecting all formal type declarers of the hierarchy
		while (e != null) {
			CtTypeParameter result = findTypeParamDeclaration((CtFormalTypeDeclarer) e, this.getSimpleName());
			if (result != null) {
				return result;
			}
			e = e.getParent(CtFormalTypeDeclarer.class);
		}
		return null;
	}

	private CtTypeParameter findTypeParamDeclaration(CtFormalTypeDeclarer type, String refName) {
		for (CtTypeParameter typeParam : type.getFormalCtTypeParameters()) {
			if (typeParam.getSimpleName().equals(refName)) {
				return typeParam;
			}
		}
		return null;
	}

	@Override
	public CtType<Object> getTypeDeclaration() {
		return getDeclaration();
	}

	@Override
	public CtTypeReference<?> getTypeErasure() {
		CtTypeParameter typeParam = getDeclaration();
		if (typeParam == null) {
			throw new SpoonException("Cannot resolve type erasure of the type parameter reference, which is not able to found it's declaration.");
		}
		return typeParam.getTypeErasure();
	}

	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		return getTypeDeclaration().isSubtypeOf(type);
	}

	@Override
	public CtTypeParameterReference clone() {
		return (CtTypeParameterReference) super.clone();
	}

	@Override
	public boolean isGenerics() {
		if (getDeclaration() instanceof CtTypeParameter) {
			return true;
		}
		return getBoundingType() != null && getBoundingType().isGenerics();
	}

	protected boolean isWildcard() {
		return false;
	}

	@Override
	public boolean isSimplyQualified() {
		return false;
	}

	@Override
	@UnsettableProperty
	public CtTypeParameterReferenceImpl setSimplyQualified(boolean isSimplyQualified) {
		return this;
	}
}
