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
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The implementation for {@link spoon.reflect.declaration.CtTypeParameter}.
 *
 * @author Renaud Pawlak
 */
public class CtTypeParameterImpl extends CtNamedElementImpl implements CtTypeParameter {
	private static final long serialVersionUID = 1L;

	CtTypeReference<?> superType;

	public CtTypeParameterImpl() {
		super();
	}

	@Override
	public void accept(CtVisitor v) {
		v.visitCtTypeParameter(this);
	}

	@Override
	public <T extends CtTypeParameter> T addBound(CtTypeReference<?> bound) {
		if (bound == null) {
			return (T) this;
		}
		if (getSuperType() == null) {
			setSuperType(bound);
		} else if (getSuperType() instanceof CtIntersectionTypeReference<?>) {
			getSuperType().asCtIntersectionTypeReference().addBound(bound);
		} else {
			setSuperType(getFactory().Type().createIntersectionTypeReference(Arrays.asList(getSuperType(), bound)));
		}
		return (T) this;
	}

	@Override
	public boolean removeBound(CtTypeReference<?> bound) {
		if (bound == null || getSuperType() == null) {
			return false;
		}
		if (getSuperType() instanceof CtIntersectionTypeReference<?>) {
			return getSuperType().asCtIntersectionTypeReference().removeBound(bound);
		} else {
			setSuperType(null);
			return true;
		}
	}

	@Override
	public List<CtTypeReference<?>> getBounds() {
		if (getSuperType() instanceof CtIntersectionTypeReference<?>) {
			return getSuperType().asCtIntersectionTypeReference().getBounds();
		} else if (getSuperType() != null) {
			return Collections.<CtTypeReference<?>>singletonList(getSuperType());
		}
		return emptyList();
	}

	@Override
	public <T extends CtTypeParameter> T setBounds(List<CtTypeReference<?>> bounds) {
		if (bounds == null) {
			return (T) this;
		}
		if (getSuperType() instanceof CtIntersectionTypeReference<?>) {
			getSuperType().asCtIntersectionTypeReference().setBounds(bounds);
		} else if (bounds.size() > 1) {
			setSuperType(getFactory().Type().createIntersectionTypeReference(bounds));
		} else {
			setSuperType(bounds.get(0));
		}
		return (T) this;
	}

	@Override
	public CtTypeReference<?> getSuperType() {
		return superType;
	}

	@Override
	public <T extends CtTypeParameter> T setSuperType(CtTypeReference<?> superType) {
		if (superType != null) {
			superType.setParent(this);
		}
		this.superType = superType;
		return (T) this;
	}
}
