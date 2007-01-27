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

import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtReference;

public abstract class CtNamedElementImpl extends CtElementImpl implements
		CtNamedElement {

	Set<ModifierKind> modifiers = new TreeSet<ModifierKind>();

	String simpleName;

	public Set<ModifierKind> getModifiers() {
		return modifiers;
	}

	public CtReference getReference() {
		return null;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public ModifierKind getVisibility() {
		if (getModifiers().contains(ModifierKind.PUBLIC))
			return ModifierKind.PUBLIC;
		if (getModifiers().contains(ModifierKind.PROTECTED))
			return ModifierKind.PROTECTED;
		if (getModifiers().contains(ModifierKind.PRIVATE))
			return ModifierKind.PRIVATE;
		return null;
	}

	public boolean hasModifier(ModifierKind modifier) {
		return getModifiers().contains(modifier);
	}

	public void setModifiers(Set<ModifierKind> modifiers) {
		this.modifiers = modifiers;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public void setVisibility(ModifierKind visibility) {
		getModifiers().remove(ModifierKind.PUBLIC);
		getModifiers().remove(ModifierKind.PROTECTED);
		getModifiers().remove(ModifierKind.PRIVATE);
		getModifiers().add(visibility);
	}

}
