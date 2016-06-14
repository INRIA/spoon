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

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Collections.unmodifiableSet;

public class CtIntersectionTypeReferenceImpl<T> extends CtTypeReferenceImpl<T> implements CtIntersectionTypeReference<T> {
	Set<CtTypeReference<?>> bounds = CtElementImpl.emptySet();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtIntersectionTypeReference(this);
	}

	@Override
	public Set<CtTypeReference<?>> getBounds() {
		return unmodifiableSet(bounds);
	}

	@Override
	public <C extends CtIntersectionTypeReference> C setBounds(Set<CtTypeReference<?>> bounds) {
		if (this.bounds == CtElementImpl.<CtTypeReference<?>>emptySet()) {
			this.bounds = new TreeSet<>(new SourcePositionComparator());
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
		if (bounds == CtElementImpl.<CtTypeReference<?>>emptySet()) {
			bounds = new TreeSet<>(new SourcePositionComparator());
		}
		bound.setParent(this);
		bounds.add(bound);
		return (C) this;
	}

	@Override
	public boolean removeBound(CtTypeReference<?> bound) {
		return bounds != CtElementImpl.<CtTypeReference<?>>emptyList() && bounds.remove(bound);
	}

	private class SourcePositionComparator implements Comparator<CtTypeReference<?>> {
		@Override
		public int compare(CtTypeReference<?> o1, CtTypeReference<?> o2) {
			if (o1.getPosition() == null || o2.getPosition() == null) {
				return -1;
			}
			int pos1 = o1.getPosition().getSourceStart();
			int pos2 = o2.getPosition().getSourceStart();
			return (pos1 < pos2) ? -1 : ((pos1 == pos2) ? 0 : 1);
		}
	}

	@Override
	public CtIntersectionTypeReference<T> clone() {
		return (CtIntersectionTypeReference<T>) super.clone();
	}
}
