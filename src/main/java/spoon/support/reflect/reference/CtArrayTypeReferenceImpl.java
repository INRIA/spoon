/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.reference;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.SpoonClassNotFoundException;

import java.lang.reflect.Array;

import static spoon.reflect.path.CtRole.TYPE;

public class CtArrayTypeReferenceImpl<T> extends CtTypeReferenceImpl<T> implements CtArrayTypeReference<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = TYPE)
	CtTypeReference<?> componentType;

	public CtArrayTypeReferenceImpl() {
		setDeclarationKind(DeclarationKind.TYPE);
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtArrayTypeReference(this);
	}

	@Override
	public CtTypeReference<?> getComponentType() {
		if (componentType == null) {
			// a sensible default component type to facilitate object creation and testing
			componentType = getFactory().Type().objectType();
			componentType.setParent(this);
		}
		return componentType;
	}

	@Override
	public CtTypeReference<?> getArrayType() {
		return getLastComponentTypeReference(componentType);
	}

	private CtTypeReference<?> getLastComponentTypeReference(CtTypeReference<?> component) {
		return component instanceof CtArrayTypeReference ? getLastComponentTypeReference(((CtArrayTypeReference) component).getComponentType()) : component;
	}

	@Override
	public <C extends CtArrayTypeReference<T>> C setComponentType(CtTypeReference<?> componentType) {
		if (componentType != null) {
			componentType.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, TYPE, componentType, this.componentType);
		this.componentType = componentType;
		return (C) this;
	}

	@Override
	public String getSimpleName() {
		return getComponentType().getSimpleName() + "[]";
	}

	@Override
	public <T extends CtReference> T setSimpleName(String simplename) {
		return (T) this;
	}

	@Override
	public String getQualifiedName() {
		return getComponentType().getQualifiedName() + "[]";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getActualClass() {
		Class<?> c = getComponentType().getActualClass();
		if (c == null) {
			throw new SpoonClassNotFoundException("you should never call getActualClass! (" + getComponentType().getQualifiedName() + " not found in the classpath)", null);
		}
		return (Class<T>) Array.newInstance(c, 0).getClass();
	}

	@Override
	public int getDimensionCount() {
		if (getComponentType() instanceof CtArrayTypeReference) {
			return ((CtArrayTypeReference<?>) getComponentType()).getDimensionCount() + 1;
		}
		return 1;
	}

	@Override
	public CtTypeReference<?> getTypeErasure() {
		CtTypeReference<?> originCT = getComponentType();
		CtTypeReference<?> erasedCT = originCT.getTypeErasure();
		if (originCT == erasedCT) {
			return this;
		}
		CtArrayTypeReference<?> erased = this.clone();
		erased.setComponentType(erasedCT);
		return erased;
	}

	@Override
	public CtArrayTypeReference<T> clone() {
		return (CtArrayTypeReference<T>) super.clone();
	}

	@Override
	public boolean isSimplyQualified() {
		if (componentType != null) {
			return componentType.isSimplyQualified();
		}
		return false;
	}

	@Override
	public CtArrayTypeReferenceImpl<T> setSimplyQualified(boolean isSimplyQualified) {
		if (componentType != null) {
			componentType.setSimplyQualified(isSimplyQualified);
		}
		return this;
	}

	public enum DeclarationKind {
		/**
		 * Brackets are after type.
		 * int[] array;
		 */
		TYPE,

		/**
		 * Brackets are after identifier.
		 * int array[];
		 */
		IDENTIFIER,

		// We do not consider declarations where square brackets are after type _and_ identifier.
		// For example, int[] array[]. See https://github.com/INRIA/spoon/issues/4315#issuecomment-991894796.
	}

	/**
	 * Sets the kind of array declaration.
	 *
	 * @param declarationKind one of {@link DeclarationKind}
	 * @param <C> type of this instance
	 * @return this instance
	 */
	public <C extends CtArrayTypeReference<T>> C setDeclarationKind(DeclarationKind declarationKind) {
		this.putMetadata(DeclarationKind.class.getSimpleName(), declarationKind);
		return (C) this;
	}

	/**
	 * Gets the kind of array declaration.
	 *
	 * @return {@link DeclarationKind} of this instance
	 */
	public DeclarationKind getDeclarationKind() {
		return (DeclarationKind) this.getMetadata(DeclarationKind.class.getSimpleName());
	}

	@Override
	public boolean isGenerics() {
		return getArrayType().isGenerics();
	}
}
