package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtProvidedService;
public class CtProvidedServiceAssert extends AbstractObjectAssert<CtProvidedServiceAssert, CtProvidedService> implements CtProvidedServiceAssertInterface<CtProvidedServiceAssert, CtProvidedService> {
	CtProvidedServiceAssert(CtProvidedService actual) {
		super(actual, CtProvidedServiceAssert.class);
	}

	@Override
	public CtProvidedServiceAssert self() {
		return this;
	}

	@Override
	public CtProvidedService actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
