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
package spoon.support.reflect.declaration;

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
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.support.UnsettableProperty;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class CtAnonymousExecutableImpl extends CtExecutableImpl<Void> implements CtAnonymousExecutable {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.MODIFIER)
	Set<ModifierKind> modifiers = emptySet();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtAnonymousExecutable(this);
	}

	@Override
	public <T extends CtModifiable> T addModifier(ModifierKind modifier) {
		if (modifiers == CtElementImpl.<ModifierKind>emptySet()) {
			modifiers = EnumSet.noneOf(ModifierKind.class);
		}
		modifiers.add(modifier);
		return (T) this;
	}

	@Override
	public boolean removeModifier(ModifierKind modifier) {
		return !modifiers.isEmpty() && modifiers.remove(modifier);
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		return modifiers;
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
		return modifiers.contains(modifier);
	}

	@Override
	public <T extends CtModifiable> T setModifiers(Set<ModifierKind> modifiers) {
		if (modifiers.size() > 0) {
			this.modifiers = EnumSet.copyOf(modifiers);
		}
		return (T) this;
	}

	@Override
	public <T extends CtModifiable> T setVisibility(ModifierKind visibility) {
		if (modifiers == CtElementImpl.<ModifierKind>emptySet()) {
			modifiers = EnumSet.noneOf(ModifierKind.class);
		}
		getModifiers().remove(ModifierKind.PUBLIC);
		getModifiers().remove(ModifierKind.PROTECTED);
		getModifiers().remove(ModifierKind.PRIVATE);
		getModifiers().add(visibility);
		return (T) this;
	}

	@Override
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
}
