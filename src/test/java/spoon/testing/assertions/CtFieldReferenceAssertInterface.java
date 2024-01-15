package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtFieldReference;
interface CtFieldReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtFieldReference<?>> extends CtVariableReferenceAssertInterface<A, W> , SpoonAssert<A, W> {
    default CtTypeReferenceAssertInterface<?, ?> getDeclaringType() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getDeclaringType());
    }

    default AbstractBooleanAssert<?> isFinal() {
        return org.assertj.core.api.Assertions.assertThat(actual().isFinal());
    }

    default AbstractBooleanAssert<?> isStatic() {
        return org.assertj.core.api.Assertions.assertThat(actual().isStatic());
    }
}