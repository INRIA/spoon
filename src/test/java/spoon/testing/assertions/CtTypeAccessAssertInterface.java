package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtTypeAccess;
interface CtTypeAccessAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTypeAccess<?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> {
    default CtTypeReferenceAssertInterface<?, ?> getAccessedType() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getAccessedType());
    }

    default AbstractBooleanAssert<?> isImplicit() {
        return org.assertj.core.api.Assertions.assertThat(actual().isImplicit());
    }
}