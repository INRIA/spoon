/**
 * Copyright (C) 2006-2018 INRIA and contributors
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

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.SpoonClassNotFoundException;

import java.lang.reflect.Array;

import static spoon.reflect.path.CtRole.TYPE;

public class
CtArrayTypeReferenceImpl<T> extends CtTypeReferenceImpl<T> implements CtArrayTypeReference<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = TYPE)
	CtTypeReference<?> componentType;

	public CtArrayTypeReferenceImpl() {
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
}
