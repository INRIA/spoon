package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtUnaryOperator;
public class CtUnaryOperatorAssert extends AbstractObjectAssert<CtUnaryOperatorAssert, CtUnaryOperator<?>> implements CtUnaryOperatorAssertInterface<CtUnaryOperatorAssert, CtUnaryOperator<?>> {
	CtUnaryOperatorAssert(CtUnaryOperator<?> actual) {
		super(actual, CtUnaryOperatorAssert.class);
	}

	@Override
	public CtUnaryOperatorAssert self() {
		return this;
	}

	@Override
	public CtUnaryOperator<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
