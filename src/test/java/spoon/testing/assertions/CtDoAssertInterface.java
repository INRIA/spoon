package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtDo;
interface CtDoAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtDo> extends SpoonAssert<A, W> , CtLoopAssertInterface<A, W> {
    default CtExpressionAssertInterface<?, ?> getLoopingExpression() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getLoopingExpression());
    }
}