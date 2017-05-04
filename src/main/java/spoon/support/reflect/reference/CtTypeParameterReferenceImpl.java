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
package spoon.support.reflect.reference;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.UnsettableProperty;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

public class CtTypeParameterReferenceImpl extends CtTypeReferenceImpl<Object> implements CtTypeParameterReference {
	private static final long serialVersionUID = 1L;

	CtTypeReference<?> superType;

	boolean upper = true;

	public CtTypeParameterReferenceImpl() {
		super();
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtTypeParameterReference(this);
	}

	@Override
	public boolean isUpper() {
		return upper;
	}

	@Override
	public <T extends CtTypeParameterReference> T setBounds(List<CtTypeReference<?>> bounds) {
		if (bounds == null || bounds.isEmpty()) {
			setBoundingType(null);
			return (T) this;
		}
		if (getBoundingType() instanceof CtIntersectionTypeReference<?>) {
			getBoundingType().asCtIntersectionTypeReference().setBounds(bounds);
		} else if (bounds.size() > 1) {
			final List<CtTypeReference<?>> refs = new ArrayList<>();
			refs.addAll(bounds);
			setBoundingType(getFactory().Type().createIntersectionTypeReferenceWithBounds(refs));
		} else {
			setBoundingType(bounds.get(0));
		}
		return (T) this;
	}

	@Override
	public <T extends CtTypeParameterReference> T setUpper(boolean upper) {
		this.upper = upper;
		return (T) this;
	}

	@Override
	public boolean isGenerics() {
		return true;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<Object> getActualClass() {
		if (isUpper()) {
			if (getBoundingType() == null) {
				return (Class<Object>) getTypeErasure().getActualClass();
			}
			return (Class<Object>) getBoundingType().getActualClass();
		}
		return null;
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
	public boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		return false;
	}

	@Override
	public <T extends CtTypeParameterReference> T addBound(CtTypeReference<?> bound) {
		if (bound == null) {
			return (T) this;
		}
		if (getBoundingType() == null) {
			setBoundingType(bound);
		} else if (getBoundingType() instanceof CtIntersectionTypeReference<?>) {
			getBoundingType().asCtIntersectionTypeReference().addBound(bound);
		} else {
			final List<CtTypeReference<?>> refs = new ArrayList<>();
			refs.add(getBoundingType());
			refs.add(bound);
			setBoundingType(getFactory().Type().createIntersectionTypeReferenceWithBounds(refs));
		}
		return (T) this;
	}

	@Override
	public boolean removeBound(CtTypeReference<?> bound) {
		if (bound == null || getBoundingType() == null) {
			return false;
		}
		if (getBoundingType() instanceof CtIntersectionTypeReference<?>) {
			return getBoundingType().asCtIntersectionTypeReference().removeBound(bound);
		} else {
			setBoundingType(null);
			return true;
		}
	}

	@Override
	public CtTypeReference<?> getBoundingType() {
		return superType;
	}

	@Override
	public <T extends CtTypeParameterReference> T setBoundingType(CtTypeReference<?> superType) {
		if (superType != null) {
			superType.setParent(this);
		}
		this.superType = superType;
		return (T) this;
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
		if (parent instanceof CtExecutableReference) {
			CtElement parent2 = parent.getParent();
			if (parent2 instanceof CtMethod) {
				e = parent2;
			} else {
				e = ((CtExecutableReference<?>) parent).getDeclaringType().getTypeDeclaration();
			}
		} else {
			e = e.getParent(CtFormalTypeDeclarer.class);
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
		return getDeclaration().isSubtypeOf(type);
	}

	@Override
	public CtTypeParameterReference clone() {
		return (CtTypeParameterReference) super.clone();
	}
}
