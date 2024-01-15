package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ObjectAssert;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.UnaryOperatorKind;
interface CtUnaryOperatorAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtUnaryOperator<?>> extends SpoonAssert<A, W> , CtStatementAssertInterface<A, W> , CtExpressionAssertInterface<A, W> {
    default ObjectAssert<UnaryOperatorKind> getKind() {
        return org.assertj.core.api.Assertions.assertThatObject(actual().getKind());
    }

    default CtExpressionAssertInterface<?, ?> getOperand() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getOperand());
    }
}