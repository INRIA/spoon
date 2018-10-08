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
package spoon.support.reflect.declaration;


import java.util.Set;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.reflect.CtModifierHandler;




/**
 * The implementation for {@link spoon.reflect.declaration.CtField}.
 *
 * @author Renaud Pawlak
 */
public class CtFieldImpl<T> extends CtNamedElementImpl implements CtField<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.DEFAULT_EXPRESSION)
	CtExpression<T> defaultExpression;

	@MetamodelPropertyField(role = CtRole.TYPE)
	CtTypeReference<T> type;

	@MetamodelPropertyField(role = CtRole.MODIFIER)
	private CtModifierHandler modifierHandler = new CtModifierHandler(this);

	public CtFieldImpl() {
	}

	@Override
	public void accept(CtVisitor v) {
		v.visitCtField(this);
	}

	@Override
	public CtType<?> getDeclaringType() {
		return (CtType<?>) parent;
	}

	@Override
	public <T> CtType<T> getTopLevelType() {
		return getDeclaringType().getTopLevelType();
	}

	@Override
	public CtExpression<T> getDefaultExpression() {
		return defaultExpression;
	}

	@Override
	public CtFieldReference<T> getReference() {
		return getFactory().Field().createReference(this);
	}

	@Override
	public CtTypeReference<T> getType() {
		return type;
	}

	@Override
	public CtFieldImpl<T> setDefaultExpression(CtExpression<T> defaultExpression) {
		if (defaultExpression != null) {
			defaultExpression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.DEFAULT_EXPRESSION, defaultExpression, this.defaultExpression);
		this.defaultExpression = defaultExpression;
		return this;
	}

	@Override
	public CtFieldImpl<T> setType(CtTypeReference<T> type) {
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
	public CtFieldImpl<T> setModifiers(Set<ModifierKind> modifiers) {
		modifierHandler.setModifiers(modifiers);
		return this;
	}

	@Override
	public CtFieldImpl<T> addModifier(ModifierKind modifier) {
		modifierHandler.addModifier(modifier);
		return this;
	}

	@Override
	public CtFieldImpl<T> removeModifier(ModifierKind modifier) {
		modifierHandler.removeModifier(modifier);
		return this;
	}

	@Override
	public Set<CtExtendedModifier> getExtendedModifiers() {
		return this.modifierHandler.getExtendedModifiers();
	}

	@Override
	public CtFieldImpl<T> setExtendedModifiers(Set<CtExtendedModifier> extendedModifiers) {
		this.modifierHandler.setExtendedModifiers(extendedModifiers);
		return this;
	}


	@Override
	public CtFieldImpl<T> setVisibility(ModifierKind visibility) {
		modifierHandler.setVisibility(visibility);
		return this;
	}

	@Override
	public ModifierKind getVisibility() {
		return modifierHandler.getVisibility();
	}

	@Override
	@DerivedProperty
	public CtExpression<T> getAssignment() {
		return getDefaultExpression();
	}

	@Override
	public CtFieldImpl<T> setAssignment(CtExpression<T> assignment) {
		setDefaultExpression(assignment);
		return this;
	}

	@MetamodelPropertyField(role = CtRole.IS_SHADOW)
	boolean isShadow;

	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public CtFieldImpl<T> setShadow(boolean isShadow) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_SHADOW, isShadow, this.isShadow);
		this.isShadow = isShadow;
		return this;
	}

	@Override
	public CtField<T> clone() {
		return (CtField<T>) super.clone();
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
