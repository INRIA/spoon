package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtFieldAccess;
interface CtFieldAccessAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtFieldAccess<?>> extends CtVariableAccessAssertInterface<A, W> , SpoonAssert<A, W> , CtTargetedExpressionAssertInterface<A, W> {
    default CtFieldReferenceAssertInterface<?, ?> getVariable() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getVariable());
    }
}