package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtThrow;
public class CtThrowAssert extends AbstractObjectAssert<CtThrowAssert, CtThrow> implements CtThrowAssertInterface<CtThrowAssert, CtThrow> {
	CtThrowAssert(CtThrow actual) {
		super(actual, CtThrowAssert.class);
	}

	@Override
	public CtThrowAssert self() {
		return this;
	}

	@Override
	public CtThrow actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
