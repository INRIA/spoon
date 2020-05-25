/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.reference;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static spoon.reflect.path.CtRole.BOUND;


public class CtIntersectionTypeReferenceImpl<T> extends CtTypeReferenceImpl<T> implements CtIntersectionTypeReference<T> {
	@MetamodelPropertyField(role = BOUND)
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

	@Override
	public boolean isSimplyQualified() {
		if (bounds != null && bounds.size() > 0) {
			return bounds.get(0).isSimplyQualified();
		}
		return false;
	}

	@Override
	public CtIntersectionTypeReferenceImpl<T> setSimplyQualified(boolean isSimplyQualified) {
		if (bounds != null && bounds.size() > 0) {
			bounds.get(0).setSimplyQualified(isSimplyQualified);
		}
		return this;
	}
}
