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

import java.lang.reflect.Array;

import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

public class CtArrayTypeReferenceImpl<T> extends CtTypeReferenceImpl<T>
		implements CtArrayTypeReference<T> {
	private static final long serialVersionUID = 1L;

	CtTypeReference<?> componentType;

	public CtArrayTypeReferenceImpl() {
		super();
	}

	public CtTypeReference<?> getComponentType() {
		return componentType;
	}

	public void setComponentType(CtTypeReference<?> componentType) {
		this.componentType = componentType;
	}

	@Override
	public String getSimpleName() {
		return componentType.getSimpleName();
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtArrayTypeReference(this);
	}

	@Override
	public String getQualifiedName() {
		if (getComponentType().getDeclaringType() != null) {
			return getComponentType().getDeclaringType().getQualifiedName()
					+ CtSimpleType.INNERTTYPE_SEPARATOR + getSimpleName();
		} else if (getComponentType().getPackage() != null) {
			return getComponentType().getPackage().getSimpleName()
					+ CtPackage.PACKAGE_SEPARATOR + getSimpleName();
		} else {
			return getSimpleName();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getActualClass() {
		Class c = getComponentType().getActualClass();
		if (c == null) {
			return null;
		}
		return (Class) Array.newInstance(c, 0).getClass();
	}

	public int getDimensionCount() {
		if (getComponentType() instanceof CtArrayTypeReference) {
			return ((CtArrayTypeReference<?>) getComponentType())
					.getDimensionCount() + 1;
		}
		return 1;
	}

}
