package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ListAssert;
import spoon.reflect.declaration.CtMultiTypedElement;
import spoon.reflect.reference.CtTypeReference;
interface CtMultiTypedElementAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtMultiTypedElement> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
    default ListAssert<CtTypeReference<?>> getMultiTypes() {
        return org.assertj.core.api.Assertions.assertThat(actual().getMultiTypes());
    }
}