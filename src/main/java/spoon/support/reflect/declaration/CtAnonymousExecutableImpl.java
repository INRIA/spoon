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


import java.util.List;
import java.util.Set;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.reflect.CtModifierHandler;




public class CtAnonymousExecutableImpl extends CtExecutableImpl<Void> implements CtAnonymousExecutable {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.MODIFIER)
	private CtModifierHandler modifierHandler = new CtModifierHandler(this);

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtAnonymousExecutable(this);
	}

	@Override
	public CtAnonymousExecutableImpl addModifier(ModifierKind modifier) {
		modifierHandler.addModifier(modifier);
		return this;
	}

	@Override
	public CtAnonymousExecutableImpl removeModifier(ModifierKind modifier) {
		modifierHandler.removeModifier(modifier);
		return this;
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
	public CtAnonymousExecutableImpl setModifiers(Set<ModifierKind> modifiers) {
		modifierHandler.setModifiers(modifiers);
		return this;
	}

	@Override
	public CtAnonymousExecutableImpl setVisibility(ModifierKind visibility) {
		modifierHandler.setVisibility(visibility);
		return this;
	}

	@Override
	public Set<CtExtendedModifier> getExtendedModifiers() {
		return this.modifierHandler.getExtendedModifiers();
	}

	@Override
	public CtAnonymousExecutableImpl setExtendedModifiers(Set<CtExtendedModifier> extendedModifiers) {
		this.modifierHandler.setExtendedModifiers(extendedModifiers);
		return this;
	}

	@Override
	@DerivedProperty
	public List<CtParameter<?>> getParameters() {
		return emptyList();
	}

	@Override
	@UnsettableProperty
	public CtAnonymousExecutableImpl setParameters(List list) {
		// unsettable property
		return this;
	}

	@Override
	@UnsettableProperty
	public CtAnonymousExecutableImpl addParameter(CtParameter parameter) {
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
	public CtAnonymousExecutableImpl setThrownTypes(Set thrownTypes) {
		// unsettable property
		return this;
	}

	@Override
	@UnsettableProperty
	public CtAnonymousExecutableImpl addThrownType(CtTypeReference throwType) {
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
	public CtAnonymousExecutableImpl setSimpleName(String simpleName) {
		// unsettable property
		return this;
	}

	@Override
	@DerivedProperty
	public CtTypeReference<Void> getType() {
		return factory.Type().VOID_PRIMITIVE;
	}

	@Override
	@UnsettableProperty
	public CtAnonymousExecutableImpl setType(CtTypeReference<Void> type) {
		// unsettable property
		return this;
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
