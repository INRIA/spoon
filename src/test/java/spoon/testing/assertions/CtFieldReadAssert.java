package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtFieldRead;
public class CtFieldReadAssert extends AbstractObjectAssert<CtFieldReadAssert, CtFieldRead<?>> implements CtFieldReadAssertInterface<CtFieldReadAssert, CtFieldRead<?>> {
	CtFieldReadAssert(CtFieldRead<?> actual) {
		super(actual, CtFieldReadAssert.class);
	}

	@Override
	public CtFieldReadAssert self() {
		return this;
	}

	@Override
	public CtFieldRead<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
