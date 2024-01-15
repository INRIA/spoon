package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import spoon.reflect.declaration.CtConstructor;
interface CtConstructorAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtConstructor<?>> extends SpoonAssert<A, W> , CtExecutableAssertInterface<A, W> , CtFormalTypeDeclarerAssertInterface<A, W> , CtShadowableAssertInterface<A, W> {
    default AbstractStringAssert<?> getSimpleName() {
        return org.assertj.core.api.Assertions.assertThat(actual().getSimpleName());
    }

    default AbstractBooleanAssert<?> isCompactConstructor() {
        return org.assertj.core.api.Assertions.assertThat(actual().isCompactConstructor());
    }
}