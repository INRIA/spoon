package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtShadowable;
interface CtShadowableAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtShadowable> extends SpoonAssert<A, W> {
    default AbstractBooleanAssert<?> isShadow() {
        return org.assertj.core.api.Assertions.assertThat(actual().isShadow());
    }
}