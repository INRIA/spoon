/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.reference;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.filter.NamedElementFilter;
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
import static spoon.reflect.path.CtRole.DECLARING_TYPE;
import static spoon.reflect.path.CtRole.IS_STATIC;
import static spoon.reflect.path.CtRole.ARGUMENT_TYPE;
import static spoon.reflect.path.CtRole.TYPE;
import static spoon.reflect.path.CtRole.TYPE_ARGUMENT;

public class CtExecutableReferenceImpl<T> extends CtReferenceImpl implements CtExecutableReference<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = IS_STATIC)
	boolean stat = false;

	@MetamodelPropertyField(role = TYPE_ARGUMENT)
	List<CtTypeReference<?>> actualTypeArguments = CtElementImpl.emptyList();

	@MetamodelPropertyField(role = TYPE)
	CtTypeReference<?> declaringType;

	@MetamodelPropertyField(role = TYPE)
	/**
	 * For methods, stores the return type of the method. (not pretty-printed).
	 * For constructors, stores the type of the target constructor (pretty-printed).
	 */
	CtTypeReference<T> type;

	@MetamodelPropertyField(role = ARGUMENT_TYPE)
	List<CtTypeReference<?>> parameters = CtElementImpl.emptyList();

	public CtExecutableReferenceImpl() {
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
		CtTypeReference<?> declaringType = getDeclaringType();
		if (declaringType == null) {
			return null;
		}

		if (declaringType instanceof CtArrayTypeReference && this.isConstructor()) {
			CtConstructor constructor = this.getFactory().createInvisibleArrayConstructor();
			constructor.setType(declaringType);
			return constructor;
		}

		return getCtExecutable(declaringType.getTypeDeclaration());
	}

	private CtExecutable<T> getCtExecutable(CtType<?> typeDecl) {
		if (typeDecl == null) {
			return null;
		}
		CtTypeReference<?>[] arrayParameters = parameters.toArray(new CtTypeReferenceImpl<?>[0]);
		CtExecutable<T> method = typeDecl.getMethod(getSimpleName(), arrayParameters);
		if ((method == null) && (typeDecl instanceof CtClass) && this.isConstructor()) {
			try {
				CtClass<?> zeClass = (CtClass) typeDecl;
				CtConstructor<?> constructor = zeClass.getConstructor(arrayParameters);
				return (CtExecutable<T>) constructor;
			} catch (ClassCastException e) {
				Launcher.LOGGER.error(e.getMessage(), e);
			}
		} else if (method == null && getSimpleName().startsWith(CtExecutableReference.LAMBDA_NAME_PREFIX)) {
			final List<CtLambda> elements = typeDecl.getElements(new NamedElementFilter<>(CtLambda.class, getSimpleName()));
			if (elements.isEmpty()) {
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
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, ARGUMENT_TYPE, this.parameters, new ArrayList<>(this.parameters));
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
		checkMethodParameterTypeRef(parameter);
		parameter.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, ARGUMENT_TYPE, this.parameters, parameter);
		return this.parameters.add(parameter);
	}

	private void checkMethodParameterTypeRef(CtTypeReference<?> parameterType) {
		if (parameterType instanceof CtTypeParameterReference && !(parameterType instanceof CtWildcardReference)) {
			throw new SpoonException("CtExecutableReference cannot use CtTypeParameterReference. Use boundingType of CtTypeParameterReference instead.");
		}
		if (parameterType instanceof CtArrayTypeReference) {
			checkMethodParameterTypeRef(((CtArrayTypeReference<?>) parameterType).getComponentType());
		}
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
			CtTypeReference<?> declaringType = getDeclaringType();
			return declaringType != null && declaringType.isSubtypeOf(executable.getDeclaringType());
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
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, TYPE_ARGUMENT, this.actualTypeArguments, new ArrayList<>(this.actualTypeArguments));
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
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, DECLARING_TYPE, declaringType, this.declaringType);
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

		CtTypeReference<?> declaringType = getDeclaringType();
		if (declaringType == null) {
			return null;
		}
		method_loop:
		for (Method m : declaringType.getActualClass().getDeclaredMethods()) {
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

	@Override
	public Constructor<?> getActualConstructor() {
		List<CtTypeReference<?>> parameters = this.getParameters();

		CtTypeReference<?> declaringType = getDeclaringType();
		if (declaringType == null) {
			return null;
		}
		constructor_loop:
		for (Constructor<?> c : declaringType.getActualClass().getDeclaredConstructors()) {
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

	@Override
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
		CtTypeReference<Object> objectType = getFactory().Type().OBJECT;
		CtTypeReference<?> declaringType = getDeclaringType();
		if (declaringType == null) {
			return getOverloadedExecutable(objectType, objectType);
		}
		CtTypeReference<?> st = declaringType.getSuperclass();
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
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, TYPE_ARGUMENT, this.actualTypeArguments, actualTypeArgument);
		actualTypeArguments.add(actualTypeArgument);
		return (C) this;
	}

	@Override
	public boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		if (actualTypeArguments == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, TYPE_ARGUMENT, actualTypeArguments, actualTypeArguments.indexOf(actualTypeArgument), actualTypeArgument);
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
