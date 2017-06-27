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

import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static spoon.reflect.path.CtRole.BOUND;


public class CtIntersectionTypeReferenceImpl<T> extends CtTypeReferenceImpl<T> implements CtIntersectionTypeReference<T> {
	List<CtTypeReference<?>> bounds = CtElementImpl.emptyList();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtIntersectionTypeReference(this);
	}

	@Override
	public List<CtTypeReference<?>> getBounds() {
		return Collections.unmodifiableList(bounds);
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
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, BOUND, this.bounds, new ArrayList<>(this.bounds));
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
			getFactory().getEnvironment().getModelChangeListener().onListAdd(this, BOUND, this.bounds, bound);
			bounds.add(bound);
		}
		return (C) this;
	}

	@Override
	public boolean removeBound(CtTypeReference<?> bound) {
		if (bounds == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, BOUND, bounds, bounds.indexOf(bound), bound);
		return bounds.remove(bound);
	}

	@Override
	public CtTypeReference<?> getTypeErasure() {
		if (bounds == null || bounds.isEmpty()) {
			return getFactory().Type().OBJECT;
		}
		return bounds.get(0).getTypeErasure();
	}

	@Override
	public CtIntersectionTypeReference<T> clone() {
		return (CtIntersectionTypeReference<T>) super.clone();
	}
}
