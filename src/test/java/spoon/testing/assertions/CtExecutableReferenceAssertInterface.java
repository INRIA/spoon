package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ListAssert;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
interface CtExecutableReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtExecutableReference<?>> extends SpoonAssert<A, W> , CtActualTypeContainerAssertInterface<A, W> , CtReferenceAssertInterface<A, W> {
    default CtTypeReferenceAssertInterface<?, ?> getDeclaringType() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getDeclaringType());
    }

    default ListAssert<CtTypeReference<?>> getParameters() {
        return org.assertj.core.api.Assertions.assertThat(actual().getParameters());
    }

    default CtTypeReferenceAssertInterface<?, ?> getType() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getType());
    }

    default AbstractBooleanAssert<?> isStatic() {
        return org.assertj.core.api.Assertions.assertThat(actual().isStatic());
    }
}