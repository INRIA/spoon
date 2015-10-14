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

package spoon.support.reflect.reference;

import spoon.reflect.reference.CtGenericElementReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spoon.reflect.ModelElementContainerDefaultCapacities.TYPE_BOUNDS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.ModelElementContainerDefaultCapacities.TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY;

public class CtTypeParameterReferenceImpl extends CtTypeReferenceImpl<Object>
		implements CtTypeParameterReference {
	private static final long serialVersionUID = 1L;

	Map<CtTypeReference<?>, Boolean> bounds = new HashMap<CtTypeReference<?>, Boolean>(TYPE_BOUNDS_CONTAINER_DEFAULT_CAPACITY);

	boolean upper = true;

	public CtTypeParameterReferenceImpl() {
		super();
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtTypeParameterReference(this);
	}

	@Override
	public List<CtTypeReference<?>> getBounds() {
		return new ArrayList<CtTypeReference<?>>(bounds.keySet());
	}

	@Override
	public Map<CtTypeReference<?>, Boolean> getBoundsWithCircular() {
		return bounds;
	}

	@Override
	public boolean isUpper() {
		return upper;
	}

	@Override
	public <T extends CtTypeParameterReference> T setBounds(List<CtTypeReference<?>> bounds) {
		for (CtTypeReference<?> bound : bounds) {
			this.bounds.put(bound, false);
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
			if (getBounds().isEmpty()) {
				return Object.class;
			}
			return (Class<Object>) getBounds().get(0).getActualClass();
		}
		return null;
	}

	@Override
	public <C extends CtGenericElementReference> C addActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		if (actualTypeArguments == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			actualTypeArguments = new ArrayList<CtTypeReference<?>>(
					TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		actualTypeArguments.add(actualTypeArgument);
		return (C) this;
	}

	@Override
	public boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		return actualTypeArguments != CtElementImpl.<CtTypeReference<?>>emptyList()
				&& actualTypeArguments.remove(actualTypeArgument);
	}

	@Override
	public <T extends CtTypeParameterReference> T addBound(CtTypeReference<?> bound) {
		bounds.put(bound, false);
		return (T) this;
	}

	@Override
	public <T extends CtTypeParameterReference> T addBound(CtTypeReference<?> bound, boolean isCircular) {
		bounds.put(bound, isCircular);
		return (T) this;
	}

	@Override
	public boolean removeBound(CtTypeReference<?> bound) {
		return bounds != CtElementImpl.<CtTypeReference<?>>emptyList() && bounds.remove(bound);
	}

	@Override
	protected AnnotatedElement getActualAnnotatedElement() {
		// this is never annotated
		return null;
	}

	@Override
	public <T extends CtReference> T setSimpleName(String simplename) {
		this.simplename = simplename;
		return (T) this;
	}
}
