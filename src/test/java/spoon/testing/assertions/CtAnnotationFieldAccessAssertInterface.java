package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtAnnotationFieldAccess;
interface CtAnnotationFieldAccessAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAnnotationFieldAccess<?>> extends CtVariableReadAssertInterface<A, W> , SpoonAssert<A, W> , CtTargetedExpressionAssertInterface<A, W> {
    default CtFieldReferenceAssertInterface<?, ?> getVariable() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getVariable());
    }
}