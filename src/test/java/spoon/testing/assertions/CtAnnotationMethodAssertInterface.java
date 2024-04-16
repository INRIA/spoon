package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtAnnotationMethod;
public interface CtAnnotationMethodAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAnnotationMethod<?>> extends SpoonAssert<A, W> , CtMethodAssertInterface<A, W> {
	default CtExpressionAssertInterface<?, ?> getDefaultExpression() {
		return SpoonAssertions.assertThat(actual().getDefaultExpression());
	}

	default CtReceiverParameterAssertInterface<?, ?> getReceiverParameter() {
		return SpoonAssertions.assertThat(actual().getReceiverParameter());
	}
}
