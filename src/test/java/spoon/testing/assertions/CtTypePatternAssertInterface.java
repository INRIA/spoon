package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.reference.CtTypeReference;
interface CtTypePatternAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTypePattern> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> , CtPatternAssertInterface<A, W> {
    default ListAssert<CtTypeReference<?>> getTypeCasts() {
        return org.assertj.core.api.Assertions.assertThat(actual().getTypeCasts());
    }

    default CtLocalVariableAssertInterface<?, ?> getVariable() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getVariable());
    }
}