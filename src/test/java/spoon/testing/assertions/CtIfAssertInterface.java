package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtIf;
interface CtIfAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtIf> extends SpoonAssert<A, W> , CtStatementAssertInterface<A, W> {
    default CtExpressionAssertInterface<?, ?> getCondition() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getCondition());
    }

    default CtStatementAssertInterface<?, ?> getElseStatement() {
        return spoon.testing.assertions.SpoonAssertions.assertThat((spoon.reflect.code.CtStatement) actual().getElseStatement());
    }

    default CtStatementAssertInterface<?, ?> getThenStatement() {
        return spoon.testing.assertions.SpoonAssertions.assertThat((spoon.reflect.code.CtStatement) actual().getThenStatement());
    }
}