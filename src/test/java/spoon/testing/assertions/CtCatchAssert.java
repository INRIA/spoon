package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCatch;
public class CtCatchAssert extends AbstractObjectAssert<CtCatchAssert, CtCatch> implements CtCatchAssertInterface<CtCatchAssert, CtCatch> {
	CtCatchAssert(CtCatch actual) {
		super(actual, CtCatchAssert.class);
	}

	@Override
	public CtCatchAssert self() {
		return this;
	}

	@Override
	public CtCatch actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
