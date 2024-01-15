package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtAssert;
interface CtAssertAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAssert<?>> extends SpoonAssert<A, W> , CtStatementAssertInterface<A, W> {
    default CtExpressionAssertInterface<?, ?> getAssertExpression() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getAssertExpression());
    }

    default CtExpressionAssertInterface<?, ?> getExpression() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getExpression());
    }
}