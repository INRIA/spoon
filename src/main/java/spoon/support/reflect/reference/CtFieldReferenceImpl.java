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
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.RtHelper;

public class CtFieldReferenceImpl<T> extends CtVariableReferenceImpl<T>
		implements CtFieldReference<T> {
	private static final long serialVersionUID = 1L;

	CtTypeReference<?> declaringType;

	boolean fina = false;

	boolean stat = false;

	public CtFieldReferenceImpl() {
		super();
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtFieldReference(this);
	}

	public Member getActualField() {
		try {
			if (getDeclaringType().getActualClass().isAnnotation()) {
				return getDeclaringType().getActualClass().getDeclaredMethod(
						getSimpleName());
			}
			return getDeclaringType().getActualClass().getDeclaredField(
					getSimpleName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		A annotation = super.getAnnotation(annotationType);
		if (annotation != null) {
			return annotation;
		}
		// use reflection
		Class<?> c = getDeclaringType().getActualClass();
		if (c.isAnnotation()) {
			for (Method m : RtHelper.getAllMethods(c)) {
				if (!getSimpleName().equals(m.getName())) {
					continue;
				}
				m.setAccessible(true);
				return m.getAnnotation(annotationType);
			}
		} else {
			for (Field f : RtHelper.getAllFields(c)) {
				if (!getSimpleName().equals(f.getName())) {
					continue;
				}
				f.setAccessible(true);
				return f.getAnnotation(annotationType);
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
		for (Field f : RtHelper.getAllFields(c)) {
			if (!getSimpleName().equals(f.getName())) {
				continue;
			}
			f.setAccessible(true);
			return f.getAnnotations();
		}
		// If the fields belong to an annotation type, they are actually
		// methods
		for (Method m : RtHelper.getAllMethods(c)) {
			if (!getSimpleName().equals(m.getName())) {
				continue;
			}
			m.setAccessible(true);
			return m.getAnnotations();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public CtField<T> getDeclaration() {
		CtSimpleType<?> type = declaringType.getDeclaration();
		if ((declaringType != null) && (type != null)) {
			return (CtField<T>) type.getField(getSimpleName());
		}
		return null;
	}

	public CtTypeReference<?> getDeclaringType() {
		return declaringType;
	}

	public String getQualifiedName() {
		return getDeclaringType().getQualifiedName() + "#" + getSimpleName();
	}

	public boolean isFinal() {
		return fina;
	}

	/**
	 * Tells if the referenced field is static.
	 */
	public boolean isStatic() {
		return stat;
	}

	public void setDeclaringType(CtTypeReference<?> declaringType) {
		this.declaringType = declaringType;
	}

	public void setFinal(boolean b) {
		fina = b;
	}

	public void setStatic(boolean stat) {
		this.stat = stat;
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		CtVariable<?> v = getDeclaration();
		if (v != null) {
			return v.getModifiers();
		}
		Member m = getActualField();
		if (m != null) {
			return RtHelper.getModifiers(m.getModifiers());
		}
		return new TreeSet<ModifierKind>();
	}

}
