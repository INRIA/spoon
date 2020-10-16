package fr.inria.gforge.spoon.architecture.preconditions;

import java.util.function.Predicate;
import spoon.reflect.declaration.CtModifiable;

public enum Visibility implements Predicate<CtModifiable> {
	PRIVATE(CtModifiable::isPrivate),
	DEFAULT((element) -> !(element.isPrivate() || element.isProtected() || element.isPublic())),
	PROTECTED(CtModifiable::isProtected),
	PUBLIC(CtModifiable::isPublic);

	private Predicate<CtModifiable> visibilityCheck;
	Visibility(Predicate<CtModifiable> visibilityCheck) {
		this.visibilityCheck = visibilityCheck;
	}
	@Override
	public boolean test(CtModifiable t) {
		return visibilityCheck.test(t);
	}
}
