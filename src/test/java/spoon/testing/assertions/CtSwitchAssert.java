package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtSwitch;
public class CtSwitchAssert extends AbstractObjectAssert<CtSwitchAssert, CtSwitch<?>> implements CtSwitchAssertInterface<CtSwitchAssert, CtSwitch<?>> {
	CtSwitchAssert(CtSwitch<?> actual) {
		super(actual, CtSwitchAssert.class);
	}

	@Override
	public CtSwitchAssert self() {
		return this;
	}

	@Override
	public CtSwitch<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
