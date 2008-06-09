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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.RtHelper;

public class CtExecutableReferenceImpl<T> extends CtReferenceImpl implements
		CtExecutableReference<T> {
	private static final long serialVersionUID = 1L;

	boolean stat = false;

	List<CtTypeReference<?>> actualTypeArguments = new ArrayList<CtTypeReference<?>>();

	CtTypeReference<?> declaringType;

	List<CtTypeReference<?>> parametersTypes = new ArrayList<CtTypeReference<?>>();

	CtTypeReference<T> type;

	public CtExecutableReferenceImpl() {
		super();
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtExecutableReference(this);
	}

	public List<CtTypeReference<?>> getActualTypeArguments() {
		return actualTypeArguments;
	}

	public boolean isConstructor() {		
		return getSimpleName().equals(CONSTRUCTOR_NAME);
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		A annotation = super.getAnnotation(annotationType);
		if (annotation != null) {
			return annotation;
		}
		// use reflection
		Class<?> c = getDeclaringType().getActualClass();
		for (Method m : RtHelper.getAllMethods(c)) {
			if (!getSimpleName().equals(m.getName())) {
				continue;
			}
			if (getParameterTypes().size() != m.getParameterTypes().length) {
				continue;
			}
			int i = 0;
			for (Class<?> t : m.getParameterTypes()) {
				if (t != getParameterTypes().get(i).getActualClass()) {
					break;
				}
				i++;
			}
			if (i == getParameterTypes().size()) {
				m.setAccessible(true);
				return m.getAnnotation(annotationType);
			}
		}
		return null;
	}

	@Override
	public Annotation[] getAnnotations() {
		Annotation[] annotations = super.getAnnotations();
		if (annotations != null) {
			return annotations;
		}
		// use reflection
		Class<?> c = getDeclaringType().getActualClass();
		for (Method m : RtHelper.getAllMethods(c)) {
			if (!getSimpleName().equals(m.getName())) {
				continue;
			}
			if (getParameterTypes().size() != m.getParameterTypes().length) {
				continue;
			}
			int i = 0;
			for (Class<?> t : m.getParameterTypes()) {
				if (t != getParameterTypes().get(i).getActualClass()) {
					break;
				}
				i++;
			}
			if (i == getParameterTypes().size()) {
				m.setAccessible(true);
				return m.getAnnotations();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public CtExecutable<T> getDeclaration() {
		CtType<?> typeDecl = (CtType<?>) getDeclaringType().getDeclaration();
		if (typeDecl == null) {
			return null;
		}

		CtExecutable<?> ret = typeDecl.getMethod(getSimpleName(),
				parametersTypes.toArray(new CtTypeReference<?>[0]));
		if ((ret == null) && (typeDecl instanceof CtClass)
				&& (getSimpleName().equals("<init>"))) {
			try {
				return (CtExecutable<T>) ((CtClass<?>) typeDecl)
						.getConstructor(parametersTypes
								.toArray(new CtTypeReference<?>[0]));
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
		return (CtExecutable<T>) ret;
	}

	public CtTypeReference<?> getDeclaringType() {
		return declaringType;
	}

	public List<CtTypeReference<?>> getParameterTypes() {
		return parametersTypes;
	}

	public CtTypeReference<T> getType() {
		return type;
	}

	@SuppressWarnings("unchecked")
	public <S extends T> CtExecutableReference<S> getOverridingExecutable(
			CtTypeReference<?> subType) {
		if ((subType == null) || subType.equals(getDeclaringType())) {
			return null;
		}
		CtSimpleType<?> t = subType.getDeclaration();
		if (t == null) {
			return null;
		}
		if (!(t instanceof CtClass)) {
			return null;
		}
		CtClass<?> c = (CtClass<?>) t;
		for (CtMethod<?> m : c.getMethods()) {
			if (m.getReference().isOverriding(this)) {
				return (CtExecutableReference<S>) m.getReference();
			}
		}
		return getOverridingExecutable(c.getSuperclass());
	}

	public boolean isOverriding(CtExecutableReference<?> executable) {
		if (!this.getDeclaringType().isSubtypeOf(executable.getDeclaringType())) {
			return false;
		}
		if (!getSimpleName().equals(executable.getSimpleName())) {
			return false;
		}
		List<CtTypeReference<?>> l1 = this.getParameterTypes();
		List<CtTypeReference<?>> l2 = executable.getParameterTypes();
		if (l1.size() != l2.size()) {
			return false;
		}
		for (int i = 0; i < l1.size(); i++) {
			if (!l1.get(i).isAssignableFrom(l2.get(i))) {
				return false;
			}
		}
		return true;
	}

	public void setActualTypeArguments(
			List<CtTypeReference<?>> actualTypeArguments) {
		this.actualTypeArguments = actualTypeArguments;
	}

	public void setDeclaringType(CtTypeReference<?> declaringType) {
		this.declaringType = declaringType;
	}

	public void setParameterTypes(List<CtTypeReference<?>> parameterTypes) {
		this.parametersTypes = parameterTypes;
	}

	public void setType(CtTypeReference<T> type) {
		this.type = type;
	}

	public Method getActualMethod() {
		for (Method m : getDeclaringType().getActualClass()
				.getDeclaredMethods()) {
			if (!m.getName().equals(getSimpleName())) {
				continue;
			}
			if (m.getParameterTypes().length != getParameterTypes().size()) {
				continue;
			}
			boolean matches = true;
			for (int i = 0; i < m.getParameterTypes().length; i++) {
				if (m.getParameterTypes()[i] != getParameterTypes().get(i)
						.getActualClass()) {
					matches = false;
					break;
				}
			}
			if (matches) {
				return m;
			}
		}
		return null;
	}

	public Constructor<?> getActualConstructor() {
		for (Constructor<?> c : getDeclaringType().getActualClass()
				.getDeclaredConstructors()) {
			if (c.getParameterTypes().length != getParameterTypes().size()) {
				continue;
			}
			boolean matches = true;
			for (int i = 0; i < c.getParameterTypes().length; i++) {
				if (c.getParameterTypes()[i] != getParameterTypes().get(i)
						.getActualClass()) {
					matches = false;
					break;
				}
			}
			if (matches) {
				return c;
			}
		}
		return null;
	}

	public boolean isStatic() {
		return stat;
		// CtExecutable<?> e = getDeclaration();
		// if (e != null) {
		// return e.getModifiers().contains(ModifierKind.STATIC);
		// }
		// try {
		// Class declaringClass = Class.forName(getDeclaringType()
		// .getQualifiedName());
		//
		// List<CtTypeReference<?>> paramsRef = getParameterTypes();
		//
		// for (Method m : declaringClass.getMethods()) {
		// if (m.getName().equals(getSimpleName())) {
		// int count = 0;
		// int i = 0;
		// Class[] params = m.getParameterTypes();
		// for (; i < params.length && i < paramsRef.size()
		// && i == count; i++) {
		// if (params[i] == paramsRef.get((i)).getActualClass()) {
		// count++;
		// }
		// }
		//
		// if (count == i) {
		// return Modifier.isStatic(m.getModifiers());
		// } else {
		// if (count == params.length - 1) {
		// for (; i < paramsRef.size() && i == count + 1; i++) {
		// if (paramsRef.get(i).getActualClass() == params[params.length - 1]
		// .getComponentType())
		// count++;
		// }
		// if (i == count + 1)
		// return Modifier.isStatic(m.getModifiers());
		// }
		// }
		// }
		// }
		// } catch (Exception e1) {
		// e1.printStackTrace();
		// }
		//
		// return false;
	}

	public void setStatic(boolean b) {
		this.stat = b;
	}

	public boolean isFinal() {
		CtExecutable<T> e = getDeclaration();
		if (e != null) {
			return e.hasModifier(ModifierKind.FINAL);
		}
		Method m = getActualMethod();
		if (m != null) {
			return Modifier.isFinal(m.getModifiers());
		}
		return false;
	}

	public Set<ModifierKind> getModifiers() {
		CtExecutable<T> e = getDeclaration();
		if (e != null) {
			return e.getModifiers();
		}
		Method m = getActualMethod();
		if (m != null) {
			return RtHelper.getModifiers(m.getModifiers());
		}
		Constructor<?> c = getActualConstructor();
		if (c != null) {
			return RtHelper.getModifiers(c.getModifiers());
		}
		return new TreeSet<ModifierKind>();
	}

	public CtExecutableReference<?> getOverridingExecutable() {
		CtTypeReference<?> st = getDeclaringType().getSuperclass();
		CtTypeReference<Object> objectType = getFactory().Type()
				.createReference(Object.class);
		if (st == null) {
			return getOverloadedExecutable(objectType, objectType);
		}
		return getOverloadedExecutable(st, objectType);
	}

	private CtExecutableReference<?> getOverloadedExecutable(
			CtTypeReference<?> t, CtTypeReference<Object> objectType) {
		if (t == null) {
			return null;
		}
		for (CtExecutableReference<?> e : t.getDeclaredExecutables()) {
			if (this.isOverriding(e)) {
				return e;
			}
		}
		if (t.equals(objectType)) {
			return null;
		}
		CtTypeReference<?> st = t.getSuperclass();
		if (st == null) {
			return getOverloadedExecutable(objectType, objectType);
		}
		return getOverloadedExecutable(t.getSuperclass(), objectType);
	}
}
