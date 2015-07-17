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

import java.util.EnumSet;
import java.util.Set;

import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

/**
 * The implementation for {@link spoon.reflect.declaration.CtParameter}.
 * 
 * @author Renaud Pawlak
 */
public class CtParameterImpl<T> extends CtNamedElementImpl implements CtParameter<T> {
	private static final long serialVersionUID = 1L;

	CtTypeReference<T> type;

	boolean varArgs = false;

	Set<ModifierKind> modifiers = CtElementImpl.EMPTY_SET();

	public CtParameterImpl() {
		super();
	}

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

	public CtTypeReference<T> getType() {
		return type;
	}

	public void setDefaultExpression(CtExpression<T> defaultExpression) {
		throw new UnsupportedOperationException();
	}

	public void setType(CtTypeReference<T> type) {
		this.type = type;
	}

	public boolean isVarArgs() {
		return varArgs;
	}

	public void setVarArgs(boolean varArgs) {
		this.varArgs = varArgs;
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
	public void setModifiers(Set<ModifierKind> modifiers) {
		this.modifiers = modifiers;
	}

	@Override
	public boolean addModifier(ModifierKind modifier) {
		if (modifiers == CtElementImpl.<ModifierKind> EMPTY_SET()) {
			this.modifiers = EnumSet.noneOf(ModifierKind.class);
		}
		return modifiers.add(modifier);
	}

	@Override
	public boolean removeModifier(ModifierKind modifier) {
		return modifiers.remove(modifier);
	}

	@Override
	public void setVisibility(ModifierKind visibility) {
		if (modifiers == CtElementImpl.<ModifierKind> EMPTY_SET()) {
			this.modifiers = EnumSet.noneOf(ModifierKind.class);
		}
		getModifiers().remove(ModifierKind.PUBLIC);
		getModifiers().remove(ModifierKind.PROTECTED);
		getModifiers().remove(ModifierKind.PRIVATE);
		getModifiers().add(visibility);
	}

	@Override
	public ModifierKind getVisibility() {
		if (getModifiers().contains(ModifierKind.PUBLIC))
			return ModifierKind.PUBLIC;
		if (getModifiers().contains(ModifierKind.PROTECTED))
			return ModifierKind.PROTECTED;
		if (getModifiers().contains(ModifierKind.PRIVATE))
			return ModifierKind.PRIVATE;
		return null;
	}
	
	@Override
	public CtExecutable<?> getParent() {
		return (CtExecutable<?>) super.getParent();
	}
	
}
