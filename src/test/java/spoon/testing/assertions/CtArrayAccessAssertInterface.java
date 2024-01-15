package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtArrayAccess;
interface CtArrayAccessAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtArrayAccess<?, ?>> extends SpoonAssert<A, W> , CtTargetedExpressionAssertInterface<A, W> {
    default CtExpressionAssertInterface<?, ?> getIndexExpression() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getIndexExpression());
    }
}