package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtWildcardReference;
interface CtWildcardReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtWildcardReference> extends SpoonAssert<A, W> , CtTypeParameterReferenceAssertInterface<A, W> {
    default CtTypeReferenceAssertInterface<?, ?> getBoundingType() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getBoundingType());
    }

    default AbstractBooleanAssert<?> isUpper() {
        return org.assertj.core.api.Assertions.assertThat(actual().isUpper());
    }
}