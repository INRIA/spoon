package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtLambda;
public class CtLambdaAssert extends AbstractObjectAssert<CtLambdaAssert, CtLambda<?>> implements CtLambdaAssertInterface<CtLambdaAssert, CtLambda<?>> {
	CtLambdaAssert(CtLambda<?> actual) {
		super(actual, CtLambdaAssert.class);
	}

	@Override
	public CtLambdaAssert self() {
		return this;
	}

	@Override
	public CtLambda<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
