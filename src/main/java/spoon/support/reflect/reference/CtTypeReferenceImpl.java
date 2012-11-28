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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.RtHelper;

public class CtTypeReferenceImpl<T> extends CtReferenceImpl implements
		CtTypeReference<T> {
	private static final long serialVersionUID = 1L;

	List<CtTypeReference<?>> actualTypeArguments = new ArrayList<CtTypeReference<?>>();

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
		try {
			return (Class<T>) Thread.currentThread().getContextClassLoader()
					.loadClass(getQualifiedName());
		} catch (Exception e) {
			throw new RuntimeException(e);
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
				// return null;
				// }
				throw e;
			}
		}
		return a;
	}

	@Override
	public Annotation[] getAnnotations() {
		Annotation[] a = super.getAnnotations();
		if (a == null) {
			return getActualClass().getAnnotations();
		}
		return a;
	}

	@SuppressWarnings("unchecked")
	public CtSimpleType<T> getDeclaration() {
		if (!isPrimitive() && (getQualifiedName().length() > 0)) {
			if (getFactory().Template().isTemplate(this)) {
				return (CtSimpleType<T>) getFactory().Template().get(
						getQualifiedName());
			}
			return (CtSimpleType<T>) getFactory().Type()
					.get(getQualifiedName());
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
					+ CtSimpleType.INNERTTYPE_SEPARATOR + getSimpleName();
		} else if (getPackage() != null) {
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
		if (isPrimitive() || type.isPrimitive()) {
			return equals(type);
		}
		CtSimpleType<?> t2 = type.getDeclaration();
		CtSimpleType<?> t1 = getDeclaration();
		if ((t1 == null) && (t2 == null)) {
			try {
				if (((this instanceof CtArrayTypeReference) || (type instanceof CtArrayTypeReference))) {
					return ((CtArrayTypeReference<?>) this).getComponentType()
							.isSubtypeOf(
									((CtArrayTypeReference<?>) type)
											.getComponentType());
				}
				Class<?> c1 = getActualClass();
				// Class.forName(this.getQualifiedName());
				Class<?> c2 = type.getActualClass();
				// Class.forName(type.getQualifiedName());
				return c2.isAssignableFrom(c1);
			} catch (Exception e) {
				return false;
			}
		}
		if (getQualifiedName().equals(type.getQualifiedName())) {
			return true;
		}
		if (t1 != null) {
			if (t1 instanceof CtType) {
				for (CtTypeReference<?> ref : ((CtType<?>) t1)
						.getSuperInterfaces()) {
					if (ref.isSubtypeOf(type)) {
						return true;
					}
				}
				if (t1 instanceof CtClass) {
					if (getFactory().Type().createReference(Object.class)
							.equals(type)) {
						return true;
					}
					if (((CtClass<?>) t1).getSuperclass() != null) {
						if (((CtClass<?>) t1).getSuperclass().equals(type)) {
							return true;
						}
						return ((CtClass<?>) t1).getSuperclass().isSubtypeOf(
								type);
					}
				}
			}
			return false;
		}
		try {
			Class<?> c = getActualClass();
			// Class.forName(getQualifiedName());
			Class<?> candidate = type.getActualClass();
			// Class.forName(type.getQualifiedName());
			return candidate.isAssignableFrom(c);
		} catch (Exception e) {
			return false;
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
		CtSimpleType<?> t = getDeclaration();
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
			for (CtField<?> f : t.getFields()) {
				l.add(f.getReference());
			}
		}
		return l;
	}

	public Collection<CtExecutableReference<?>> getDeclaredExecutables() {
		Collection<CtExecutableReference<?>> l = new ArrayList<CtExecutableReference<?>>();
		CtSimpleType<T> t = getDeclaration();
		if (t == null) {
			for (Method m : getActualClass().getDeclaredMethods()) {
				l.add(getFactory().Method().createReference(m));
			}
			for (Constructor<?> c : getActualClass().getDeclaredConstructors()) {
				l.add(getFactory().Constructor().createReference(c));
			}
		} else {
			if (t instanceof CtType) {
				for (CtMethod<?> m : ((CtType<?>) t).getMethods()) {
					l.add(m.getReference());
				}
			}
			if (t instanceof CtClass) {
				for (CtConstructor<T> c : ((CtClass<T>) t).getConstructors()) {
					l.add(c.getReference());
				}
			}
		}
		return l;
	}

	public Collection<CtFieldReference<?>> getAllFields() {
		Collection<CtFieldReference<?>> l = new ArrayList<CtFieldReference<?>>();
		CtSimpleType<?> t = getDeclaration();
		if (t == null) {
			Class<?> c = getActualClass();
			if (c != null) {
				for (Field f : c.getDeclaredFields()) {
					l.add(getFactory().Field().createReference(f));
				}
				Class<?> sc = c.getSuperclass();
				if (sc != null) {
					l.addAll(getFactory().Type().createReference(sc)
							.getAllFields());
				}
			}
		} else {
			for (CtField<?> f : t.getFields()) {
				l.add(f.getReference());
			}
			if (t instanceof CtClass) {
				CtTypeReference<?> st = ((CtClass<?>) t).getSuperclass();
				if (st != null) {
					l.addAll(st.getAllFields());
				}
			}
		}
		return l;
	}

	public Collection<CtExecutableReference<?>> getAllExecutables() {
		Collection<CtExecutableReference<?>> l = new ArrayList<CtExecutableReference<?>>();
		CtSimpleType<T> t = getDeclaration();
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
			if (sc != null) {
				l.addAll(getFactory().Type().createReference(sc)
						.getAllExecutables());
			}
		} else {
			if (t instanceof CtType) {
				for (CtMethod<?> m : ((CtType<?>) t).getMethods()) {
					l.add(m.getReference());
				}
			}
			if (t instanceof CtClass) {
				for (CtConstructor<T> c : ((CtClass<T>) t).getConstructors()) {
					l.add(c.getReference());
				}
				CtTypeReference<?> st = ((CtClass<?>) t).getSuperclass();
				if (st != null) {
					l.addAll(st.getAllExecutables());
				}
			}
		}
		return l;
	}

	//
	// public Set<CtMethod<?>> getAllMethods() {
	// Set<CtMethod<?>> ret = new TreeSet<CtMethod<?>>();
	// ret.addAll(getMethods());
	//
	// for (CtTypeReference<?> ref : getSuperInterfaces()) {
	// if (ref.getDeclaration() != null) {
	// CtType<?> t = (CtType<?>) ref.getDeclaration();
	// ret.addAll(t.getAllMethods());
	// }
	// }
	// return ret;
	// }

	public Set<ModifierKind> getModifiers() {
		CtSimpleType<T> t = getDeclaration();
		if (t != null) {
			return t.getModifiers();
		}
		Class<T> c = getActualClass();
		if (c != null) {
			return RtHelper.getModifiers(c.getModifiers());
		}
		return new TreeSet<ModifierKind>();
	}

	public CtTypeReference<?> getSuperclass() {
		CtSimpleType<T> t = getDeclaration();
		if (t != null) {
			if (t instanceof CtClass) {
				return ((CtClass<T>) t).getSuperclass();
			}
		} else {
			Class<T> c = getActualClass();
			if (c != null) {
				Class<?> sc = c.getSuperclass();
				if (sc != null) {
					return getFactory().Type().createReference(sc);
				}
			}
		}
		return null;
	}

	public Set<CtTypeReference<?>> getSuperInterfaces() {
		CtSimpleType<?> t = getDeclaration();
		if (t != null) {
			if (t instanceof CtType) {
				return ((CtType<?>) t).getSuperInterfaces();
			}
		} else {
			Class<?> c = getActualClass();
			if (c != null) {
				Class<?>[] sis = c.getInterfaces();
				if ((sis != null) && (sis.length > 0)) {
					Set<CtTypeReference<?>> set = new TreeSet<CtTypeReference<?>>();
					for (Class<?> si : sis) {
						set.add(getFactory().Type().createReference(si));
					}
					return set;
				}
			}
		}
		return new TreeSet<CtTypeReference<?>>();
	}

	public boolean isAnonymous() {
		return getSimpleName().isEmpty();
	}

	public boolean isSuperReference() {
		return isSuperReference ;
	}
	
	public void setSuperReference(boolean b){
		isSuperReference = b;
	}

}
