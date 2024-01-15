package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtConditional;
interface CtConditionalAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtConditional<?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> {
    default CtExpressionAssertInterface<?, ?> getCondition() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getCondition());
    }

    default CtExpressionAssertInterface<?, ?> getElseExpression() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getElseExpression());
    }

    default CtExpressionAssertInterface<?, ?> getThenExpression() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getThenExpression());
    }
}