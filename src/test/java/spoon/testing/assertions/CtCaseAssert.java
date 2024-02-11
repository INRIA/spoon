package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCase;
public class CtCaseAssert extends AbstractObjectAssert<CtCaseAssert, CtCase<?>> implements CtCaseAssertInterface<CtCaseAssert, CtCase<?>> {
	CtCaseAssert(CtCase<?> actual) {
		super(actual, CtCaseAssert.class);
	}

	@Override
	public CtCaseAssert self() {
		return this;
	}

	@Override
	public CtCase<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
