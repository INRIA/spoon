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
package spoon.support.reflect.declaration;

import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class CtAnonymousExecutableImpl extends CtExecutableImpl<Void> implements CtAnonymousExecutable {
	private static final long serialVersionUID = 1L;

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
	public CtClass<?> getDeclaringType() {
		return (CtClass<?>) parent;
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
	public CtExecutable setParameters(List list) {
		throw new UnsupportedOperationException("You can't have parameters in a anonymous executable");
	}

	@Override
	public CtExecutable addParameter(CtParameter parameter) {
		throw new UnsupportedOperationException("You can't have parameters in a anonymous executable");
	}

	@Override
	public boolean removeParameter(CtParameter parameter) {
		throw new UnsupportedOperationException("You can't have parameters in a anonymous executable");
	}

	@Override
	public Set<CtTypeReference<? extends Throwable>> getThrownTypes() {
		return emptySet();
	}

	@Override
	public CtExecutable setThrownTypes(Set thrownTypes) {
		throw new UnsupportedOperationException("You can't have throw types in a anonymous executable");
	}

	@Override
	public CtExecutable addThrownType(CtTypeReference throwType) {
		throw new UnsupportedOperationException("You can't have throw types in a anonymous executable");
	}

	@Override
	public boolean removeThrownType(CtTypeReference throwType) {
		throw new UnsupportedOperationException("You can't have throw types in a anonymous executable");
	}

	@Override
	public String getSimpleName() {
		return "";
	}

	@Override
	public <T extends CtNamedElement> T setSimpleName(String simpleName) {
		throw new UnsupportedOperationException("You can't have a name in a anonymous executable");
	}

	@Override
	public CtTypeReference<Void> getType() {
		return factory.Type().VOID_PRIMITIVE;
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference<Void> type) {
		throw new UnsupportedOperationException("An anonymous executable isn't typed");
	}

	@Override
	public CtAnonymousExecutable clone() {
		return (CtAnonymousExecutable) super.clone();
	}
}
