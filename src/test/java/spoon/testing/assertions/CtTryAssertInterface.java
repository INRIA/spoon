package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtTry;
interface CtTryAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTry> extends CtBodyHolderAssertInterface<A, W> , SpoonAssert<A, W> , CtStatementAssertInterface<A, W> {
    default CtBlockAssertInterface<?, ?> getBody() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getBody());
    }

    default ListAssert<CtCatch> getCatchers() {
        return org.assertj.core.api.Assertions.assertThat(actual().getCatchers());
    }

    default CtBlockAssertInterface<?, ?> getFinalizer() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getFinalizer());
    }
}