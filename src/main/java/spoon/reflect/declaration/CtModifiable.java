/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.support.DerivedProperty;
import spoon.support.reflect.CtExtendedModifier;

import java.util.Set;

import static spoon.reflect.path.CtRole.EMODIFIER;
import static spoon.reflect.path.CtRole.MODIFIER;

/**
 * This interface defines an element that accepts modifiers.
 */
public interface CtModifiable extends CtElement {
	/**
	 * Returns the modifiers of this element, excluding annotations. Implicit
	 * modifiers, such as the {@code public} and {@code static} modifiers of
	 * interface members, are included.
	 *
	 * @return the modifiers of this declaration in undefined order; an empty
	 * set if there are none
	 */
	@PropertyGetter(role = MODIFIER)
	Set<ModifierKind> getModifiers();

	/**
	 * Tells if this element contains the given modifier.
	 *
	 * @param modifier
	 * 		to search
	 * @return {@code true} if this element contain the modifier
	 */
	boolean hasModifier(ModifierKind modifier);

	/**
	 * Sets the modifiers.
	 */
	@PropertySetter(role = MODIFIER)
	<T extends CtModifiable> T setModifiers(Set<ModifierKind> modifiers);

	/**
	 * add a modifier
	 *
	 * @param modifier
	 */
	@PropertySetter(role = MODIFIER)
	<T extends CtModifiable> T addModifier(ModifierKind modifier);

	/**
	 * remove a modifier
	 *
	 * @param modifier
	 */
	@PropertySetter(role = MODIFIER)
	<T extends CtModifiable> T removeModifier(ModifierKind modifier);

	/**
	 * Sets the visibility of this modifiable element (replaces old visibility).
	 */
	@PropertySetter(role = MODIFIER)
	<T extends CtModifiable> T setVisibility(ModifierKind visibility);

	/**
	 * Gets the visibility of this modifiable element.
	 */
	@DerivedProperty
	ModifierKind getVisibility();

	/**
	 * @return the set of extended modifiers (those incl. implicit).
	 */
	@PropertyGetter(role = EMODIFIER)
	Set<CtExtendedModifier> getExtendedModifiers();
	<T extends CtModifiable> T setExtendedModifiers(Set<CtExtendedModifier> extendedModifiers);

	/**
	 * Returns true if it contains a public modifier (see {@link #hasModifier(ModifierKind)})
	 */
	@DerivedProperty
	boolean isPublic();

	/**
	 * Returns true if it contains a final modifier (see {@link #hasModifier(ModifierKind)})
	 */
	@DerivedProperty
	boolean isFinal();

	/**
	 * Returns true if it contains a static modifier (see {@link #hasModifier(ModifierKind)})
	 */
	@DerivedProperty
	boolean isStatic();

	/**
	 * Returns true if it contains a protected modifier (see {@link #hasModifier(ModifierKind)})
	 */
	@DerivedProperty
	boolean isProtected();

	/**
	 * Returns true if it contains a private modifier (see {@link #hasModifier(ModifierKind)})
	 */
	@DerivedProperty
	boolean isPrivate();

	/**
	 * Returns true if it contains an abstract modifier (see {@link #hasModifier(ModifierKind)})
	 */
	@DerivedProperty
	boolean isAbstract();
}
