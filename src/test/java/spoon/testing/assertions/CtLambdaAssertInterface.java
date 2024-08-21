package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtLambda;
public interface CtLambdaAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtLambda<?>> extends SpoonAssert<A, W> , CtExecutableAssertInterface<A, W> , CtExpressionAssertInterface<A, W> {
	default CtExpressionAssertInterface<?, ?> getExpression() {
		return SpoonAssertions.assertThat(actual().getExpression());
	}

	default CtReceiverParameterAssertInterface<?, ?> getReceiverParameter() {
		return SpoonAssertions.assertThat(actual().getReceiverParameter());
	}
}
