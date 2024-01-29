package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtTypePattern;
public class CtTypePatternAssert extends AbstractObjectAssert<CtTypePatternAssert, CtTypePattern> implements CtTypePatternAssertInterface<CtTypePatternAssert, CtTypePattern> {
	CtTypePatternAssert(CtTypePattern actual) {
		super(actual, CtTypePatternAssert.class);
	}

	@Override
	public CtTypePatternAssert self() {
		return this;
	}

	@Override
	public CtTypePattern actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
