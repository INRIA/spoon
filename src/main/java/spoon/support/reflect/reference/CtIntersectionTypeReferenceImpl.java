/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.TYPE_BOUNDS_CONTAINER_DEFAULT_CAPACITY;

public class CtIntersectionTypeReferenceImpl<T> extends CtTypeReferenceImpl<T> implements CtIntersectionTypeReference<T> {
	List<CtTypeReference<?>> bounds = CtElementImpl.emptyList();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtIntersectionTypeReference(this);
	}

	@Override
	public List<CtTypeReference<?>> getBounds() {
		return unmodifiableList(bounds);
	}

	@Override
	public <C extends CtIntersectionTypeReference> C setBounds(List<CtTypeReference<?>> bounds) {
		if (this.bounds == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			this.bounds = new ArrayList<CtTypeReference<?>>(TYPE_BOUNDS_CONTAINER_DEFAULT_CAPACITY);
		}
		this.bounds.clear();
		final List<CtTypeReference<?>> newBounds = new ArrayList<CtTypeReference<?>>();
		newBounds.addAll(bounds);
		for (CtTypeReference<?> bound : newBounds) {
			addBound(bound);
		}
		return (C) this;
	}

	@Override
	public <C extends CtIntersectionTypeReference> C addBound(CtTypeReference<?> bound) {
		if (bounds == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			bounds = new ArrayList<CtTypeReference<?>>(TYPE_BOUNDS_CONTAINER_DEFAULT_CAPACITY);
		}
		bound.setParent(this);
		bounds.add(bound);
		return (C) this;
	}

	@Override
	public boolean removeBound(CtTypeReference<?> bound) {
		return bounds != CtElementImpl.<CtTypeReference<?>>emptyList() && bounds.remove(bound);
	}
}
