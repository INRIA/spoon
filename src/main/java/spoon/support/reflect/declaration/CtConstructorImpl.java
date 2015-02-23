/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import spoon.delegate.ModifiableDelegate;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.delegate.ModifiableDelegateImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CtConstructorImpl<T> extends CtExecutableImpl<T> implements CtConstructor<T> {
	private static final long serialVersionUID = 1L;

	List<CtTypeReference<?>> formalTypeParameters = EMPTY_LIST();

	ModifiableDelegate modifiableDelegate = new ModifiableDelegateImpl();

	@Override
	public void setSimpleName(String simpleName) {
		throw new RuntimeException("Operation not allowed");
	}

	@Override
	public String getSimpleName() {
		return "<init>";
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtConstructor(this);
	}

	@SuppressWarnings("unchecked")
	public CtType<T> getDeclaringType() {
		return (CtType<T>) parent;
	}

	public CtTypeReference<T> getType() {
		if (getDeclaringType() == null)
			return null;
		return getDeclaringType().getReference();
	}

	public void setType(CtTypeReference<T> type) {
	}

	@Override
	public List<CtTypeReference<?>> getFormalTypeParameters() {
		return formalTypeParameters;
	}

	@Override
	public boolean addFormalTypeParameter(CtTypeReference<?> formalTypeParameter) {
		if (formalTypeParameter == null) {
			return false;
		}
		if (formalTypeParameters == CtElementImpl.<CtTypeReference<?>>EMPTY_LIST()) {
			formalTypeParameters = new ArrayList<CtTypeReference<?>>();
		}
		return formalTypeParameters.add(formalTypeParameter);
	}

	@Override
	public void setFormalTypeParameters(List<CtTypeReference<?>> formalTypeParameters) {
		this.formalTypeParameters = formalTypeParameters;
	}

	@Override
	public boolean removeFormalTypeParameter(CtTypeReference<?> formalTypeParameter) {
		return formalTypeParameter != null && formalTypeParameters.remove(formalTypeParameter);
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		return modifiableDelegate.getModifiers();
	}

	@Override
	public boolean hasModifier(ModifierKind modifier) {
		return modifiableDelegate.hasModifier(modifier);
	}

	@Override
	public void setModifiers(Set<ModifierKind> modifiers) {
		modifiableDelegate.setModifiers(modifiers);
	}

	@Override
	public boolean addModifier(ModifierKind modifier) {
		return modifiableDelegate.addModifier(modifier);
	}

	@Override
	public boolean removeModifier(ModifierKind modifier) {
		return modifiableDelegate.removeModifier(modifier);
	}

	@Override
	public void setVisibility(ModifierKind visibility) {
		modifiableDelegate.setVisibility(visibility);
	}

	@Override
	public ModifierKind getVisibility() {
		return modifiableDelegate.getVisibility();
	}
}
