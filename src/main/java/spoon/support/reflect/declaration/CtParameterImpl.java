/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.reflect.CtModifierHandler;
import spoon.support.reflect.code.CtLocalVariableImpl;

import java.util.Set;

/**
 * The implementation for {@link spoon.reflect.declaration.CtParameter}.
 *
 * @author Renaud Pawlak
 */
public class CtParameterImpl<T> extends CtNamedElementImpl implements CtParameter<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.TYPE)
	CtTypeReference<T> type;

	@MetamodelPropertyField(role = CtRole.IS_VARARGS)
	boolean varArgs = false;

	@MetamodelPropertyField(role = CtRole.MODIFIER)
	private CtModifierHandler modifierHandler = new CtModifierHandler(this);

	@MetamodelPropertyField(role = CtRole.IS_INFERRED)
	private boolean inferred;

	public CtParameterImpl() {
	}

	@Override
	public void accept(CtVisitor v) {
		v.visitCtParameter(this);
	}

	@Override
	@DerivedProperty
	public CtExpression<T> getDefaultExpression() {
		return null;
	}

	@Override
	public CtParameterReference<T> getReference() {
		return getFactory().Executable().createParameterReference(this);
	}

	@Override
	public CtTypeReference<T> getType() {
		return type;
	}

	@Override
	@UnsettableProperty
	public <C extends CtVariable<T>> C setDefaultExpression(CtExpression<T> defaultExpression) {
		// unsettable property
		return (C) this;
	}

	@Override
	public boolean isUnnamed() {
		return CtLocalVariableImpl.isUnnamed(this);
	}

	@Override
	public boolean isPartOfJointDeclaration() {
		// a parameter can never be part of a joint declaration
		return false;
	}

	@Override
	public boolean isInferred() {
		return this.inferred;
	}

	@Override
	public <U extends CtParameter<T>> U setInferred(boolean inferred) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_INFERRED, inferred, this.inferred);
		this.inferred = inferred;
		return (U) this;
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference type) {
		if (type != null) {
			type.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.TYPE, type, this.type);
		this.type = type;
		return (C) this;
	}

	@Override
	public boolean isVarArgs() {
		return varArgs;
	}

	@Override
	public <C extends CtParameter<T>> C setVarArgs(boolean varArgs) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_VARARGS, varArgs, this.varArgs);
		this.varArgs = varArgs;
		return (C) this;
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		return modifierHandler.getModifiers();
	}

	@Override
	public boolean hasModifier(ModifierKind modifier) {
		return getModifiers().contains(modifier);
	}

	@Override
	public <C extends CtModifiable> C setModifiers(Set<ModifierKind> modifiers) {
		modifierHandler.setModifiers(modifiers);
		return (C) this;
	}

	@Override
	public <C extends CtModifiable> C addModifier(ModifierKind modifier) {
		modifierHandler.addModifier(modifier);
		return (C) this;
	}

	@Override
	public <C extends CtModifiable> C removeModifier(ModifierKind modifier) {
		modifierHandler.removeModifier(modifier);
		return (C) this;
	}

	@Override
	public <C extends CtModifiable> C setVisibility(ModifierKind visibility) {
		modifierHandler.setVisibility(visibility);
		return (C) this;
	}

	@Override
	public ModifierKind getVisibility() {
		modifierHandler.getVisibility();
		return null;
	}

	@Override
	public CtExecutable<?> getParent() {
		return (CtExecutable<?>) super.getParent();
	}

	@Override
	public Set<CtExtendedModifier> getExtendedModifiers() {
		return this.modifierHandler.getExtendedModifiers();
	}

	@Override
	public <T extends CtModifiable> T setExtendedModifiers(Set<CtExtendedModifier> extendedModifiers) {
		this.modifierHandler.setExtendedModifiers(extendedModifiers);
		return (T) this;
	}


	@MetamodelPropertyField(role = CtRole.IS_SHADOW)
	boolean isShadow;

	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public <E extends CtShadowable> E setShadow(boolean isShadow) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_SHADOW, isShadow, this.isShadow);
		this.isShadow = isShadow;
		return (E) this;
	}

	@Override
	public CtParameter<T> clone() {
		return (CtParameter<T>) super.clone();
	}

	@Override
	public boolean isPublic() {
		return this.modifierHandler.isPublic();
	}

	@Override
	public boolean isPrivate() {
		return this.modifierHandler.isPrivate();
	}

	@Override
	public boolean isProtected() {
		return this.modifierHandler.isProtected();
	}

	@Override
	public boolean isFinal() {
		return this.modifierHandler.isFinal();
	}

	@Override
	public boolean isStatic() {
		return this.modifierHandler.isStatic();
	}

	@Override
	public boolean isAbstract() {
		return this.modifierHandler.isAbstract();
	}

	@Override
	public boolean isTransient() {
		return this.modifierHandler.isTransient();
	}

	@Override
	public boolean isVolatile() {
		return this.modifierHandler.isVolatile();
	}

	@Override
	public boolean isSynchronized() {
		return this.modifierHandler.isSynchronized();
	}

	@Override
	public boolean isNative() {
		return this.modifierHandler.isNative();
	}

	@Override
	public boolean isStrictfp() {
		return this.modifierHandler.isStrictfp();
	}
}
