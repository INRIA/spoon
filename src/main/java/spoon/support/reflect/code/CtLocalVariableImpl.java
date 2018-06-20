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
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtRHSReceiver;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.reflect.CtModifierHandler;

import java.util.Set;

import static spoon.reflect.path.CtRole.DEFAULT_EXPRESSION;
import static spoon.reflect.path.CtRole.IS_INFERRED;
import static spoon.reflect.path.CtRole.NAME;
import static spoon.reflect.path.CtRole.TYPE;

public class CtLocalVariableImpl<T> extends CtStatementImpl implements CtLocalVariable<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.DEFAULT_EXPRESSION)
	CtExpression<T> defaultExpression;

	@MetamodelPropertyField(role = CtRole.NAME)
	String name = "";

	@MetamodelPropertyField(role = CtRole.TYPE)
	CtTypeReference<T> type;

	@MetamodelPropertyField(role = CtRole.MODIFIER)
	private CtModifierHandler modifierHandler = new CtModifierHandler(this);

	@MetamodelPropertyField(role = CtRole.IS_INFERRED)
	private boolean inferred;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtLocalVariable(this);
	}

	@Override
	public CtExpression<T> getDefaultExpression() {
		return defaultExpression;
	}

	@Override
	public CtLocalVariableReference<T> getReference() {
		return getFactory().Code().createLocalVariableReference(this);
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
		if (defaultExpression != null) {
			defaultExpression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, DEFAULT_EXPRESSION, defaultExpression, this.defaultExpression);
		this.defaultExpression = defaultExpression;
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
		if (type != null) {
			type.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, TYPE, type, this.type);
		this.type = type;
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
	public <T extends CtModifiable> T setExtendedModifiers(Set<CtExtendedModifier> extendedModifiers) {
		this.modifierHandler.setExtendedModifiers(extendedModifiers);
		return (T) this;
	}

	@Override
	@DerivedProperty
	public CtExpression<T> getAssignment() {
		return getDefaultExpression();
	}

	@Override
	@UnsettableProperty
	public <C extends CtRHSReceiver<T>> C setAssignment(CtExpression<T> assignment) {
		setDefaultExpression(assignment);
		return (C) this;
	}

	@Override
	public boolean isInferred() {
		return this.inferred;
	}

	@Override
	public <U extends CtLocalVariable<T>> U setInferred(boolean inferred) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, IS_INFERRED, inferred, this.inferred);
		this.inferred = inferred;
		return (U) this;
	}

	@Override
	public CtLocalVariable<T> clone() {
		return (CtLocalVariable<T>) super.clone();
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
