package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.CtNamedElement;
public interface CtNamedElementAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtNamedElement> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
	default AbstractStringAssert<?> getSimpleName() {
		return Assertions.assertThat(actual().getSimpleName());
	}
}
