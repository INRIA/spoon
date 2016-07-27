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

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.visitor.CtVisitor;

import java.util.List;

public class CtParameterReferenceImpl<T> extends CtVariableReferenceImpl<T> implements CtParameterReference<T> {
	private static final long serialVersionUID = 1L;

	CtExecutableReference<?> executable;

	public CtParameterReferenceImpl() {
		super();
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtParameterReference(this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public CtParameter<T> getDeclaration() {
		final CtParameter<T> ctParameter = lookupDynamically();
		if (ctParameter != null) {
			return ctParameter;
		}
		return fromDeclaringExecutable();
	}

	private CtParameter<T> lookupDynamically() {
		CtElement element = this;
		CtParameter optional = null;
		String name = getSimpleName();
		try {
			do {
				CtExecutable executable = element.getParent(CtExecutable.class);
				if (executable == null) {
					return null;
				}
				for (CtParameter parameter : (List<CtParameter>) executable.getParameters()) {
					if (name.equals(parameter.getSimpleName())) {
						optional = parameter;
					}
				}
				element = executable;
			} while (optional == null);
		} catch (ParentNotInitializedException e) {
			return null;
		}
		return optional;
	}

	private CtParameter<T> fromDeclaringExecutable() {
		CtExecutable<?> exec = executable.getDeclaration();
		if (exec == null) {
			return null;
		}
		List<CtParameter<?>> params = exec.getParameters();
		for (CtParameter<?> p : params) {
			if (this.getSimpleName().equals(p.getSimpleName())) {
				return (CtParameter<T>) p;
			}
		}
		return null;
	}

	@Override
	public CtExecutableReference<?> getDeclaringExecutable() {
		return executable;
	}

	@Override
	public <C extends CtParameterReference<T>> C setDeclaringExecutable(CtExecutableReference<?> executable) {
		if (executable != null) {
			executable.setParent(this);
		}
		this.executable = executable;
		return (C) this;
	}

	@Override
	public CtParameterReference<T> clone() {
		return (CtParameterReference<T>) super.clone();
	}
}
