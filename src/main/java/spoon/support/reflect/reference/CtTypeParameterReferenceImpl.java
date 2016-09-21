/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

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
	public boolean isAssignableFrom(CtTypeReference<?> type) {
		return false;
	}

	@Override
	public boolean isGenerics() {
		return true;
	}

	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		return false;
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
				return Object.class;
			}
			return (Class<Object>) getBoundingType().getActualClass();
		}
		return null;
	}

	@Override
	public <C extends CtActualTypeContainer> C setActualTypeArguments(List<? extends CtTypeReference<?>> actualTypeArguments) {
		throw new UnsupportedOperationException("Type parameter can't have an actual type argument");
	}

	@Override
	public <C extends CtActualTypeContainer> C addActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		throw new UnsupportedOperationException("Type parameter can't have an actual type argument");
	}

	@Override
	public boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		throw new UnsupportedOperationException("Type parameter can't have an actual type argument");
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
		return getRecursiveDeclaration(this);
	}

	private CtTypeParameter getRecursiveDeclaration(CtElement element) {
		final CtFormalTypeDeclarer formalTypeDeclarer = element.getParent(CtFormalTypeDeclarer.class);
		if (formalTypeDeclarer == null) {
			return null;
		}
		for (CtTypeParameter typeParameter : formalTypeDeclarer.getFormalCtTypeParameters()) {
			if (simplename.equals(typeParameter.getSimpleName())) {
				return typeParameter;
			}
		}
		return getRecursiveDeclaration(formalTypeDeclarer);
	}

	@Override
	public CtType<Object> getTypeDeclaration() {
		return getDeclaration();
	}

	@Override
	public CtTypeParameterReference clone() {
		return (CtTypeParameterReference) super.clone();
	}
}
