package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtContinue;
public class CtContinueAssert extends AbstractObjectAssert<CtContinueAssert, CtContinue> implements CtContinueAssertInterface<CtContinueAssert, CtContinue> {
	CtContinueAssert(CtContinue actual) {
		super(actual, CtContinueAssert.class);
	}

	@Override
	public CtContinueAssert self() {
		return this;
	}

	@Override
	public CtContinue actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
