package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtUsedService;
public class CtUsedServiceAssert extends AbstractObjectAssert<CtUsedServiceAssert, CtUsedService> implements CtUsedServiceAssertInterface<CtUsedServiceAssert, CtUsedService> {
	CtUsedServiceAssert(CtUsedService actual) {
		super(actual, CtUsedServiceAssert.class);
	}

	@Override
	public CtUsedServiceAssert self() {
		return this;
	}

	@Override
	public CtUsedService actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
