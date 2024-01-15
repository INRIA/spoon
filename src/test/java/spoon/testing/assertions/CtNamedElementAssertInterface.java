package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import spoon.reflect.declaration.CtNamedElement;
interface CtNamedElementAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtNamedElement> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
    default AbstractStringAssert<?> getSimpleName() {
        return org.assertj.core.api.Assertions.assertThat(actual().getSimpleName());
    }
}