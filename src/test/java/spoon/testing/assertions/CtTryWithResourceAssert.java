package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtTryWithResource;
public class CtTryWithResourceAssert extends AbstractObjectAssert<CtTryWithResourceAssert, CtTryWithResource> implements CtTryWithResourceAssertInterface<CtTryWithResourceAssert, CtTryWithResource> {
	CtTryWithResourceAssert(CtTryWithResource actual) {
		super(actual, CtTryWithResourceAssert.class);
	}

	@Override
	public CtTryWithResourceAssert self() {
		return this;
	}

	@Override
	public CtTryWithResource actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
