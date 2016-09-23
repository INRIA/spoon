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

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static spoon.reflect.ModelElementContainerDefaultCapacities.TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY;

/**
 * The implementation for {@link spoon.reflect.declaration.CtMethod}.
 *
 * @author Renaud Pawlak
 */
public class CtMethodImpl<T> extends CtExecutableImpl<T> implements CtMethod<T> {
	private static final long serialVersionUID = 1L;

	CtTypeReference<T> returnType;

	boolean defaultMethod = false;

	List<CtTypeParameterReference> formalTypeParameters = emptyList();

	List<CtTypeParameter> formalCtTypeParameters = emptyList();

	Set<ModifierKind> modifiers = CtElementImpl.emptySet();

	public CtMethodImpl() {
		super();
	}

	@Override
	public void accept(CtVisitor v) {
		v.visitCtMethod(this);
	}

	@Override
	public CtTypeReference<T> getType() {
		return returnType;
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference<T> type) {
		if (type != null) {
			type.setParent(this);
		}
		this.returnType = type;
		return (C) this;
	}

	@Override
	public CtType<?> getDeclaringType() {
		return (CtType<?>) parent;
	}

	@Override
	public boolean isDefaultMethod() {
		return defaultMethod;
	}

	@Override
	public <C extends CtMethod<T>> C setDefaultMethod(boolean defaultMethod) {
		this.defaultMethod = defaultMethod;
		return (C) this;
	}

	@Override
	public List<CtTypeParameter> getFormalCtTypeParameters() {
		return formalCtTypeParameters;
	}

	@Override
	public <C extends CtFormalTypeDeclarer> C setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters) {
		if (formalTypeParameters == null || formalTypeParameters.isEmpty()) {
			this.formalCtTypeParameters = CtElementImpl.emptyList();
			return (C) this;
		}
		if (this.formalCtTypeParameters == CtElementImpl.<CtTypeParameter>emptyList()) {
			this.formalCtTypeParameters = new ArrayList<>(TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		this.formalCtTypeParameters.clear();
		for (CtTypeParameter formalTypeParameter : formalTypeParameters) {
			addFormalCtTypeParameter(formalTypeParameter);
		}
		return (C) this;
	}

	@Override
	public <C extends CtFormalTypeDeclarer> C addFormalCtTypeParameter(CtTypeParameter formalTypeParameter) {
		if (formalTypeParameter == null) {
			return (C) this;
		}
		if (formalCtTypeParameters == CtElementImpl.<CtTypeParameter>emptyList()) {
			formalCtTypeParameters = new ArrayList<>(TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		formalTypeParameter.setParent(this);
		formalCtTypeParameters.add(formalTypeParameter);
		return (C) this;
	}

	@Override
	public boolean removeFormalCtTypeParameter(CtTypeParameter formalTypeParameter) {
		return formalCtTypeParameters.contains(formalTypeParameter) && formalCtTypeParameters.remove(formalTypeParameter);
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
			this.modifiers = EnumSet.copyOf(modifiers);
		}
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

	@Override
	public <R extends T> void replace(CtMethod<T> element) {
		replace((CtElement) element);
	}

	boolean isShadow;

	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public <E extends CtShadowable> E setShadow(boolean isShadow) {
		this.isShadow = isShadow;
		return (E) this;
	}

	@Override
	public CtMethod<T> clone() {
		return (CtMethod<T>) super.clone();
	}
}
