package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtIf;
public class CtIfAssert extends AbstractObjectAssert<CtIfAssert, CtIf> implements CtIfAssertInterface<CtIfAssert, CtIf> {
	CtIfAssert(CtIf actual) {
		super(actual, CtIfAssert.class);
	}

	@Override
	public CtIfAssert self() {
		return this;
	}

	@Override
	public CtIf actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
