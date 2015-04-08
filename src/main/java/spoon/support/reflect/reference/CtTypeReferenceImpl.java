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
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import spoon.Launcher;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.util.RtHelper;

public class CtTypeReferenceImpl<T> extends CtReferenceImpl implements
		CtTypeReference<T> {
	private static final long serialVersionUID = 1L;

	List<CtTypeReference<?>> actualTypeArguments = CtElementImpl.EMPTY_LIST();

	List<CtAnnotation<? extends Annotation>> annotations = CtElementImpl.EMPTY_LIST();

	CtTypeReference<?> declaringType;

	CtPackageReference pack;

	boolean isSuperReference = false;

	public CtTypeReferenceImpl() {
		super();
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtTypeReference(this);
	}

	public CtTypeReference<?> box() {
		if (!isPrimitive()) {
			return this;
		}
		if (getSimpleName().equals("int")) {
			return factory.Type().createReference(Integer.class);
		}
		if (getSimpleName().equals("float")) {
			return factory.Type().createReference(Float.class);
		}
		if (getSimpleName().equals("long")) {
			return factory.Type().createReference(Long.class);
		}
		if (getSimpleName().equals("char")) {
			return factory.Type().createReference(Character.class);
		}
		if (getSimpleName().equals("double")) {
			return factory.Type().createReference(Double.class);
		}
		if (getSimpleName().equals("boolean")) {
			return factory.Type().createReference(Boolean.class);
		}
		if (getSimpleName().equals("short")) {
			return factory.Type().createReference(Short.class);
		}
		if (getSimpleName().equals("byte")) {
			return factory.Type().createReference(Byte.class);
		}
		if (getSimpleName().equals("void")) {
			return factory.Type().createReference(Void.class);
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getActualClass() {
		if (isPrimitive()) {
			String simpleN = getSimpleName();
			if (simpleN.equals("boolean")) {
				return (Class<T>) boolean.class;
			} else if (simpleN.equals("byte")) {
				return (Class<T>) byte.class;
			} else if (simpleN.equals("double")) {
				return (Class<T>) double.class;
			} else if (simpleN.equals("int")) {
				return (Class<T>) int.class;
			} else if (simpleN.equals("short")) {
				return (Class<T>) short.class;
			} else if (simpleN.equals("char")) {
				return (Class<T>) char.class;
			} else if (simpleN.equals("long")) {
				return (Class<T>) long.class;
			} else if (simpleN.equals("float")) {
				return (Class<T>) float.class;
			} else if (simpleN.equals("void")) {
				return (Class<T>) void.class;
			}
		}
		return findClass();
	}

	/**
	 * Finds the class requested in {@link #getActualClass()}, using the
	 * {@code ClassLoader} of the {@code Environment}
	 */
	@SuppressWarnings("unchecked")
	protected Class<T> findClass() {
		try {
			return (Class<T>) getFactory().getEnvironment().getClassLoader().loadClass(getQualifiedName());
		} catch (java.lang.ClassNotFoundException cnfe) {
			throw new spoon.support.reflect.reference.SpoonClassNotFoundException("cannot load class: "
					+ getQualifiedName() + " with class loader "
					+ Thread.currentThread().getContextClassLoader(), cnfe);
		}
	}

	public List<CtTypeReference<?>> getActualTypeArguments() {
		return actualTypeArguments;
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		A a = super.getAnnotation(annotationType);
		if (a == null) { // Couldn't get annotation from CtModel, trying with RT
			// reflection
			try {
				return getActualClass().getAnnotation(annotationType);
			} catch (RuntimeException e) {
				// if(e.getCause() instanceof ClassNotFoundException){
				// // the RT fails because either the classpath is not set-up
				// correctly, or the class is generated
				// // since I don't know how to tell one from the other, I'll
				// ignore the exception and return null.
				return null;
				// }
				// throw e;
			}
		}
		return a;
	}

	@Override
	protected AnnotatedElement getActualAnnotatedElement() {
		return getActualClass();
	}

	@SuppressWarnings("unchecked")
	public CtType<T> getDeclaration() {
		if (!isPrimitive() && !isAnonymous()) {
			return (CtType<T>) getFactory().Type()
					.get(getQualifiedName());
		}
		if (!isPrimitive() && isAnonymous()) {
			final CtType<?> rootType = getFactory().Type().get(getDeclaringType().getQualifiedName());
			final CtNewClass elements = rootType.getElements(new AbstractFilter<CtNewClass>(CtNewClass.class) {
				@Override
				public boolean matches(CtNewClass element) {
					return getSimpleName().equals(element.getAnonymousClass().getSimpleName());
				}
			}).get(0);
			return elements.getAnonymousClass();
		}
		return null;
	}

	public CtTypeReference<?> getDeclaringType() {
		return declaringType;
	}

	public CtPackageReference getPackage() {
		return pack;
	}

	public String getQualifiedName() {
		if (getDeclaringType() != null) {
			return getDeclaringType().getQualifiedName()
					+ CtType.INNERTTYPE_SEPARATOR + getSimpleName();
		} else if (getPackage() != null
				&& !getPackage().getSimpleName().equals(
				CtPackage.TOP_LEVEL_PACKAGE_NAME)) {
			return getPackage().getSimpleName() + CtPackage.PACKAGE_SEPARATOR
					+ getSimpleName();
		} else {
			return getSimpleName();
		}
	}

	public boolean isAssignableFrom(CtTypeReference<?> type) {
		if (type != null) {
			return type.isSubtypeOf(this);
		}
		return false;
	}

	public boolean isPrimitive() {
		return (getSimpleName().equals("boolean")
				|| getSimpleName().equals("byte")
				|| getSimpleName().equals("double")
				|| getSimpleName().equals("int")
				|| getSimpleName().equals("short")
				|| getSimpleName().equals("char")
				|| getSimpleName().equals("long")
				|| getSimpleName().equals("float") || getSimpleName().equals(
				"void"));
	}

	public boolean isSubtypeOf(CtTypeReference<?> type) {
		if (type instanceof CtTypeParameterReference) {
			return false;
		}
		if (NULL_TYPE_NAME.equals(getSimpleName()) || NULL_TYPE_NAME.equals(type.getSimpleName())) {
			return false;
		}
		// anonymous types cannot be resolved
		if (isAnonymous() || type.isAnonymous()) {
			return false;
		}
		if (isPrimitive() || type.isPrimitive()) {
			return equals(type);
		}
		CtType<?> superTypeDecl = (CtType<?>) type.getDeclaration();
		CtType<?> subTypeDecl = getDeclaration();
		if ((subTypeDecl == null) && (superTypeDecl == null)) {
			try {
				if (((this instanceof CtArrayTypeReference) && (type instanceof CtArrayTypeReference))) {
					return ((CtArrayTypeReference<?>) this).getComponentType()
							.isSubtypeOf(
									((CtArrayTypeReference<?>) type)
											.getComponentType());
				}
				Class<?> actualSubType = getActualClass();
				Class<?> actualSuperType = type.getActualClass();
				return actualSuperType.isAssignableFrom(actualSubType);
			} catch (Exception e) {
				Launcher.logger.error("cannot determine runtime types for '"
						+ this + "' (" + getActualClass() + ") and '" + type
						+ "' (" + type.getActualClass() + ")", e);
				return false;
			}
		}
		if (getQualifiedName().equals(type.getQualifiedName())) {
			return true;
		}
		if (subTypeDecl != null) {
			if (subTypeDecl instanceof CtType) {
				for (CtTypeReference<?> ref : ((CtType<?>) subTypeDecl)
						.getSuperInterfaces()) {
					if (ref.isSubtypeOf(type)) {
						return true;
					}
				}
				if (subTypeDecl instanceof CtClass) {
					if (getFactory().Type().OBJECT.equals(type)) {
						return true;
					}
					if (((CtClass<?>) subTypeDecl).getSuperclass() != null) {
						if (((CtClass<?>) subTypeDecl).getSuperclass().equals(
								type)) {
							return true;
						}
						return ((CtClass<?>) subTypeDecl).getSuperclass()
								.isSubtypeOf(type);
					}
				}
			}
			return false;
		} else {
			try {
				Class<?> actualSubType = getActualClass();
				for (Class<?> c : actualSubType.getInterfaces()) {
					if (getFactory().Type().createReference(c)
							.isSubtypeOf(type)) {
						return true;
					}
				}
				CtTypeReference<?> superType = getFactory().Type()
						.createReference(actualSubType.getSuperclass());
				if (superType.equals(type)) {
					return true;
				} else {
					return superType.isSubtypeOf(type);
				}
			} catch (Exception e) {
				Launcher.logger.error("cannot determine runtime types for '"
						+ this + "' and '" + type + "'", e);
				return false;
			}
		}
	}

	public void setActualTypeArguments(
			List<CtTypeReference<?>> actualTypeArguments) {
		this.actualTypeArguments = actualTypeArguments;
	}

	public void setDeclaringType(CtTypeReference<?> declaringType) {
		this.declaringType = declaringType;
	}

	public void setPackage(CtPackageReference pack) {
		this.pack = pack;
	}

	public CtTypeReference<?> unbox() {
		if (!isPrimitive()) {
			return this;
		}
		if (getActualClass() == Integer.class) {
			return factory.Type().createReference(int.class);
		}
		if (getActualClass() == Float.class) {
			return factory.Type().createReference(float.class);
		}
		if (getActualClass() == Long.class) {
			return factory.Type().createReference(long.class);
		}
		if (getActualClass() == Character.class) {
			return factory.Type().createReference(char.class);
		}
		if (getActualClass() == Double.class) {
			return factory.Type().createReference(double.class);
		}
		if (getActualClass() == Boolean.class) {
			return factory.Type().createReference(boolean.class);
		}
		if (getActualClass() == Short.class) {
			return factory.Type().createReference(short.class);
		}
		if (getActualClass() == Byte.class) {
			return factory.Type().createReference(byte.class);
		}
		if (getActualClass() == Void.class) {
			return factory.Type().createReference(void.class);
		}
		return this;
	}

	public Collection<CtFieldReference<?>> getDeclaredFields() {
		Collection<CtFieldReference<?>> l = new ArrayList<CtFieldReference<?>>();
		CtType<?> t = getDeclaration();
		if (t == null) {
			for (Field f : getActualClass().getDeclaredFields()) {
				l.add(getFactory().Field().createReference(f));
			}
			if (getActualClass().isAnnotation()) {
				for (Method m : getActualClass().getDeclaredMethods()) {
					CtTypeReference<?> retRef = getFactory().Type()
							.createReference(m.getReturnType());
					CtFieldReference<?> fr = getFactory().Field()
							.createReference(this, retRef, m.getName());
					// fr.
					l.add(fr);
				}
			}

		} else {
			return t.getDeclaredFields();
		}
		return l;
	}
	
	@Override
	public Collection<CtExecutableReference<?>> getDeclaredExecutables() {
		CtType<T> t = getDeclaration();
		if (t == null) {
			return RtHelper.getAllExecutables(getActualClass(), getFactory());
		} else {
			return t.getDeclaredExecutables();
		}
	}

	public Collection<CtFieldReference<?>> getAllFields() {
		CtType<?> t = getDeclaration();
		if (t == null) {
			return RtHelper.getAllFields(getActualClass(), getFactory());
		} else {
			return t.getAllFields();
		}
	}

	public Collection<CtExecutableReference<?>> getAllExecutables() {
		Collection<CtExecutableReference<?>> l = new ArrayList<CtExecutableReference<?>>();
		CtType<T> t = getDeclaration();
		if (t == null) {
			Class<?> c = getActualClass();
			for (Method m : c.getDeclaredMethods()) {
				l.add(getFactory().Method().createReference(m));
			}
			for (Constructor<?> cons : c.getDeclaredConstructors()) {
				CtExecutableReference<?> consRef = getFactory().Constructor()
						.createReference(cons);
				l.add(consRef);
			}
			Class<?> sc = c.getSuperclass();
			l.addAll(getFactory().Type().createReference(sc)
						.getAllExecutables());
		} else {
			return t.getAllExecutables();
		}
		return l;
	}

	public Set<ModifierKind> getModifiers() {
		CtType<T> t = getDeclaration();
		if (t != null) {
			return t.getModifiers();
		}
		Class<T> c = getActualClass();
		return RtHelper.getModifiers(c.getModifiers());
	}

	public CtTypeReference<?> getSuperclass() {
		CtType<T> t = getDeclaration();
		if (t != null) {
			return ((CtClass<T>) t).getSuperclass();
		} else {
			Class<T> c = getActualClass();
			Class<?> sc = c.getSuperclass();
			return getFactory().Type().createReference(sc);
		}
	}

	public Set<CtTypeReference<?>> getSuperInterfaces() {
		CtType<?> t = getDeclaration();
		if (t != null) {
			return t.getSuperInterfaces();
		} else {
			Class<?> c = getActualClass();
			Class<?>[] sis = c.getInterfaces();
			if ((sis != null) && (sis.length > 0)) {
				Set<CtTypeReference<?>> set = new TreeSet<CtTypeReference<?>>();
				for (Class<?> si : sis) {
					set.add(getFactory().Type().createReference(si));
				}
				return set;
			}
		}
		return new TreeSet<CtTypeReference<?>>();
	}

	@Override
	public boolean isAnonymous() {
		try {
			Integer.parseInt(getSimpleName());
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public boolean isSuperReference() {
		return isSuperReference;
	}

	public void setSuperReference(boolean b) {
		isSuperReference = b;
	}

	@Override
	public boolean addActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		if (actualTypeArguments == CtElementImpl
				.<CtTypeReference<?>>EMPTY_LIST()) {
			actualTypeArguments = new ArrayList<CtTypeReference<?>>();
		}
		return actualTypeArguments.add(actualTypeArgument);
	}

	@Override
	public boolean removeActualTypeArgument(
			CtTypeReference<?> actualTypeArgument) {
		if (actualTypeArguments == CtElementImpl
				.<CtTypeReference<?>>EMPTY_LIST()) {
			return false;
		}
		return actualTypeArguments.remove(actualTypeArgument);
	}

	@Override
	public boolean isInterface() {
		CtType<T> t = getDeclaration();
		if (t == null) {
			return getActualClass().isInterface();
		} else {
			return t.isInterface();
		}
	}

	@Override
	public List<CtAnnotation<? extends Annotation>> getTypeAnnotations() {
		return Collections.unmodifiableList(annotations);
	}

	@Override
	public void setTypeAnnotations(List<CtAnnotation<? extends Annotation>> annotations) {
		this.annotations = annotations;
	}

	@Override
	public boolean addTypeAnnotation(CtAnnotation<? extends Annotation> annotation) {
		if (annotation == null) {
			return false;
		}
		if ((List<?>) this.annotations == (List<?>) CtElementImpl.EMPTY_LIST()) {
			this.annotations = new ArrayList<CtAnnotation<? extends Annotation>>();
		}
		return !this.annotations.contains(annotation) && this.annotations.add(annotation);
	}

	@Override
	public boolean removeTypeAnnotation(CtAnnotation<? extends Annotation> annotation) {
		if (annotation == null) {
			return false;
		}
		return this.annotations.remove(annotation);
	}
	
}
