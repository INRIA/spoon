package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtBinaryOperator;
public class CtBinaryOperatorAssert extends AbstractObjectAssert<CtBinaryOperatorAssert, CtBinaryOperator<?>> implements CtBinaryOperatorAssertInterface<CtBinaryOperatorAssert, CtBinaryOperator<?>> {
	CtBinaryOperatorAssert(CtBinaryOperator<?> actual) {
		super(actual, CtBinaryOperatorAssert.class);
	}

	@Override
	public CtBinaryOperatorAssert self() {
		return this;
	}

	@Override
	public CtBinaryOperator<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
