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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

/**
 * The implementation for {@link spoon.reflect.declaration.CtType}.
 */
public abstract class CtTypeImpl<T> extends CtSimpleTypeImpl<T> implements
		CtType<T> {

	private static final long serialVersionUID = 1L;

	List<CtTypeReference<?>> formalTypeParameters = EMPTY_LIST();

	Set<CtTypeReference<?>> interfaces = EMPTY_SET();

	Set<CtMethod<?>> methods = EMPTY_SET();

	public CtTypeImpl() {
		super();
	}

	public <M> boolean addMethod(CtMethod<M> method) {
		if (methods == CtElementImpl.<CtMethod<?>> EMPTY_SET()) {
			methods = new TreeSet<CtMethod<?>>();
		}
		return methods.add(method);
	}

	public <S> boolean addSuperInterface(CtTypeReference<S> interfac) {
		if (interfaces == CtElementImpl.<CtTypeReference<?>> EMPTY_SET()) {
			interfaces = new TreeSet<CtTypeReference<?>>();
		}
		return interfaces.add(interfac);
	}

	public <M> boolean removeMethod(CtMethod<M> method) {
		if (methods == CtElementImpl.<CtMethod<?>> EMPTY_SET()) {
			methods = new TreeSet<CtMethod<?>>();
		}
		return methods.remove(method);
	}

	public <S> boolean removeSuperInterface(CtTypeReference<S> interfac) {
		if (methods == CtElementImpl.<CtMethod<?>> EMPTY_SET()) {
			methods = new TreeSet<CtMethod<?>>();
		}
		return interfaces.remove(interfac);
	}

	public boolean addFormalTypeParameter(CtTypeReference<?> formalTypeParameter) {
		if (formalTypeParameters == CtElementImpl
				.<CtTypeReference<?>> EMPTY_LIST()) {
			formalTypeParameters = new ArrayList<CtTypeReference<?>>();
		}
		return formalTypeParameters.add(formalTypeParameter);
	}

	public boolean removeFormalTypeParameter(
			CtTypeReference<?> formalTypeParameter) {
		if (formalTypeParameters == CtElementImpl
				.<CtTypeReference<?>> EMPTY_LIST()) {
			formalTypeParameters = new ArrayList<CtTypeReference<?>>();
		}
		return formalTypeParameters.remove(formalTypeParameter);
	}

	public List<CtTypeReference<?>> getFormalTypeParameters() {
		return formalTypeParameters;
	}

	@SuppressWarnings("unchecked")
	public <R> CtMethod<R> getMethod(CtTypeReference<R> returnType,
			String name, CtTypeReference<?>... parameterTypes) {
		for (CtMethod<?> mm : methods) {
			CtMethod<R> m = (CtMethod<R>) mm;
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

	@SuppressWarnings("unchecked")
	public <R> CtMethod<R> getMethod(String name,
			CtTypeReference<?>... parameterTypes) {
		for (CtMethod<?> m : methods) {
			if (m.getSimpleName().equals(name)) {
				boolean cont = m.getParameters().size() == parameterTypes.length;
				for (int i = 0; cont && (i < m.getParameters().size())
						&& (i < parameterTypes.length); i++) {
					// String
					// s1=m.getParameters().get(i).getType().getQualifiedName();
					// String s2=parameterTypes[i].getQualifiedName();
					if (!m.getParameters().get(i).getType()
							.equals(parameterTypes[i])) {
						cont = false;
					}
				}
				if (cont) {
					return (CtMethod<R>) m;
				}
			}
		}
		return null;
	}

	public Set<CtMethod<?>> getMethods() {
		return methods;
	}

	@Override
	public Set<CtMethod<?>> getMethodsAnnotatedWith(
			CtTypeReference<?>... annotationTypes) {
		Set<CtMethod<?>> result = new HashSet<>();
		for (CtMethod<?> m : methods) {
			for (CtAnnotation<?> a : m.getAnnotations()) {
				if (Arrays.asList(annotationTypes).contains(
						a.getAnnotationType())) {
					result.add(m);
				}
			}
		}
		return result;
	}

	@Override
	public List<CtMethod<?>> getMethodsByName(String name) {
		List<CtMethod<?>> result = new ArrayList<>();
		for (CtMethod<?> m : methods) {
			if (name.equals(m.getSimpleName())) {
				result.add(m);
			}
		}
		return result;
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
