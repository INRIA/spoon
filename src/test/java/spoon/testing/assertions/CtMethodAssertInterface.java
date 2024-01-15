package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtMethod;
interface CtMethodAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtMethod<?>> extends SpoonAssert<A, W> , CtExecutableAssertInterface<A, W> , CtFormalTypeDeclarerAssertInterface<A, W> , CtShadowableAssertInterface<A, W> {
    default AbstractBooleanAssert<?> isDefaultMethod() {
        return org.assertj.core.api.Assertions.assertThat(actual().isDefaultMethod());
    }
}