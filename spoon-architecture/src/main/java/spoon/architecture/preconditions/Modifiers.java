/**
 * SPDX-License-Identifier:  MIT
 */
package spoon.architecture.preconditions;

import java.util.function.Predicate;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.ModifierKind;

/**
 * This class defines multiple convenience predicates for easier modifier filtering. No defined method is null safe. You can check even for modifiers an element can't have.
 */
public class Modifiers {

	private Modifiers() {

	}

	public static <T extends CtModifiable> Predicate<T> isFinal() {
		return CtModifiable::isFinal;
	}
	public static <T extends CtModifiable> Predicate<T> isStatic() {
		return CtModifiable::isStatic;
	}
	public static <T extends CtModifiable> Predicate<T> isTransient() {
		return v -> v.hasModifier(ModifierKind.TRANSIENT);
	}
	public static <T extends CtModifiable> Predicate<T> isAbstract() {
		return CtModifiable::isAbstract;
	}
	public static <T extends CtModifiable> Predicate<T> isSynchronized() {
		return v -> v.hasModifier(ModifierKind.SYNCHRONIZED);
	}
}
