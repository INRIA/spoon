package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtExpression;
interface CtAbstractInvocationAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAbstractInvocation<?>> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
    default ListAssert<CtExpression<?>> getArguments() {
        return org.assertj.core.api.Assertions.assertThat(actual().getArguments());
    }

    default CtExecutableReferenceAssertInterface<?, ?> getExecutable() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getExecutable());
    }
}