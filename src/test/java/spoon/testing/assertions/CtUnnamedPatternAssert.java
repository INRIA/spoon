package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtUnnamedPattern;
public class CtUnnamedPatternAssert extends AbstractObjectAssert<CtUnnamedPatternAssert, CtUnnamedPattern> implements CtUnnamedPatternAssertInterface<CtUnnamedPatternAssert, CtUnnamedPattern> {
	CtUnnamedPatternAssert(CtUnnamedPattern actual) {
		super(actual, CtUnnamedPatternAssert.class);
	}

	@Override
	public CtUnnamedPatternAssert self() {
		return this;
	}

	@Override
	public CtUnnamedPattern actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
