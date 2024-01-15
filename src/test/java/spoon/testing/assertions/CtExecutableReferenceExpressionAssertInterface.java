package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtExecutableReferenceExpression;
interface CtExecutableReferenceExpressionAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtExecutableReferenceExpression<?, ?>> extends SpoonAssert<A, W> , CtTargetedExpressionAssertInterface<A, W> {
    default CtExecutableReferenceAssertInterface<?, ?> getExecutable() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getExecutable());
    }
}