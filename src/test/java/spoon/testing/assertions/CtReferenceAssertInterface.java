package spoon.testing.assertions;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtFieldReference;
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
		List<CtVariable<?>> declarations;
		if (actual() instanceof CtFieldReference<?> ctFieldReference) {
			// If necessary, this can be extended to resolve hidden variables from super types as well
			declarations = List.of(ctFieldReference.getFieldDeclaration());
		} else {
			declarations = actual().map(new PotentialVariableDeclarationFunction(actual().getSimpleName())).list();
		}
		// By default, the containsExactly assertion checks with equals or compareTo.
		// A declaration `String i` and an unrelated `String i` are considered equal.
		// Given that the potential declarations will all have the same name it is important to check
		// that they are not only equal, but the same object through ==.
		//
		// The code expects a comparator, so we are using the identity hash code (= pointer value) for the comparison.
		Assertions.assertThat(declarations).as("Potential declarations of variable <%s>", actual()).usingElementComparator(Comparator.comparing(System::identityHashCode)).containsExactly(potentialDeclarations);
		return this;
	}
}
