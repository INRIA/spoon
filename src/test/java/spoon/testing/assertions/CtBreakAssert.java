package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtBreak;
public class CtBreakAssert extends AbstractObjectAssert<CtBreakAssert, CtBreak> implements CtBreakAssertInterface<CtBreakAssert, CtBreak> {
	CtBreakAssert(CtBreak actual) {
		super(actual, CtBreakAssert.class);
	}

	@Override
	public CtBreakAssert self() {
		return this;
	}

	@Override
	public CtBreak actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
