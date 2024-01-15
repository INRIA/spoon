package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.reference.CtTypeReference;
interface CtInvocationAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtInvocation<?>> extends SpoonAssert<A, W> , CtActualTypeContainerAssertInterface<A, W> , CtStatementAssertInterface<A, W> , CtTargetedExpressionAssertInterface<A, W> , CtAbstractInvocationAssertInterface<A, W> {
    default ListAssert<CtTypeReference<?>> getActualTypeArguments() {
        return org.assertj.core.api.Assertions.assertThat(actual().getActualTypeArguments());
    }

    default CtTypeReferenceAssertInterface<?, ?> getType() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getType());
    }
}