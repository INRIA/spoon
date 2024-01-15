package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewArray;
interface CtNewArrayAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtNewArray<?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> {
    default ListAssert<CtExpression<Integer>> getDimensionExpressions() {
        return org.assertj.core.api.Assertions.assertThat(actual().getDimensionExpressions());
    }

    default ListAssert<CtExpression<?>> getElements() {
        return org.assertj.core.api.Assertions.assertThat(actual().getElements());
    }
}