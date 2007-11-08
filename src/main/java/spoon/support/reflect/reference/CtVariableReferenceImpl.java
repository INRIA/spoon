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

import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

public abstract class CtVariableReferenceImpl<T> extends CtReferenceImpl
		implements CtVariableReference<T> {
	private static final long serialVersionUID = 1L;

	CtTypeReference<T> type;

	public CtVariableReferenceImpl() {
		super();
	}

	public CtTypeReference<T> getType() {
		return type;
	}

	public void setType(CtTypeReference<T> type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof CtVariableReference)) {
			return false;
		}
		CtVariableReference<?> ref = (CtVariableReference<?>) object;
		return this.type.equals(ref.getType())
				&& simplename.equals(ref.getSimpleName());
	}

	public Set<ModifierKind> getModifiers() {
		CtVariable<T> v = getDeclaration();
		if (v != null) {
			return v.getModifiers();
		}
		return new TreeSet<ModifierKind>();
	}

}
