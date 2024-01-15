package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtStatement;
interface CtForAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtFor> extends SpoonAssert<A, W> , CtLoopAssertInterface<A, W> {
    default CtExpressionAssertInterface<?, ?> getExpression() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getExpression());
    }

    default ListAssert<CtStatement> getForInit() {
        return org.assertj.core.api.Assertions.assertThat(actual().getForInit());
    }

    default ListAssert<CtStatement> getForUpdate() {
        return org.assertj.core.api.Assertions.assertThat(actual().getForUpdate());
    }
}