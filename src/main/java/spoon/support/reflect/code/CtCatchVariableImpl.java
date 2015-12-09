/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.support.reflect.code;

import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtMultiTypedElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static spoon.reflect.ModelElementContainerDefaultCapacities
		.CATCH_VARIABLE_MULTI_TYPES_CONTAINER_DEFAULT_CAPACITY;

public class CtCatchVariableImpl<T> extends CtCodeElementImpl implements CtCatchVariable<T> {
	private static final long serialVersionUID = 1L;

	String name;

	CtTypeReference<T> type;

	List<CtTypeReference<?>> types = emptyList();

	Set<ModifierKind> modifiers = CtElementImpl.emptySet();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtCatchVariable(this);
	}

	@Override
	public CtExpression<T> getDefaultExpression() {
		return null;
	}

	@Override
	public CtCatchVariableReference<T> getReference() {
		return getFactory().Code().createCatchVariableReference(this);
	}

	@Override
	public String getSimpleName() {
		return name;
	}

	@Override
	public CtTypeReference<T> getType() {
		return type;
	}

	@Override
	public <C extends CtVariable<T>> C setDefaultExpression(CtExpression<T> defaultExpression) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C extends CtNamedElement> C setSimpleName(String simpleName) {
		this.name = simpleName;
		return (C) this;
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference<T> type) {
		if (type != null) {
			type.setParent(this);
		}
		this.type = type;
		return (C) this;
	}

	@Override
	public <T extends CtMultiTypedElement> T addMultiType(CtTypeReference<?> ref) {
		if (types == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			types = new ArrayList<CtTypeReference<?>>(
					CATCH_VARIABLE_MULTI_TYPES_CONTAINER_DEFAULT_CAPACITY);
		}
		ref.setParent(this);
		types.add(ref);
		return (T) this;
	}

	@Override
	public boolean removeMultiType(CtTypeReference<?> ref) {
		return types.remove(ref);
	}

	@Override
	public List<CtTypeReference<?>> getMultiTypes() {
		return types;
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		return modifiers;
	}

	@Override
	public boolean hasModifier(ModifierKind modifier) {
		return getModifiers().contains(modifier);
	}

	@Override
	public <C extends CtModifiable> C setModifiers(Set<ModifierKind> modifiers) {
		this.modifiers = modifiers;
		return (C) this;
	}

	@Override
	public <C extends CtModifiable> C addModifier(ModifierKind modifier) {
		if (modifiers == CtElementImpl.<ModifierKind>emptySet()) {
			this.modifiers = EnumSet.noneOf(ModifierKind.class);
		}
		modifiers.add(modifier);
		return (C) this;
	}

	@Override
	public boolean removeModifier(ModifierKind modifier) {
		return !modifiers.isEmpty() && modifiers.remove(modifier);
	}

	@Override
	public <C extends CtModifiable> C setVisibility(ModifierKind visibility) {
		if (modifiers == CtElementImpl.<ModifierKind>emptySet()) {
			this.modifiers = EnumSet.noneOf(ModifierKind.class);
		}
		getModifiers().remove(ModifierKind.PUBLIC);
		getModifiers().remove(ModifierKind.PROTECTED);
		getModifiers().remove(ModifierKind.PRIVATE);
		getModifiers().add(visibility);
		return (C) this;
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
}
