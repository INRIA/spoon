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

import spoon.Launcher;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.RtHelper;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.Set;

public class CtFieldReferenceImpl<T> extends CtVariableReferenceImpl<T> implements CtFieldReference<T> {
	private static final long serialVersionUID = 1L;

	CtTypeReference<?> declaringType;

	boolean fina = false;

	boolean stat = false;

	public CtFieldReferenceImpl() {
		super();
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtFieldReference(this);
	}

	@Override
	public Member getActualField() {
		try {
			if (getDeclaringType().getActualClass().isAnnotation()) {
				return getDeclaringType().getActualClass().getDeclaredMethod(
						getSimpleName());
			}
			return getDeclaringType().getActualClass().getDeclaredField(
					getSimpleName());
		} catch (Exception e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	protected AnnotatedElement getActualAnnotatedElement() {
		return (AnnotatedElement) getActualField();
	}

	// @Override
	// public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
	// A annotation = super.getAnnotation(annotationType);
	// if (annotation != null) {
	// return annotation;
	// }
	// // use reflection
	// Class<?> c = getDeclaringType().getActualClass();
	// if (c.isAnnotation()) {
	// for (Method m : RtHelper.getAllMethods(c)) {
	// if (!getSimpleName().equals(m.getName())) {
	// continue;
	// }
	// m.setAccessible(true);
	// return m.getAnnotation(annotationType);
	// }
	// } else {
	// for (Field f : RtHelper.getAllFields(c)) {
	// if (!getSimpleName().equals(f.getName())) {
	// continue;
	// }
	// f.setAccessible(true);
	// return f.getAnnotation(annotationType);
	// }
	// }
	// return null;
	// }

	// @Override
	// public Annotation[] getAnnotations() {
	// Annotation[] annotations = super.getAnnotations();
	// if (annotations != null) {
	// return annotations;
	// }
	// // use reflection
	// Class<?> c = getDeclaringType().getActualClass();
	// for (Field f : RtHelper.getAllFields(c)) {
	// if (!getSimpleName().equals(f.getName())) {
	// continue;
	// }
	// f.setAccessible(true);
	// return f.getAnnotations();
	// }
	// // If the fields belong to an annotation type, they are actually
	// // methods
	// for (Method m : RtHelper.getAllMethods(c)) {
	// if (!getSimpleName().equals(m.getName())) {
	// continue;
	// }
	// m.setAccessible(true);
	// return m.getAnnotations();
	// }
	// return null;
	// }

	@Override
	@SuppressWarnings("unchecked")
	public CtField<T> getDeclaration() {
		final CtField<T> ctField = lookupDynamically();
		if (ctField != null) {
			return ctField;
		}
		return fromDeclaringType();
	}

	private CtField<T> lookupDynamically() {
		CtElement element = this;
		CtField optional = null;
		String name = getSimpleName();
		try {
			do {
				CtType type = element.getParent(CtType.class);
				if (type == null) {
					return null;
				}
				final CtField potential = type.getField(name);
				if (potential != null) {
					optional = potential;
				}
				element = type;
			} while (optional == null);
		} catch (ParentNotInitializedException e) {
			return null;
		}
		return optional;
	}

	private CtField<T> fromDeclaringType() {
		if (declaringType == null) {
			return null;
		}
		CtType<?> type = declaringType.getDeclaration();
		if (type != null) {
			return (CtField<T>) type.getField(getSimpleName());
		}
		return null;
	}

	@Override
	public CtField<T> getFieldDeclaration() {
		if (declaringType == null) {
			return null;
		}
		CtType<?> type = declaringType.getTypeDeclaration();
		if (type != null) {
			final CtField<T> ctField = (CtField<T>) type.getField(getSimpleName());
			if (ctField == null && type instanceof CtEnum) {
				return ((CtEnum) type).getEnumValue(getSimpleName());
			}
			return ctField;
		}
		return null;
	}

	@Override
	public CtTypeReference<?> getDeclaringType() {
		return declaringType;
	}

	@Override
	public String getQualifiedName() {
		return getDeclaringType().getQualifiedName() + "#" + getSimpleName();
	}

	@Override
	public boolean isFinal() {
		return fina;
	}

	/**
	 * Tells if the referenced field is static.
	 */
	@Override
	public boolean isStatic() {
		return stat;
	}

	@Override
	public <C extends CtFieldReference<T>> C setDeclaringType(CtTypeReference<?> declaringType) {
		if (declaringType != null) {
			declaringType.setParent(this);
		}
		this.declaringType = declaringType;
		return (C) this;
	}

	@Override
	public <C extends CtFieldReference<T>> C setFinal(boolean b) {
		fina = b;
		return (C) this;
	}

	@Override
	public <C extends CtFieldReference<T>> C setStatic(boolean stat) {
		this.stat = stat;
		return (C) this;
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
		return Collections.emptySet();
	}

	@Override
	public CtFieldReference<T> clone() {
		return (CtFieldReference<T>) super.clone();
	}
}
