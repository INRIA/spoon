package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.MapAssert;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtAnnotation;
interface CtAnnotationAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAnnotation<?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> , CtShadowableAssertInterface<A, W> {
    default CtTypeReferenceAssertInterface<?, ?> getAnnotationType() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getAnnotationType());
    }

    default MapAssert<String, CtExpression> getValues() {
        return org.assertj.core.api.Assertions.assertThat(actual().getValues());
    }
}