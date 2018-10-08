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


import java.util.Set;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.reflect.CtModifierHandler;




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
	public CtLocalVariableImpl<T> setDefaultExpression(CtExpression<T> defaultExpression) {
		if (defaultExpression != null) {
			defaultExpression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.DEFAULT_EXPRESSION, defaultExpression, this.defaultExpression);
		this.defaultExpression = defaultExpression;
		return this;
	}

	@Override
	public CtLocalVariableImpl<T> setSimpleName(String simpleName) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.NAME, simpleName, this.name);
		this.name = simpleName;
		return this;
	}

	@Override
	public CtLocalVariableImpl<T> setType(CtTypeReference<T> type) {
		if (type != null) {
			type.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.TYPE, type, this.type);
		this.type = type;
		return this;
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
	public CtLocalVariableImpl<T> setModifiers(Set<ModifierKind> modifiers) {
		modifierHandler.setModifiers(modifiers);
		return this;
	}

	@Override
	public CtLocalVariableImpl<T> addModifier(ModifierKind modifier) {
		modifierHandler.addModifier(modifier);
		return this;
	}

	@Override
	public CtLocalVariableImpl<T> removeModifier(ModifierKind modifier) {
		modifierHandler.removeModifier(modifier);
		return this;
	}

	@Override
	public CtLocalVariableImpl<T> setVisibility(ModifierKind visibility) {
		modifierHandler.setVisibility(visibility);
		return this;
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
	public CtLocalVariableImpl<T> setExtendedModifiers(Set<CtExtendedModifier> extendedModifiers) {
		this.modifierHandler.setExtendedModifiers(extendedModifiers);
		return this;
	}

	@Override
	@DerivedProperty
	public CtExpression<T> getAssignment() {
		return getDefaultExpression();
	}

	@Override
	@UnsettableProperty
	public CtLocalVariableImpl<T> setAssignment(CtExpression<T> assignment) {
		setDefaultExpression(assignment);
		return this;
	}

	@Override
	public boolean isInferred() {
		return this.inferred;
	}

	@Override
	public CtLocalVariableImpl<T> setInferred(boolean inferred) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_INFERRED, inferred, this.inferred);
		this.inferred = inferred;
		return this;
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
