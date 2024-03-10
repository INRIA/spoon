package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtPattern;
public class CtPatternAssert extends AbstractObjectAssert<CtPatternAssert, CtPattern> implements CtPatternAssertInterface<CtPatternAssert, CtPattern> {
	CtPatternAssert(CtPattern actual) {
		super(actual, CtPatternAssert.class);
	}

	@Override
	public CtPatternAssert self() {
		return this;
	}

	@Override
	public CtPattern actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
