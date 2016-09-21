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

import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtVisitor;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.Set;

public abstract class CtVariableReferenceImpl<T> extends CtReferenceImpl implements CtVariableReference<T> {
	private static final long serialVersionUID = 1L;

	CtTypeReference<T> type;

	public CtVariableReferenceImpl() {
		super();
	}

	@Override
	public void accept(CtVisitor visitor) {
		// nothing
	}

	@Override
	public CtTypeReference<T> getType() {
		return type;
	}

	@Override
	public <C extends CtVariableReference<T>> C setType(CtTypeReference<T> type) {
		if (type != null) {
			type.setParent(this);
		}
		this.type = type;
		return (C) this;
	}

	@Override
	protected AnnotatedElement getActualAnnotatedElement() {
		// this is never available through reflection
		return null;
	}

	@Override
	public CtVariable<T> getDeclaration() {
		return null;
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		CtVariable<T> v = getDeclaration();
		if (v != null) {
			return v.getModifiers();
		}
		return Collections.emptySet();
	}

	@Override
	public void replace(CtVariableReference<?> reference) {
		super.replace(reference);
	}

	@Override
	public CtVariableReference<T> clone() {
		return (CtVariableReference<T>) super.clone();
	}
}
