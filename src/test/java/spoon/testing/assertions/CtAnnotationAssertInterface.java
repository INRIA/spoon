package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.MapAssert;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtAnnotation;
public interface CtAnnotationAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAnnotation<?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> , CtShadowableAssertInterface<A, W> {
	default CtTypeReferenceAssertInterface<?, ?> getAnnotationType() {
		return SpoonAssertions.assertThat(actual().getAnnotationType());
	}

	default MapAssert<String, CtExpression> getValues() {
		return Assertions.assertThat(actual().getValues());
	}
}
