package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtAssignment;
interface CtAssignmentAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAssignment<?, ?>> extends SpoonAssert<A, W> , CtStatementAssertInterface<A, W> , CtRHSReceiverAssertInterface<A, W> , CtExpressionAssertInterface<A, W> {
    default CtExpressionAssertInterface<?, ?> getAssigned() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getAssigned());
    }
}