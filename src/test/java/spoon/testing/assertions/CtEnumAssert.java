package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtEnum;
public class CtEnumAssert extends AbstractObjectAssert<CtEnumAssert, CtEnum<?>> implements CtEnumAssertInterface<CtEnumAssert, CtEnum<?>> {
	CtEnumAssert(CtEnum<?> actual) {
		super(actual, CtEnumAssert.class);
	}

	@Override
	public CtEnumAssert self() {
		return this;
	}

	@Override
	public CtEnum<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
