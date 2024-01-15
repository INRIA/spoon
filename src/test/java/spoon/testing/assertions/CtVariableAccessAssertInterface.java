package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtVariableAccess;
interface CtVariableAccessAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtVariableAccess<?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> {
    default CtTypeReferenceAssertInterface<?, ?> getType() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getType());
    }

    default CtVariableReferenceAssertInterface<?, ?> getVariable() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getVariable());
    }
}