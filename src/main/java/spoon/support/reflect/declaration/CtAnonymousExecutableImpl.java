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

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.CtVisitor;

public class CtAnonymousExecutableImpl extends CtElementImpl implements
		CtAnonymousExecutable {
	private static final long serialVersionUID = 1L;

	CtBlock<?> body;

	Set<ModifierKind> modifiers = new TreeSet<ModifierKind>();

	public void accept(CtVisitor visitor) {
		visitor.visitCtAnonymousExecutable(this);
	}

	public CtBlock<?> getBody() {
		return body;
	}

	public Set<ModifierKind> getModifiers() {
		return modifiers;
	}

	public CtClass<?> getOwnerClass() {
		return (CtClass<?>) parent;
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
		return modifiers.contains(modifier);
	}

	public void setBody(CtBlock<?> block) {
		body = block;
	}

	public void setModifiers(Set<ModifierKind> modifiers) {
		this.modifiers = modifiers;
	}

	public void setVisibility(ModifierKind visibility) {
		getModifiers().remove(ModifierKind.PUBLIC);
		getModifiers().remove(ModifierKind.PROTECTED);
		getModifiers().remove(ModifierKind.PRIVATE);
		getModifiers().add(visibility);
	}

}
