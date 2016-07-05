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
package spoon.reflect.declaration;

import java.util.Set;

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
	<T extends CtModifiable> T setModifiers(Set<ModifierKind> modifiers);

	/**
	 * add a modifier
	 *
	 * @param modifier
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<T extends CtModifiable> T addModifier(ModifierKind modifier);

	/**
	 * remove a modifier
	 *
	 * @param modifier
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean removeModifier(ModifierKind modifier);

	/**
	 * Sets the visibility of this modifiable element (replaces old visibility).
	 */
	<T extends CtModifiable> T setVisibility(ModifierKind visibility);

	/**
	 * Gets the visibility of this modifiable element.
	 */
	ModifierKind getVisibility();
}
