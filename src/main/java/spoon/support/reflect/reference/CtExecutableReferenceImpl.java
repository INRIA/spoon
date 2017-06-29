/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

import spoon.Launcher;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.util.RtHelper;
import spoon.support.visitor.ClassTypingContext;
import spoon.support.visitor.SignaturePrinter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static spoon.reflect.ModelElementContainerDefaultCapacities.METHOD_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.IS_STATIC;
import static spoon.reflect.path.CtRole.PARAMETER;
import static spoon.reflect.path.CtRole.TYPE;
import static spoon.reflect.path.CtRole.TYPE_PARAMETER;

public class CtExecutableReferenceImpl<T> extends CtReferenceImpl implements CtExecutableReference<T> {
	private static final long serialVersionUID = 1L;

	boolean stat = false;

	List<CtTypeReference<?>> actualTypeArguments = CtElementImpl.emptyList();

	CtTypeReference<?> declaringType;

	/**
	 * For methods, stores the return type of the method. (not pretty-printed).
	 * For constructors, stores the type of the target constructor (pretty-printed).
	 */
	CtTypeReference<T> type;

	List<CtTypeReference<?>> parameters = CtElementImpl.emptyList();

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
		final CtTypeReference<?> typeRef = getDeclaringType();
		if (typeRef == null || typeRef.getDeclaration() == null) {
			return null;
		}
		return getCtExecutable(typeRef.getDeclaration());
	}

	@Override
	public CtExecutable<T> getExecutableDeclaration() {
		return getCtExecutable(getDeclaringType().getTypeDeclaration());
	}

	private CtExecutable<T> getCtExecutable(CtType<?> typeDecl) {
		if (typeDecl == null) {
			return null;
		}
		CtExecutable<T> method = typeDecl.getMethod(getSimpleName(), parameters.toArray(new CtTypeReferenceImpl<?>[parameters.size()]));
		if ((method == null) && (typeDecl instanceof CtClass) && (getSimpleName().equals(CtExecutableReference.CONSTRUCTOR_NAME))) {
			try {
				return (CtExecutable<T>) ((CtClass<?>) typeDecl).getConstructor(parameters.toArray(new CtTypeReferenceImpl<?>[parameters.size()]));
			} catch (ClassCastException e) {
				Launcher.LOGGER.error(e.getMessage(), e);
			}
		} else if (method == null && getSimpleName().startsWith(CtExecutableReference.LAMBDA_NAME_PREFIX)) {
			final List<CtLambda<T>> elements = (List<CtLambda<T>>) typeDecl.getElements(new NameFilter<CtLambda<T>>(getSimpleName()));
			if (elements.size() == 0) {
				return null;
			}
			return elements.get(0);
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
		return unmodifiableList(parameters);
	}

	@Override
	public <C extends CtExecutableReference<T>> C setParameters(List<CtTypeReference<?>> parameters) {
		if (parameters == null || parameters.isEmpty()) {
			this.parameters = CtElementImpl.emptyList();
			return (C) this;
		}
		if (this.parameters == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			this.parameters = new ArrayList<>();
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, PARAMETER, this.parameters, new ArrayList<>(this.parameters));
		this.parameters.clear();
		for (CtTypeReference<?> parameter : parameters) {
			addParameter(parameter);
		}
		return (C) this;
	}

	private boolean addParameter(CtTypeReference<?> parameter) {
		if (parameter == null) {
			return false;
		}
		parameter.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, PARAMETER, this.parameters, parameter);
		return this.parameters.add(parameter);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <S extends T> CtExecutableReference<S> getOverridingExecutable(CtTypeReference<?> subType) {
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
		CtExecutable<?> exec = executable.getExecutableDeclaration();
		CtExecutable<?> thisExec = getExecutableDeclaration();
		if (exec == null || thisExec == null) {
			//the declaration of this executable is not in spoon model
			//use light detection algorithm, which ignores generic types
			final boolean isSame = getSimpleName().equals(executable.getSimpleName()) && getParameters().equals(executable.getParameters()) && getActualTypeArguments().equals(executable.getActualTypeArguments());
			if (!isSame) {
				return false;
			}
			if (!getDeclaringType().isSubtypeOf(executable.getDeclaringType())) {
				return false;
			}
			return true;
		}
		if (exec instanceof CtMethod<?> && thisExec instanceof CtMethod<?>) {
			return new ClassTypingContext(((CtTypeMember) thisExec).getDeclaringType()).isOverriding((CtMethod<?>) thisExec, (CtMethod<?>) exec);
		}
		//it is not a method. So we can return true only if it is reference to the this executable
		return exec == getDeclaration();
	}

	@Override
	public <C extends CtActualTypeContainer> C setActualTypeArguments(List<? extends CtTypeReference<?>> actualTypeArguments) {
		if (actualTypeArguments == null || actualTypeArguments.isEmpty()) {
			this.actualTypeArguments = CtElementImpl.emptyList();
			return (C) this;
		}
		if (this.actualTypeArguments == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			this.actualTypeArguments = new ArrayList<>();
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, TYPE_PARAMETER, this.actualTypeArguments, new ArrayList<>(this.actualTypeArguments));
		this.actualTypeArguments.clear();
		for (CtTypeReference<?> actualTypeArgument : actualTypeArguments) {
			addActualTypeArgument(actualTypeArgument);
		}
		return (C) this;
	}

	@Override
	public <C extends CtExecutableReference<T>> C setDeclaringType(CtTypeReference<?> declaringType) {
		if (declaringType != null) {
			declaringType.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, TYPE, declaringType, this.declaringType);
		this.declaringType = declaringType;
		return (C) this;
	}

	@Override
	public <C extends CtExecutableReference<T>> C setType(CtTypeReference<T> type) {
		if (type != null) {
			type.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, TYPE, type, this.type);
		this.type = type;
		return (C) this;
	}

	@Override
	protected AnnotatedElement getActualAnnotatedElement() {
		if (isConstructor()) {
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
			if (!m.getDeclaringClass().isSynthetic() && m.isSynthetic()) {
				continue;
			}
			if (!m.getName().equals(getSimpleName())) {
				continue;
			}
			if (m.getParameterTypes().length != parameters.size()) {
				continue;
			}
			for (int i = 0; i < parameters.size(); i++) {
				Class<?> methodParameterType = m.getParameterTypes()[i];
				Class<?> currentParameterType = parameters.get(i).getActualClass();
				if (methodParameterType != currentParameterType) {
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
	}

	@Override
	public <C extends CtExecutableReference<T>> C setStatic(boolean stat) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, IS_STATIC, stat, this.stat);
		this.stat = stat;
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

	@Override
	public void replace(CtExecutableReference<?> reference) {
		super.replace(reference);
	}

	public Set<ModifierKind> getModifiers() {
		CtExecutable<T> e = getDeclaration();
		if (e != null) {
			if (e instanceof CtMethod) {
				return ((CtMethod<T>) e).getModifiers();
			} else if (e instanceof CtConstructor) {
				return ((CtConstructor<T>) e).getModifiers();
			}
			return CtElementImpl.emptySet();
		}
		Method m = getActualMethod();
		if (m != null) {
			return RtHelper.getModifiers(m.getModifiers());
		}
		Constructor<?> c = getActualConstructor();
		if (c != null) {
			return RtHelper.getModifiers(c.getModifiers());
		}
		return Collections.emptySet();
	}

	@Override
	public CtExecutableReference<?> getOverridingExecutable() {
		CtTypeReference<?> st = getDeclaringType().getSuperclass();
		CtTypeReference<Object> objectType = getFactory().Type().OBJECT;
		if (st == null) {
			return getOverloadedExecutable(objectType, objectType);
		}
		return getOverloadedExecutable(st, objectType);
	}

	private CtExecutableReference<?> getOverloadedExecutable(CtTypeReference<?> t, CtTypeReference<Object> objectType) {
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
	public <C extends CtActualTypeContainer> C addActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		if (actualTypeArgument == null) {
			return (C) this;
		}
		if (actualTypeArguments == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			actualTypeArguments = new ArrayList<>(METHOD_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		actualTypeArgument.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, TYPE_PARAMETER, this.actualTypeArguments, actualTypeArgument);
		actualTypeArguments.add(actualTypeArgument);
		return (C) this;
	}

	@Override
	public boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		if (actualTypeArguments == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, TYPE_PARAMETER, actualTypeArguments, actualTypeArguments.indexOf(actualTypeArgument), actualTypeArgument);
		return actualTypeArguments.remove(actualTypeArgument);
	}

	@Override
	public String getSignature() {
		final SignaturePrinter pr = new SignaturePrinter();
		pr.scan(this);
		return pr.getSignature();
	}

	@Override
	public CtExecutableReference<T> clone() {
		return (CtExecutableReference<T>) super.clone();
	}
}
