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

package spoon.reflect.declaration;

import java.util.Set;

import spoon.processing.FactoryAccessor;

/**
 * This interface defines an element that accepts modifiers.
 */
public interface CtModifiable extends CtElement, FactoryAccessor {
	/**
	 * Returns the modifiers of this element, excluding annotations. Implicit
	 * modifiers, such as the {@code public} and {@code static} modifiers of
	 * interface members, are included.
	 * 
	 * @return the modifiers of this declaration in undefined order; an empty
	 *         set if there are none
	 */
	Set<ModifierKind> getModifiers();

	/**
	 * Tells if this element contains the given modifier.
	 * 
	 * @param modifier
	 *            to search
	 * @return {@code true} if this element contain the modifier
	 */
	boolean hasModifier(ModifierKind modifier);

	/**
	 * Sets the modifiers.
	 */
	void setModifiers(Set<ModifierKind> modifiers);

	/**
	 * Sets the visibility of this modifiable element (replaces old visibility).
	 */
	void setVisibility(ModifierKind visibility);

	/**
	 * Gets the visibility of this modifiable element.
	 */
	ModifierKind getVisibility();
}
