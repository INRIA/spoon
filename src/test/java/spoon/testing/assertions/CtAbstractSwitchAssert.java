package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtAbstractSwitch;
public class CtAbstractSwitchAssert extends AbstractObjectAssert<CtAbstractSwitchAssert, CtAbstractSwitch<?>> implements CtAbstractSwitchAssertInterface<CtAbstractSwitchAssert, CtAbstractSwitch<?>> {
	CtAbstractSwitchAssert(CtAbstractSwitch<?> actual) {
		super(actual, CtAbstractSwitchAssert.class);
	}

	@Override
	public CtAbstractSwitchAssert self() {
		return this;
	}

	@Override
	public CtAbstractSwitch<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
