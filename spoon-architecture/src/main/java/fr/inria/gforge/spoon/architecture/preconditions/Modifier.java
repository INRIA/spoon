package fr.inria.gforge.spoon.architecture.preconditions;

import java.util.function.Predicate;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.ModifierKind;

public enum Modifier implements Predicate<CtModifiable> {
	FINAL(CtModifiable::isFinal),
	STATIC(CtModifiable::isStatic),
	TRANSIENT(v -> v.hasModifier(ModifierKind.TRANSIENT)),
	ABSTRACT(CtModifiable::isAbstract),
	SYNCHRONIZED(v -> v.hasModifier(ModifierKind.SYNCHRONIZED));

	private Predicate<CtModifiable> modifierCheck;
	Modifier(Predicate<CtModifiable> modifierCheck) {
		this.modifierCheck = modifierCheck;
	}
	@Override
	public boolean test(CtModifiable t) {
		return modifierCheck.test(t);
	}
}
