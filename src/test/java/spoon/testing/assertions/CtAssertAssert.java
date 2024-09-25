package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtAssert;
public class CtAssertAssert extends AbstractObjectAssert<CtAssertAssert, CtAssert<?>> implements CtAssertAssertInterface<CtAssertAssert, CtAssert<?>> {
	CtAssertAssert(CtAssert<?> actual) {
		super(actual, CtAssertAssert.class);
	}

	@Override
	public CtAssertAssert self() {
		return this;
	}

	@Override
	public CtAssert<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
