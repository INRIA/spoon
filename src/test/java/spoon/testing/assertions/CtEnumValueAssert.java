package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtEnumValue;
public class CtEnumValueAssert extends AbstractObjectAssert<CtEnumValueAssert, CtEnumValue<?>> implements CtEnumValueAssertInterface<CtEnumValueAssert, CtEnumValue<?>> {
	CtEnumValueAssert(CtEnumValue<?> actual) {
		super(actual, CtEnumValueAssert.class);
	}

	@Override
	public CtEnumValueAssert self() {
		return this;
	}

	@Override
	public CtEnumValue<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
