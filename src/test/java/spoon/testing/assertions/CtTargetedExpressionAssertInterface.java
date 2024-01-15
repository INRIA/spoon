package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtTargetedExpression;
interface CtTargetedExpressionAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTargetedExpression<?, ?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> {
    default CtExpressionAssertInterface<?, ?> getTarget() {
        return spoon.testing.assertions.SpoonAssertions.assertThat((spoon.reflect.code.CtExpression<?>) actual().getTarget());
    }
}