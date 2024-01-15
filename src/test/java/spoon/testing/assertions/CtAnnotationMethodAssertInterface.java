package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtAnnotationMethod;
interface CtAnnotationMethodAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAnnotationMethod<?>> extends SpoonAssert<A, W> , CtMethodAssertInterface<A, W> {
    default CtExpressionAssertInterface<?, ?> getDefaultExpression() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getDefaultExpression());
    }
}