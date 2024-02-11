package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtConditional;
public class CtConditionalAssert extends AbstractObjectAssert<CtConditionalAssert, CtConditional<?>> implements CtConditionalAssertInterface<CtConditionalAssert, CtConditional<?>> {
	CtConditionalAssert(CtConditional<?> actual) {
		super(actual, CtConditionalAssert.class);
	}

	@Override
	public CtConditionalAssert self() {
		return this;
	}

	@Override
	public CtConditional<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
