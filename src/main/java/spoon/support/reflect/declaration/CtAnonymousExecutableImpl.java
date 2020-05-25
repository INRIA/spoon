/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.reflect.CtModifierHandler;

import java.util.List;
import java.util.Set;

public class CtAnonymousExecutableImpl extends CtExecutableImpl<Void> implements CtAnonymousExecutable {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.MODIFIER)
	private CtModifierHandler modifierHandler = new CtModifierHandler(this);

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtAnonymousExecutable(this);
	}

	@Override
	public <T extends CtModifiable> T addModifier(ModifierKind modifier) {
		modifierHandler.addModifier(modifier);
		return (T) this;
	}

	@Override
	public <T extends CtModifiable> T removeModifier(ModifierKind modifier) {
		modifierHandler.removeModifier(modifier);
		return (T) this;
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		return modifierHandler.getModifiers();
	}

	@Override
	public ModifierKind getVisibility() {
		if (getModifiers().contains(ModifierKind.PUBLIC)) {
			return ModifierKind.PUBLIC;
		}
		if (getModifiers().contains(ModifierKind.PROTECTED)) {
			return ModifierKind.PROTECTED;
		}
		if (getModifiers().contains(ModifierKind.PRIVATE)) {
			return ModifierKind.PRIVATE;
		}
		return null;
	}

	@Override
	public boolean hasModifier(ModifierKind modifier) {
		return modifierHandler.hasModifier(modifier);
	}

	@Override
	public <T extends CtModifiable> T setModifiers(Set<ModifierKind> modifiers) {
		modifierHandler.setModifiers(modifiers);
		return (T) this;
	}

	@Override
	public <T extends CtModifiable> T setVisibility(ModifierKind visibility) {
		modifierHandler.setVisibility(visibility);
		return (T) this;
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

	@Override
	@DerivedProperty
	public List<CtParameter<?>> getParameters() {
		return emptyList();
	}

	@Override
	@UnsettableProperty
	public CtExecutable setParameters(List list) {
		// unsettable property
		return this;
	}

	@Override
	@UnsettableProperty
	public CtExecutable addParameter(CtParameter parameter) {
		// unsettable property
		return this;
	}

	@Override
	@UnsettableProperty
	public boolean removeParameter(CtParameter parameter) {
		return false;
	}

	@Override
	@DerivedProperty
	public Set<CtTypeReference<? extends Throwable>> getThrownTypes() {
		return emptySet();
	}

	@Override
	@UnsettableProperty
	public CtExecutable setThrownTypes(Set thrownTypes) {
		// unsettable property
		return this;
	}

	@Override
	@UnsettableProperty
	public CtExecutable addThrownType(CtTypeReference throwType) {
		// unsettable property
		return this;
	}

	@Override
	@UnsettableProperty
	public boolean removeThrownType(CtTypeReference throwType) {
		// unsettable property
		return false;
	}

	@Override
	public String getSimpleName() {
		return "";
	}

	@Override
	@UnsettableProperty
	public <T extends CtNamedElement> T setSimpleName(String simpleName) {
		// unsettable property
		return (T) this;
	}

	@Override
	@DerivedProperty
	public CtTypeReference<Void> getType() {
		return factory.Type().VOID_PRIMITIVE;
	}

	@Override
	@UnsettableProperty
	public <C extends CtTypedElement> C setType(CtTypeReference<Void> type) {
		// unsettable property
		return (C) this;
	}

	@Override
	public CtAnonymousExecutable clone() {
		return (CtAnonymousExecutable) super.clone();
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
}
