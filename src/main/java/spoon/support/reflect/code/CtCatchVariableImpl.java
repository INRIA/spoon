/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
import spoon.reflect.visitor.filter.SuperInheritanceHierarchyFunction;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.reflect.CtModifierHandler;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static spoon.reflect.ModelElementContainerDefaultCapacities.CATCH_VARIABLE_MULTI_TYPES_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.NAME;
import static spoon.reflect.path.CtRole.MULTI_TYPE;

public class CtCatchVariableImpl<T> extends CtCodeElementImpl implements CtCatchVariable<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.NAME)
	String name = "";

	@MetamodelPropertyField(role = MULTI_TYPE)
	List<CtTypeReference<?>> types = emptyList();

	@MetamodelPropertyField(role = CtRole.MODIFIER)
	private CtModifierHandler modifierHandler = new CtModifierHandler(this);

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

	@SuppressWarnings("unchecked")
	@Override
	@DerivedProperty
	public CtTypeReference<T> getType() {
		if (types.isEmpty()) {
			return null;
		} else if (types.size() == 1) {
			return (CtTypeReference<T>) types.get(0);
		}
		//compute common super type of exceptions
		List<CtTypeReference<?>> superTypesOfFirst = types.get(0).map(new SuperInheritanceHierarchyFunction()
				.includingInterfaces(false)
				.includingSelf(true)
				.returnTypeReferences(true)).list();
		if (superTypesOfFirst.isEmpty()) {
			return null;
		}
		int commonSuperTypeIdx = 0;
		//index of Throwable. Last is Object
		int throwableIdx = superTypesOfFirst.size() - 2;
		for (int i = 1; i < types.size() && commonSuperTypeIdx != throwableIdx; i++) {
			CtTypeReference<?> nextException = types.get(i);
			while (commonSuperTypeIdx < throwableIdx) {
				if (nextException.isSubtypeOf(superTypesOfFirst.get(commonSuperTypeIdx))) {
					//nextException is sub type of actually selected commonSuperType
					break;
				}
				//try next super type
				commonSuperTypeIdx++;
			}
		}
		return (CtTypeReference<T>) superTypesOfFirst.get(commonSuperTypeIdx);
	}

	@Override
	@UnsettableProperty
	public <C extends CtVariable<T>> C setDefaultExpression(CtExpression<T> defaultExpression) {
		// unsettable property
		return (C) this;
	}

	@Override
	public <C extends CtNamedElement> C setSimpleName(String simpleName) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, NAME, simpleName, this.name);
		this.name = simpleName;
		return (C) this;
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference<T> type) {
		setMultiTypes(type == null ? emptyList() : Collections.singletonList(type));
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
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, MULTI_TYPE, this.types, type);
		types.add(type);
		return (T) this;
	}

	@Override
	public boolean removeMultiType(CtTypeReference<?> ref) {
		if (this.types == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, MULTI_TYPE, types, types.indexOf(ref), ref);
		return types.remove(ref);
	}

	@Override
	public List<CtTypeReference<?>> getMultiTypes() {
		return types;
	}

	@Override
	public <T extends CtMultiTypedElement> T setMultiTypes(List<CtTypeReference<?>> types) {
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, MULTI_TYPE, this.types, new ArrayList<>(this.types));
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
	public Set<CtExtendedModifier> getExtendedModifiers() {
		return this.modifierHandler.getExtendedModifiers();
	}

	@Override
	public <C extends CtModifiable> C setExtendedModifiers(Set<CtExtendedModifier> extendedModifiers) {
		this.modifierHandler.setExtendedModifiers(extendedModifiers);
		return (C) this;
	}

	@Override
	public CtCatchVariable<T> clone() {
		return (CtCatchVariable<T>) super.clone();
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
