/**
 * Copyright (C) 2006-2017 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 * <p>
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 * <p>
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.support.reflect;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.support.reflect.declaration.CtElementImpl;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static spoon.reflect.path.CtRole.MODIFIER;

public class CtModifierHandler implements Serializable {
	private static final long serialVersionUID = 1L;

	private Set<ModifierKind> modifiers = CtElementImpl.emptySet();

	private CtElement element;

	public CtModifierHandler(CtElement element) {
		this.element = element;
	}

	public Factory getFactory() {
		return element.getFactory();
	}

	public Set<ModifierKind> getModifiers() {
		return modifiers;
	}

	public boolean hasModifier(ModifierKind modifier) {
		return getModifiers().contains(modifier);
	}

	public CtModifierHandler setModifiers(Set<ModifierKind> modifiers) {
		if (modifiers.size() > 0) {
			getFactory().getEnvironment().getModelChangeListener().onSetDeleteAll(element, MODIFIER, this.modifiers, new HashSet<>(this.modifiers));
			this.modifiers.clear();
			for (ModifierKind modifier : modifiers) {
				addModifier(modifier);
			}
		}
		return this;
	}

	public CtModifierHandler addModifier(ModifierKind modifier) {
		if (modifiers == CtElementImpl.<ModifierKind>emptySet()) {
			this.modifiers = EnumSet.noneOf(ModifierKind.class);
		}
		getFactory().getEnvironment().getModelChangeListener().onSetAdd(element, MODIFIER, this.modifiers, modifier);
		modifiers.add(modifier);
		return this;
	}

	public boolean removeModifier(ModifierKind modifier) {
		if (modifiers == CtElementImpl.<ModifierKind>emptySet()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onSetDelete(element, MODIFIER, modifiers, modifier);
		return modifiers.remove(modifier);
	}

	public CtModifierHandler setVisibility(ModifierKind visibility) {
		if (modifiers == CtElementImpl.<ModifierKind>emptySet()) {
			this.modifiers = EnumSet.noneOf(ModifierKind.class);
		}
		if (hasModifier(visibility)) {
			return this;
		}
		if (isPublic()) {
			removeModifier(ModifierKind.PUBLIC);
		}
		if (isProtected()) {
			removeModifier(ModifierKind.PROTECTED);
		}
		if (isPrivate()) {
			removeModifier(ModifierKind.PRIVATE);
		}
		addModifier(visibility);
		return this;
	}

	public ModifierKind getVisibility() {
		if (isPublic()) {
			return ModifierKind.PUBLIC;
		}
		if (isProtected()) {
			return ModifierKind.PROTECTED;
		}
		if (isPrivate()) {
			return ModifierKind.PRIVATE;
		}
		return null;
	}

	public boolean isPublic() {
		return getModifiers().contains(ModifierKind.PUBLIC);
	}

	public boolean isProtected() {
		return getModifiers().contains(ModifierKind.PROTECTED);
	}

	public boolean isPrivate() {
		return getModifiers().contains(ModifierKind.PRIVATE);
	}

	@Override
	public int hashCode() {
		return getModifiers().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CtModifierHandler)) {
			return false;
		}
		final CtModifierHandler other = (CtModifierHandler) obj;
		if (getVisibility() == null) {
			if (other.getVisibility() != null) {
				return false;
			}
		} else if (other.getVisibility() == null) {
			return false;
		} else  if (!getVisibility().equals(other.getVisibility())) {
			return false;
		}
		if (getModifiers().size() != other.getModifiers().size()) {
			return false;
		}
		return getModifiers().containsAll(other.getModifiers());
	}
}
