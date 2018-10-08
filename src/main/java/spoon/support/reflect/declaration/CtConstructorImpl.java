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


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.reflect.CtModifierHandler;

import static spoon.reflect.ModelElementContainerDefaultCapacities.TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY;




public class CtConstructorImpl<T> extends CtExecutableImpl<T> implements CtConstructor<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.TYPE_PARAMETER)
	List<CtTypeParameter> formalCtTypeParameters = emptyList();

	@MetamodelPropertyField(role = CtRole.MODIFIER)
	private CtModifierHandler modifierHandler = new CtModifierHandler(this);

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtConstructor(this);
	}

	@Override
	@UnsettableProperty
	public CtConstructorImpl<T> setSimpleName(String simpleName) {
		return this;
	}

	@Override
	public String getSimpleName() {
		return CtExecutableReference.CONSTRUCTOR_NAME;
	}

	@Override
	@SuppressWarnings("unchecked")
	public CtType<T> getDeclaringType() {
		return (CtType<T>) parent;
	}

	@Override
	@DerivedProperty
	public CtTypeReference<T> getType() {
		if (getDeclaringType() == null) {
			return null;
		}
		return getDeclaringType().getReference();
	}

	@Override
	@UnsettableProperty
	public CtConstructorImpl<T> setType(CtTypeReference<T> type) {
		// unsettable property
		return this;
	}

	@Override
	public List<CtTypeParameter> getFormalCtTypeParameters() {
		return formalCtTypeParameters;
	}

	@Override
	public CtConstructorImpl<T> setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters) {
		if (formalTypeParameters == null || formalTypeParameters.isEmpty()) {
			this.formalCtTypeParameters = CtElementImpl.emptyList();
			return this;
		}
		if (this.formalCtTypeParameters == CtElementImpl.<CtTypeParameter>emptyList()) {
			this.formalCtTypeParameters = new ArrayList<>(TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.TYPE_PARAMETER, this.formalCtTypeParameters, new ArrayList<>(this.formalCtTypeParameters));
		this.formalCtTypeParameters.clear();
		for (CtTypeParameter formalTypeParameter : formalTypeParameters) {
			addFormalCtTypeParameter(formalTypeParameter);
		}
		return this;
	}

	@Override
	public CtConstructorImpl<T> addFormalCtTypeParameter(CtTypeParameter formalTypeParameter) {
		if (formalTypeParameter == null) {
			return this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.TYPE_PARAMETER, this.formalCtTypeParameters, formalTypeParameter);
		if (formalCtTypeParameters == CtElementImpl.<CtTypeParameter>emptyList()) {
			formalCtTypeParameters = new ArrayList<>(TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		formalTypeParameter.setParent(this);
		formalCtTypeParameters.add(formalTypeParameter);
		return this;
	}

	@Override
	public boolean removeFormalCtTypeParameter(CtTypeParameter formalTypeParameter) {
		if (!formalCtTypeParameters.contains(formalTypeParameter)) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CtRole.TYPE_PARAMETER, formalCtTypeParameters, formalCtTypeParameters.indexOf(formalTypeParameter), formalTypeParameter);
		return formalCtTypeParameters.remove(formalTypeParameter);
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
	public CtConstructorImpl<T> setModifiers(Set<ModifierKind> modifiers) {
		modifierHandler.setModifiers(modifiers);
		return this;
	}

	@Override
	public CtConstructorImpl<T> addModifier(ModifierKind modifier) {
		modifierHandler.addModifier(modifier);
		return this;
	}

	@Override
	public CtConstructorImpl<T> removeModifier(ModifierKind modifier) {
		modifierHandler.removeModifier(modifier);
		return this;
	}

	@Override
	public CtConstructorImpl<T> setVisibility(ModifierKind visibility) {
		modifierHandler.setVisibility(visibility);
		return this;
	}

	@Override
	public ModifierKind getVisibility() {
		return modifierHandler.getVisibility();
	}

	@Override
	public Set<CtExtendedModifier> getExtendedModifiers() {
		return this.modifierHandler.getExtendedModifiers();
	}

	@Override
	public CtConstructorImpl<T> setExtendedModifiers(Set<CtExtendedModifier> extendedModifiers) {
		this.modifierHandler.setExtendedModifiers(extendedModifiers);
		return this;
	}


	@MetamodelPropertyField(role = CtRole.IS_SHADOW)
	boolean isShadow;

	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public CtConstructorImpl<T> setShadow(boolean isShadow) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_SHADOW, isShadow, this.isShadow);
		this.isShadow = isShadow;
		return this;
	}

	@Override
	public CtConstructor<T> clone() {
		return (CtConstructor<T>) super.clone();
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
