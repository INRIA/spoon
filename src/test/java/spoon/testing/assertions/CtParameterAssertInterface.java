package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtParameter;
interface CtParameterAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtParameter<?>> extends CtVariableAssertInterface<A, W> , SpoonAssert<A, W> , CtShadowableAssertInterface<A, W> {
    default AbstractBooleanAssert<?> isInferred() {
        return org.assertj.core.api.Assertions.assertThat(actual().isInferred());
    }

    default AbstractBooleanAssert<?> isVarArgs() {
        return org.assertj.core.api.Assertions.assertThat(actual().isVarArgs());
    }
}