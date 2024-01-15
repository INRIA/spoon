package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import spoon.reflect.reference.CtArrayTypeReference;
interface CtArrayTypeReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtArrayTypeReference<?>> extends CtTypeReferenceAssertInterface<A, W> , SpoonAssert<A, W> {
    default CtTypeReferenceAssertInterface<?, ?> getComponentType() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getComponentType());
    }

    default AbstractStringAssert<?> getSimpleName() {
        return org.assertj.core.api.Assertions.assertThat(actual().getSimpleName());
    }
}