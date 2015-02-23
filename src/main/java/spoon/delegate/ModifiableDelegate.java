package spoon.delegate;

import spoon.reflect.declaration.ModifierKind;

import java.util.Set;

public interface ModifiableDelegate {
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
	void setModifiers(Set<ModifierKind> modifiers);

	/**
	 * add a modifier
	 *
	 * @param modifier
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean addModifier(ModifierKind modifier);

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
	void setVisibility(ModifierKind visibility);

	/**
	 * Gets the visibility of this modifiable element.
	 */
	ModifierKind getVisibility();
}
