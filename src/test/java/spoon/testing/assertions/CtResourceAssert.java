package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtResource;
public class CtResourceAssert extends AbstractObjectAssert<CtResourceAssert, CtResource<?>> implements CtResourceAssertInterface<CtResourceAssert, CtResource<?>> {
	CtResourceAssert(CtResource<?> actual) {
		super(actual, CtResourceAssert.class);
	}

	@Override
	public CtResourceAssert self() {
		return this;
	}

	@Override
	public CtResource<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
