/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.support.reflect.declaration.CtElementImpl;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static spoon.reflect.path.CtRole.MODIFIER;

public class CtModifierHandler implements Serializable {
	private static final long serialVersionUID = 1L;

	private Set<CtExtendedModifier> modifiers = CtElementImpl.emptySet();

	private CtElement element;

	public CtModifierHandler(CtElement element) {
		this.element = element;
	}

	public Factory getFactory() {
		return element.getFactory();
	}

	public Set<CtExtendedModifier> getExtendedModifiers() {
		return Collections.unmodifiableSet(this.modifiers);
	}

	public CtModifierHandler setExtendedModifiers(Set<CtExtendedModifier> extendedModifiers) {
		if (extendedModifiers != null && !extendedModifiers.isEmpty()) {
			getFactory().getEnvironment().getModelChangeListener().onSetDeleteAll(element, MODIFIER, this.modifiers, new HashSet<>(this.modifiers));
			if (this.modifiers == CtElementImpl.<CtExtendedModifier>emptySet()) {
				this.modifiers = new HashSet<>();
			} else {
				this.modifiers.clear();
			}
			for (CtExtendedModifier extendedModifier : extendedModifiers) {
				getFactory().getEnvironment().getModelChangeListener().onSetAdd(element, MODIFIER, this.modifiers, extendedModifier.getKind());
				this.modifiers.add(extendedModifier);
			}
		}
		return this;
	}

	public Set<ModifierKind> getModifiers() {
		return modifiers.stream().map(CtExtendedModifier::getKind).collect(Collectors.toSet());
	}

	public boolean hasModifier(ModifierKind modifier) {
		return getModifiers().contains(modifier);
	}

	public CtModifierHandler setModifiers(Set<ModifierKind> modifiers) {
		if (modifiers == null) {
			modifiers = Collections.emptySet();
		}
			getFactory().getEnvironment().getModelChangeListener().onSetDeleteAll(element, MODIFIER, this.modifiers, new HashSet<>(this.modifiers));
			this.modifiers.clear();
			for (ModifierKind modifier : modifiers) {
				addModifier(modifier);
			}
		return this;
	}

	public CtModifierHandler addModifier(ModifierKind modifier) {
		if (this.modifiers == CtElementImpl.<CtExtendedModifier>emptySet()) {
			this.modifiers = new HashSet<>();
		}
		getFactory().getEnvironment().getModelChangeListener().onSetAdd(element, MODIFIER, this.modifiers, modifier);
		// we always add explicit modifiers, then we have to remove first implicit one
		modifiers.remove(new CtExtendedModifier(modifier, true));
		modifiers.add(new CtExtendedModifier(modifier));
		return this;
	}

	public CtModifierHandler removeModifier(ModifierKind modifier) {
		if (this.modifiers == CtElementImpl.<CtExtendedModifier>emptySet()) {
			return this;
		}
		getFactory().getEnvironment().getModelChangeListener().onSetDelete(element, MODIFIER, modifiers, modifier);
		// we want to remove implicit OR explicit modifier
		modifiers.remove(new CtExtendedModifier(modifier));
		modifiers.remove(new CtExtendedModifier(modifier, true));
		return this;
	}

	public CtModifierHandler setVisibility(ModifierKind visibility) {
		if (visibility != ModifierKind.PUBLIC && visibility != ModifierKind.PROTECTED && visibility != ModifierKind.PRIVATE) {
			throw new SpoonException("setVisibility could only be called with a private, public or protected argument value. Given argument: " + visibility);
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

	public boolean isAbstract() {
		return getModifiers().contains(ModifierKind.ABSTRACT);
	}

	public boolean isStatic() {
		return getModifiers().contains(ModifierKind.STATIC);
	}

	public boolean isFinal() {
		return getModifiers().contains(ModifierKind.FINAL);
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
