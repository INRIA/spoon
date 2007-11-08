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

package spoon.support.reflect.declaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

/**
 * The implementation for {@link spoon.reflect.declaration.CtType}.
 */
public abstract class CtTypeImpl<T> extends CtSimpleTypeImpl<T> implements
		CtType<T> {

	List<CtTypeReference<?>> formalTypeParameters = new ArrayList<CtTypeReference<?>>();

	Set<CtTypeReference<?>> interfaces = new TreeSet<CtTypeReference<?>>();

	Set<CtMethod<?>> methods = new TreeSet<CtMethod<?>>();

	public CtTypeImpl() {
		super();
	}

	public List<CtTypeReference<?>> getFormalTypeParameters() {
		return formalTypeParameters;
	}

	@SuppressWarnings("unchecked")
	public <R> CtMethod<R> getMethod(CtTypeReference<R> returnType,
			String name, CtTypeReference<?>... parameterTypes) {
		for (CtMethod mm : methods) {
			CtMethod<R> m = mm;
			if (m.getSimpleName().equals(name)) {
				if (!m.getType().equals(returnType)) {
					continue;
				}
				boolean cont = m.getParameters().size() == parameterTypes.length;
				for (int i = 0; cont && (i < m.getParameters().size())
						&& (i < parameterTypes.length); i++) {
					if (!m.getParameters().get(i).getType().getQualifiedName()
							.equals(parameterTypes[i].getQualifiedName())) {
						cont = false;
					}
				}
				if (cont) {
					return m;
				}
			}
		}
		return null;
	}

	public CtMethod<?> getMethod(String name,
			CtTypeReference<?>... parameterTypes) {
		for (CtMethod<?> m : methods) {
			if (m.getSimpleName().equals(name)) {
				boolean cont = m.getParameters().size() == parameterTypes.length;
				for (int i = 0; cont && (i < m.getParameters().size())
						&& (i < parameterTypes.length); i++) {
					// String
					// s1=m.getParameters().get(i).getType().getQualifiedName();
					// String s2=parameterTypes[i].getQualifiedName();
					if (!m.getParameters().get(i).getType().equals(
							parameterTypes[i])) {
						cont = false;
					}
				}
				if (cont) {
					return m;
				}
			}
		}
		return null;
	}

	public Set<CtMethod<?>> getMethods() {
		return methods;
	}

	@Override
	public String getQualifiedName() {
		if (isTopLevel()) {
			return super.getQualifiedName();
		}
		if (getDeclaringType() != null) {
			return getDeclaringType().getQualifiedName() + INNERTTYPE_SEPARATOR
					+ getSimpleName();
		}
		return getSimpleName();
	}

	public Set<CtTypeReference<?>> getSuperInterfaces() {
		return interfaces;
	}

	public void setFormalTypeParameters(
			List<CtTypeReference<?>> formalTypeParameters) {
		this.formalTypeParameters = formalTypeParameters;
	}

	public void setMethods(Set<CtMethod<?>> methods) {
		this.methods = methods;
	}

	public void setSuperInterfaces(Set<CtTypeReference<?>> interfaces) {
		this.interfaces = interfaces;
	}

}
