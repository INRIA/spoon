package fr.inria.gforge.spoon.architecture.preconditions;

import java.util.function.Predicate;
import spoon.reflect.declaration.CtModifiable;

public class VisibilityFilter {


	public static <T extends CtModifiable> Predicate<T> isPrivate() {
		return CtModifiable::isPrivate;
	}
	public static <T extends CtModifiable> Predicate<T> isDefault() {
		return (element) -> !(element.isPrivate() || element.isProtected() || element.isPublic());
	}
	public static <T extends CtModifiable> Predicate<T> isProtected() {
		return CtModifiable::isProtected;
	}
	public static <T extends CtModifiable> Predicate<T> isPublic() {
		return CtModifiable::isPublic;
	}
}
