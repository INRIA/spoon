package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCatch;
interface CtCatchAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtCatch> extends CtBodyHolderAssertInterface<A, W> , SpoonAssert<A, W> , CtCodeElementAssertInterface<A, W> {
    default CtBlockAssertInterface<?, ?> getBody() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getBody());
    }

    default CtCatchVariableAssertInterface<?, ?> getParameter() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getParameter());
    }
}