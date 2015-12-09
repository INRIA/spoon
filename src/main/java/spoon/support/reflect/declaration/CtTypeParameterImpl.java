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
package spoon.support.reflect.declaration;

import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

import java.util.ArrayList;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.TYPE_BOUNDS_CONTAINER_DEFAULT_CAPACITY;

/**
 * The implementation for {@link spoon.reflect.declaration.CtTypeParameter}.
 *
 * @author Renaud Pawlak
 */
public class CtTypeParameterImpl extends CtNamedElementImpl implements CtTypeParameter {
	private static final long serialVersionUID = 1L;

	List<CtTypeReference<?>> bounds = emptyList();

	public CtTypeParameterImpl() {
		super();
	}

	@Override
	public void accept(CtVisitor v) {
		v.visitCtTypeParameter(this);
	}

	@Override
	public <T extends CtTypeParameter> T addBound(CtTypeReference<?> bound) {
		if (bounds == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			bounds = new ArrayList<CtTypeReference<?>>(TYPE_BOUNDS_CONTAINER_DEFAULT_CAPACITY);
		}
		bound.setParent(this);
		this.bounds.add(bound);
		return (T) this;
	}

	@Override
	public boolean removeBound(CtTypeReference<?> bound) {
		return bounds != CtElementImpl.<CtTypeReference<?>>emptyList() && this.bounds.remove(bound);
	}

	@Override
	public List<CtTypeReference<?>> getBounds() {
		return bounds;
	}

	@Override
	public <T extends CtTypeParameter> T setBounds(List<CtTypeReference<?>> bounds) {
		if (this.bounds == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			this.bounds = new ArrayList<CtTypeReference<?>>(TYPE_BOUNDS_CONTAINER_DEFAULT_CAPACITY);
		}
		this.bounds.clear();
		for (CtTypeReference<?> bound : bounds) {
			addBound(bound);
		}
		return (T) this;
	}
}
