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

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class CtIntersectionTypeReferenceImpl<T> extends CtTypeReferenceImpl<T> implements CtIntersectionTypeReference<T> {
	List<CtTypeReference<?>> bounds = CtElementImpl.emptyList();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtIntersectionTypeReference(this);
	}

	@Override
	public List<CtTypeReference<?>> getBounds() {
		return bounds;
	}

	@Override
	public <C extends CtIntersectionTypeReference> C setBounds(List<CtTypeReference<?>> bounds) {
		if (bounds == null || bounds.isEmpty()) {
			this.bounds = CtElementImpl.emptyList();
			return (C) this;
		}
		if (this.bounds == CtElementImpl.<CtTypeReference<?>>emptySet()) {
			this.bounds = new ArrayList<>();
		}
		this.bounds.clear();
		for (CtTypeReference<?> bound : bounds) {
			addBound(bound);
		}
		return (C) this;
	}

	@Override
	public <C extends CtIntersectionTypeReference> C addBound(CtTypeReference<?> bound) {
		if (bound == null) {
			return (C) this;
		}
		if (bounds == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			bounds = new ArrayList<>();
		}
		if (!bounds.contains(bound)) {
			bound.setParent(this);
			bounds.add(bound);
		}
		return (C) this;
	}

	@Override
	public boolean removeBound(CtTypeReference<?> bound) {
		return bounds != CtElementImpl.<CtTypeReference<?>>emptyList() && bounds.remove(bound);
	}

	@Override
	public CtIntersectionTypeReference<T> clone() {
		return (CtIntersectionTypeReference<T>) super.clone();
	}
}
