package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCasePattern;
public class CtCasePatternAssert extends AbstractObjectAssert<CtCasePatternAssert, CtCasePattern> implements CtCasePatternAssertInterface<CtCasePatternAssert, CtCasePattern> {
	CtCasePatternAssert(CtCasePattern actual) {
		super(actual, CtCasePatternAssert.class);
	}

	@Override
	public CtCasePatternAssert self() {
		return this;
	}

	@Override
	public CtCasePattern actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
