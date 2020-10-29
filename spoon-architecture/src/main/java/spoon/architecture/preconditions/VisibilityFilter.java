package spoon.architecture.preconditions;

import java.util.function.Predicate;
import spoon.reflect.declaration.CtModifiable;

/**
 * This class defines multiple convenience predicates for easier visibility modifier filtering. No defined method is null safe. You can check even for modifiers an element can't have.
 */
public class VisibilityFilter {

	private VisibilityFilter() {

	}

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
