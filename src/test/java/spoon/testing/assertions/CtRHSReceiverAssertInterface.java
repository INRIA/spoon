package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtRHSReceiver;
interface CtRHSReceiverAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtRHSReceiver<?>> extends SpoonAssert<A, W> {
    default CtExpressionAssertInterface<?, ?> getAssignment() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getAssignment());
    }
}