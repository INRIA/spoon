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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtGenericElementReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.util.RtHelper;

import static spoon.reflect.ModelElementContainerDefaultCapacities.METHOD_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY;

public class CtExecutableReferenceImpl<T> extends CtReferenceImpl implements
		CtExecutableReference<T> {
	private static final long serialVersionUID = 1L;

	boolean stat = false;

	List<CtTypeReference<?>> actualTypeArguments = CtElementImpl.EMPTY_LIST();

	CtTypeReference<?> declaringType;

	CtTypeReference<T> type;

	List<CtTypeReference<?>> parameters = CtElementImpl.EMPTY_LIST();

	public CtExecutableReferenceImpl() {
		super();
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtExecutableReference(this);
	}

	@Override
	public List<CtTypeReference<?>> getActualTypeArguments() {
		return actualTypeArguments;
	}

	@Override
	public boolean isConstructor() {
		return getSimpleName().equals(CONSTRUCTOR_NAME);
	}

//	@Override
//	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
//		A annotation = super.getAnnotation(annotationType);
//		if (annotation != null) {
//			return annotation;
//		}
//		// use reflection
//		Class<?> c = getDeclaringType().getActualClass();
//		for (Method m : RtHelper.getAllMethods(c)) {
//			if (!getSimpleName().equals(m.getName())) {
//				continue;
//			}
//			if (getParameterTypes().size() != m.getParameterTypes().length) {
//				continue;
//			}
//			int i = 0;
//			for (Class<?> t : m.getParameterTypes()) {
//				if (t != getParameterTypes().get(i).getActualClass()) {
//					break;
//				}
//				i++;
//			}
//			if (i == getParameterTypes().size()) {
//				m.setAccessible(true);
//				return m.getAnnotation(annotationType);
//			}
//		}
//		return null;
//	}

//	@Override
//	public Annotation[] getAnnotations() {
//		Annotation[] annotations = super.getAnnotations();
//		if (annotations != null) {
//			return annotations;
//		}
//		// use reflection
//		Class<?> c = getDeclaringType().getActualClass();
//		for (Method m : RtHelper.getAllMethods(c)) {
//			if (!getSimpleName().equals(m.getName())) {
//				continue;
//			}
//			if (getParameterTypes().size() != m.getParameterTypes().length) {
//				continue;
//			}
//			int i = 0;
//			for (Class<?> t : m.getParameterTypes()) {
//				if (t != getParameterTypes().get(i).getActualClass()) {
//					break;
//				}
//				i++;
//			}
//			if (i == getParameterTypes().size()) {
//				m.setAccessible(true);
//				return m.getAnnotations();
//			}
//		}
//		return null;
//	}

	@Override
	@SuppressWarnings("unchecked")
	public CtExecutable<T> getDeclaration() {
		CtType<?> typeDecl = getDeclaringType().getDeclaration();
		if (typeDecl == null) {
			return null;
		}

		CtExecutable<T> method = typeDecl
				.getMethod(getSimpleName(), parameters.toArray(
						new CtTypeReferenceImpl<?>[parameters.size()]));
		if ((method == null) && (typeDecl instanceof CtClass)
				&& (getSimpleName().equals("<init>"))) {
			try {
				return (CtExecutable<T>) ((CtClass<?>) typeDecl)
						.getConstructor(parameters.toArray(
								new CtTypeReferenceImpl<?>[parameters.size()]));
			} catch (ClassCastException e) {
				Launcher.logger.error(e.getMessage(), e);
			}
		}
		return method;
	}

	@Override
	public CtTypeReference<?> getDeclaringType() {
		return declaringType;
	}

	@Override
	public CtTypeReference<T> getType() {
		return type;
	}

	@Override
	public List<CtTypeReference<?>> getParameters() {
		return Collections.unmodifiableList(parameters);
	}

	@Override
	public <C extends CtExecutableReference<T>> C setParameters(List<CtTypeReference<?>> parameters) {
		if (this.parameters == CtElementImpl.<CtTypeReference<?>> EMPTY_LIST()) {
			this.parameters = new ArrayList<CtTypeReference<?>>();
			this.parameters.addAll(parameters);
		}
		return (C) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <S extends T> CtExecutableReference<S> getOverridingExecutable(
			CtTypeReference<?> subType) {
		if ((subType == null) || subType.equals(getDeclaringType())) {
			return null;
		}
		CtType<?> t = subType.getDeclaration();
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

	@Override
	public boolean isOverriding(CtExecutableReference<?> executable) {
		if (!this.getDeclaringType().isSubtypeOf(executable.getDeclaringType())) {
			return false;
		}
		return getSimpleName().equals(executable.getSimpleName());
	}

	@Override
	public <C extends CtGenericElementReference> C setActualTypeArguments(List<CtTypeReference<?>> actualTypeArguments) {
		this.actualTypeArguments = actualTypeArguments;
		return (C) this;
	}

	@Override
	public <C extends CtExecutableReference<T>> C setDeclaringType(CtTypeReference<?> declaringType) {
		this.declaringType = declaringType;
		return (C) this;
	}

	@Override
	public <C extends CtExecutableReference<T>> C setType(CtTypeReference<T> type) {
		this.type = type;
		return (C) this;
	}

	@Override
	protected AnnotatedElement getActualAnnotatedElement() {
		if(isConstructor()) {
			return getActualConstructor();
		} else {
			return getActualMethod();
		}
	}

	@Override
	public Method getActualMethod() {
		List<CtTypeReference<?>> parameters = this.getParameters();
		
		method_loop:
		for (Method m : getDeclaringType().getActualClass().getDeclaredMethods()) {
			if (!m.getDeclaringClass().isSynthetic() &&
					m.isSynthetic()) {
				continue;
			}
			if (!m.getName().equals(getSimpleName())) {
				continue;
			}
			if (m.getParameterTypes().length != parameters.size()) {
				continue;
			}
			for (int i = 0; i < parameters.size(); i++) {
				if (m.getParameterTypes()[i] != parameters.get(i).getActualClass()) {
					continue method_loop;
				}
			}
			
			return m;
		}
		return null;
	}

	public Constructor<?> getActualConstructor() {
		List<CtTypeReference<?>> parameters = this.getParameters();
		
		constructor_loop:
		for (Constructor<?> c : getDeclaringType().getActualClass().getDeclaredConstructors()) {
			if (c.getParameterTypes().length != parameters.size()) {
				continue;
			}
			for (int i = 0; i < parameters.size(); i++) {
				if (c.getParameterTypes()[i] != parameters.get(i).getActualClass()) {
					continue constructor_loop;
				}
			}
			return c;
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
		// Launcher.logger.error(e1.getMessage(), e1);
		// }
		//
		// return false;
	}

	@Override
	public <C extends CtExecutableReference<T>> C setStatic(boolean b) {
		this.stat = b;
		return (C) this;
	}

	@Override
	public boolean isFinal() {
		CtExecutable<T> e = getDeclaration();
		if (e != null) {
			if (e instanceof CtMethod) {
				return ((CtMethod<T>) e).hasModifier(ModifierKind.FINAL);
			} else if (e instanceof CtConstructor) {
				return ((CtConstructor<T>) e).hasModifier(ModifierKind.FINAL);
			}
			return false;
		}
		Method m = getActualMethod();
		return m != null && Modifier.isFinal(m.getModifiers());
	}

	public Set<ModifierKind> getModifiers() {
		CtExecutable<T> e = getDeclaration();
		if (e != null) {
			if (e instanceof CtMethod) {
				return ((CtMethod<T>) e).getModifiers();
			} else if (e instanceof CtConstructor) {
				return ((CtConstructor<T>) e).getModifiers();
			}
			return CtElementImpl.EMPTY_SET();
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

	@Override
	public CtExecutableReference<?> getOverridingExecutable() {
		CtTypeReference<?> st = getDeclaringType().getSuperclass();
		CtTypeReference<Object> objectType = getFactory().Type().createReference(Object.class);
		if (st == null) {
			return getOverloadedExecutable(objectType, objectType);
		}
		return getOverloadedExecutable(st, objectType);
	}

	private CtExecutableReference<?> getOverloadedExecutable(CtTypeReference<?> t,
															 CtTypeReference<Object> objectType) {
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

	@Override
	public <C extends CtGenericElementReference> C addActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		if (actualTypeArguments == CtElementImpl.<CtTypeReference<?>> EMPTY_LIST()) {
			actualTypeArguments = new ArrayList<CtTypeReference<?>>(
					METHOD_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		actualTypeArguments.add(actualTypeArgument);
		return (C) this;
	}

	@Override
	public boolean removeActualTypeArgument(
			CtTypeReference<?> actualTypeArgument) {
		return actualTypeArguments !=CtElementImpl.<CtTypeReference<?>>EMPTY_LIST() &&
				actualTypeArguments.remove(actualTypeArgument);
	}
}
