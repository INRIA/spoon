package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtTry;
public class CtTryAssert extends AbstractObjectAssert<CtTryAssert, CtTry> implements CtTryAssertInterface<CtTryAssert, CtTry> {
	CtTryAssert(CtTry actual) {
		super(actual, CtTryAssert.class);
	}

	@Override
	public CtTryAssert self() {
		return this;
	}

	@Override
	public CtTry actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
