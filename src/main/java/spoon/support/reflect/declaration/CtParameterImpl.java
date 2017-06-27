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
import spoon.support.UnsettableProperty;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static spoon.reflect.path.CtRole.IS_SHADOW;
import static spoon.reflect.path.CtRole.IS_VARARGS;
import static spoon.reflect.path.CtRole.MODIFIER;
import static spoon.reflect.path.CtRole.TYPE;

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
	Set<ModifierKind> modifiers = CtElementImpl.emptySet();

	public CtParameterImpl() {
		super();
	}

	@Override
	public void accept(CtVisitor v) {
		v.visitCtParameter(this);
	}

	@Override
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
	public <C extends CtTypedElement> C setType(CtTypeReference<T> type) {
		if (type != null) {
			type.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, TYPE, type, this.type);
		this.type = type;
		return (C) this;
	}

	@Override
	public boolean isVarArgs() {
		return varArgs;
	}

	@Override
	public <C extends CtParameter<T>> C setVarArgs(boolean varArgs) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, IS_VARARGS, varArgs, this.varArgs);
		this.varArgs = varArgs;
		return (C) this;
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
			getFactory().getEnvironment().getModelChangeListener().onSetDeleteAll(this, MODIFIER, this.modifiers, new HashSet<>(this.modifiers));
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
		getFactory().getEnvironment().getModelChangeListener().onSetAdd(this, MODIFIER, this.modifiers, modifier);
		modifiers.add(modifier);
		return (C) this;
	}

	@Override
	public boolean removeModifier(ModifierKind modifier) {
		if (modifiers == CtElementImpl.<ModifierKind>emptySet()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onSetDelete(this, MODIFIER, modifiers, modifier);
		return modifiers.remove(modifier);
	}

	@Override
	public <C extends CtModifiable> C setVisibility(ModifierKind visibility) {
		if (modifiers == CtElementImpl.<ModifierKind>emptySet()) {
			this.modifiers = EnumSet.noneOf(ModifierKind.class);
		}
		removeModifier(ModifierKind.PUBLIC);
		removeModifier(ModifierKind.PROTECTED);
		removeModifier(ModifierKind.PRIVATE);
		addModifier(visibility);
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
	public CtExecutable<?> getParent() {
		return (CtExecutable<?>) super.getParent();
	}

	@MetamodelPropertyField(role = CtRole.IS_SHADOW)
	boolean isShadow;

	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public <E extends CtShadowable> E setShadow(boolean isShadow) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, IS_SHADOW, isShadow, this.isShadow);
		this.isShadow = isShadow;
		return (E) this;
	}

	@Override
	public CtParameter<T> clone() {
		return (CtParameter<T>) super.clone();
	}
}
