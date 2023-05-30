/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.reference;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.SpoonClassNotFoundException;
import spoon.support.util.RtHelper;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.Set;

import static spoon.reflect.path.CtRole.DECLARING_TYPE;
import static spoon.reflect.path.CtRole.IS_FINAL;
import static spoon.reflect.path.CtRole.IS_STATIC;

public class CtFieldReferenceImpl<T> extends CtVariableReferenceImpl<T> implements CtFieldReference<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = DECLARING_TYPE)
	CtTypeReference<?> declaringType;

	@MetamodelPropertyField(role = IS_FINAL)
	boolean fina = false;

	@MetamodelPropertyField(role = IS_STATIC)
	boolean stat = false;

	public CtFieldReferenceImpl() {
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtFieldReference(this);
	}

	@Override
	public Member getActualField() {
		CtTypeReference<?> typeRef = getDeclaringType();
		if (typeRef == null) {
			throw new SpoonException("Declaring type of field " + getSimpleName() + " isn't defined");
		}
		Class<?> clazz;
		try {
			clazz = typeRef.getActualClass();
		} catch (SpoonClassNotFoundException e) {
			if (getFactory().getEnvironment().getNoClasspath()) {
				Launcher.LOGGER.info("The class " + typeRef.getQualifiedName() + " of field " + getSimpleName() + " is not on class path. Problem ignored in noclasspath mode");
				return null;
			}
			throw e;
		}
		try {
			return clazz.getDeclaredField(getSimpleName());
		} catch (NoSuchFieldException e) {
			throw new SpoonException("The field " + getQualifiedName() + " was not found", e);
		}
	}

	@Override
	protected AnnotatedElement getActualAnnotatedElement() {
		return (AnnotatedElement) getActualField();
	}

	@Override
	@SuppressWarnings("unchecked")
	public CtField<T> getDeclaration() {
		return fromDeclaringType();
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
		CtTypeReference<?> declaringType = getDeclaringType();

		if (declaringType != null) {
			return getDeclaringType().getQualifiedName() + "#" + getSimpleName();
		} else {
			return  "<unknown>#" + getSimpleName();
		}
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
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, DECLARING_TYPE, declaringType, this.declaringType);
		this.declaringType = declaringType;
		return (C) this;
	}

	@Override
	public <C extends CtFieldReference<T>> C setFinal(boolean fina) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, IS_FINAL, fina, this.fina);
		this.fina = fina;
		return (C) this;
	}

	@Override
	public <C extends CtFieldReference<T>> C setStatic(boolean stat) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, IS_STATIC, stat, this.stat);
		this.stat = stat;
		return (C) this;
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		// special-case the length field of array, as it doesn't have a declaration
		// as arrays only have one field, we do not need to check the name additionally
		CtTypeReference<?> declaringType = getDeclaringType();
		if (declaringType != null && declaringType.isArray()) {
			return Set.of(ModifierKind.PUBLIC, ModifierKind.FINAL);
		}
		CtVariable<?> v = getDeclaration();
		if (v != null) {
			return v.getModifiers();
		}
		// the modifiers of the "class" of AClass.class is the empty set
		if (this.isParentInitialized()
				&& this.getParent() instanceof CtFieldAccess
				&& ((CtFieldAccess) this.getParent()).getTarget() instanceof CtTypeAccess) {
			return emptySet();
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
