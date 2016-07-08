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
package spoon.support.reflect.code;

import spoon.diff.AddAction;
import spoon.diff.DeleteAction;
import spoon.diff.DeleteAllAction;
import spoon.diff.UpdateAction;
import spoon.diff.context.ListContext;
import spoon.diff.context.ObjectContext;
import spoon.diff.context.SetContext;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtMultiTypedElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static spoon.reflect.ModelElementContainerDefaultCapacities.CATCH_VARIABLE_MULTI_TYPES_CONTAINER_DEFAULT_CAPACITY;

public class CtCatchVariableImpl<T> extends CtCodeElementImpl implements CtCatchVariable<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.NAME)
	String name = "";

	@MetamodelPropertyField(role = CtRole.TYPE)
	CtTypeReference<T> type;

	@MetamodelPropertyField(role = CtRole.TYPE)
	List<CtTypeReference<?>> types = emptyList();

	@MetamodelPropertyField(role = CtRole.MODIFIER)
	Set<ModifierKind> modifiers = CtElementImpl.emptySet();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtCatchVariable(this);
	}

	@Override
	@DerivedProperty
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
	@UnsettableProperty
	public <C extends CtVariable<T>> C setDefaultExpression(CtExpression<T> defaultExpression) {
		// unsettable property
		return (C) this;
	}

	@Override
	public <C extends CtNamedElement> C setSimpleName(String simpleName) {
		if (getFactory().getEnvironment().buildStackChanges()) {
			getFactory().getEnvironment().pushToStack(new UpdateAction(new ObjectContext(this, "name"), simpleName, this.name));
		}
		this.name = simpleName;
		return (C) this;
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference<T> type) {
		if (type != null) {
			type.setParent(this);
		}
		if (getFactory().getEnvironment().buildStackChanges()) {
			getFactory().getEnvironment().pushToStack(new UpdateAction(new ObjectContext(this, "type"), type, this.type));
		}
		this.type = type;
		return (C) this;
	}

	@Override
	public <T extends CtMultiTypedElement> T addMultiType(CtTypeReference<?> type) {
		if (type == null) {
			return (T) this;
		}
		if (types == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			types = new ArrayList<>(CATCH_VARIABLE_MULTI_TYPES_CONTAINER_DEFAULT_CAPACITY);
		}
		type.setParent(this);
		if (getFactory().getEnvironment().buildStackChanges()) {
			getFactory().getEnvironment().pushToStack(new AddAction(new ListContext(
					this, this.types), type));
		}
		types.add(type);
		return (T) this;
	}

	@Override
	public boolean removeMultiType(CtTypeReference<?> ref) {
		if (this.types == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			return false;
		}
		if (getFactory().getEnvironment().buildStackChanges()) {
			getFactory().getEnvironment().pushToStack(new DeleteAction(new ListContext(
					this, types, types.indexOf(ref)), ref));
		}
		return types.remove(ref);
	}

	@Override
	public List<CtTypeReference<?>> getMultiTypes() {
		return types;
	}

	@Override
	public <T extends CtMultiTypedElement> T setMultiTypes(List<CtTypeReference<?>> types) {
		if (getFactory().getEnvironment().buildStackChanges()) {
			getFactory().getEnvironment().pushToStack(new DeleteAllAction(new ListContext(this, this.types), new ArrayList<>(this.types)));
		}
		if (types == null || types.isEmpty()) {
			this.types = CtElementImpl.emptyList();
			return (T) this;
		}
		if (this.types == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			this.types = new ArrayList<>();
		}
		this.types.clear();
		for (CtTypeReference<?> t : types) {
			addMultiType(t);
		}
		return (T) this;
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
		if (modifiers.size() > 0) {
			if (getFactory().getEnvironment().buildStackChanges()) {
				getFactory().getEnvironment().pushToStack(new DeleteAllAction(new SetContext(this, this.modifiers), new HashSet<>(this.modifiers)));
			}
			this.modifiers.clear();
			for (ModifierKind modifier : modifiers) {
				addModifier(modifier);
			}
		}
		return (C) this;
	}

	@Override
	public <C extends CtModifiable> C addModifier(ModifierKind modifier) {
		if (modifiers == CtElementImpl.<ModifierKind>emptySet()) {
			this.modifiers = EnumSet.noneOf(ModifierKind.class);
		}
		if (getFactory().getEnvironment().buildStackChanges()) {
			getFactory().getEnvironment().pushToStack(new AddAction(new SetContext(
					this, this.modifiers), modifier));
		}
		modifiers.add(modifier);
		return (C) this;
	}

	@Override
	public boolean removeModifier(ModifierKind modifier) {
		if (modifiers == CtElementImpl.<ModifierKind>emptySet()) {
			return false;
		}
		if (getFactory().getEnvironment().buildStackChanges()) {
			getFactory().getEnvironment().pushToStack(new DeleteAction(new SetContext(
					this, modifiers), modifier));
		}
		return modifiers.remove(modifier);
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

	@Override
	public CtCatchVariable<T> clone() {
		return (CtCatchVariable<T>) super.clone();
	}
}
