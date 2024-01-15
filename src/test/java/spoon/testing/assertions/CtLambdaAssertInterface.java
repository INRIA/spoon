package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtLambda;
interface CtLambdaAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtLambda<?>> extends SpoonAssert<A, W> , CtExecutableAssertInterface<A, W> , CtExpressionAssertInterface<A, W> {
    default CtExpressionAssertInterface<?, ?> getExpression() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getExpression());
    }
}