package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtForEach;
interface CtForEachAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtForEach> extends SpoonAssert<A, W> , CtLoopAssertInterface<A, W> {
    default CtExpressionAssertInterface<?, ?> getExpression() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getExpression());
    }

    default CtLocalVariableAssertInterface<?, ?> getVariable() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getVariable());
    }
}