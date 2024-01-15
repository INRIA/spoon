package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtLocalVariable;
interface CtLocalVariableAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtLocalVariable<?>> extends CtVariableAssertInterface<A, W> , SpoonAssert<A, W> , CtStatementAssertInterface<A, W> , CtRHSReceiverAssertInterface<A, W> , CtResourceAssertInterface<A, W> {
    default AbstractBooleanAssert<?> isInferred() {
        return org.assertj.core.api.Assertions.assertThat(actual().isInferred());
    }
}