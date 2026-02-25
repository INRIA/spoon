package spoon.testing.assertions;
import java.util.List;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.filter.PotentialVariableDeclarationFunction;
public interface CtReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtReference> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
	default AbstractStringAssert<?> getSimpleName() {
		return Assertions.assertThat(actual().getSimpleName());
	}

	default CtElementAssert getDeclaration() {
		return new CtElementAssert(actual().getDeclaration());
	}

	/**
	 * Asserts that the {@link CtReferenceAssertInterface#actual()} reference resolves to the given potential declarations in that order.
	 * <p>
	 * The resolution in {@code CtLocalVariableReference#getDeclaration()} will choose the first declaration that matches.
	 * For testing the variable resolution ({@link PotentialVariableDeclarationFunction}) and noticing potential mistakes
	 * like duplicate variable declarations, it is useful to be able to assert what all potential declarations, including the ones
	 * that are hidden by other declarations resolve to.
	 *
	 * @param potentialDeclarations
	 * 		the potential declarations that the reference could resolve to, in the order they would be resolved
	 */
	default CtReferenceAssertInterface<? extends A, ? extends W> hasExactlyPotentialDeclarations(CtVariable<?>... potentialDeclarations) {
		// TODO: Is this method okay here? It is technically only necessary to test the getDeclaration() resolution of a LocalVariableReference,
		// but given that this is done in multiple test classes, I don't know where this should be placed?
		//
		// TODO: Open for name suggestions
		List<CtVariable<?>> declarations = actual().map(new PotentialVariableDeclarationFunction(actual().getSimpleName())).list();
		// By default, the containsExactly assertion checks with equals or compareTo.
		// A declaration `String i` and an unrelated `String i` are considered equal.
		// Given that the potential declarations will all have the same name it is important to check
		// that they are not only equal, but the same object through ==
		//
		// We are not sorting, so the else should be fine.
		Assertions.assertThat(declarations).as("Potential declarations of variable <%s>", actual()).usingElementComparator((a, b) -> a == b ? 0 : Integer.compare(System.identityHashCode(a), System.identityHashCode(b))).containsExactly(potentialDeclarations);
		return this;
	}
}
