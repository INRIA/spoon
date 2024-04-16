package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtConstructor;
public class CtConstructorAssert extends AbstractObjectAssert<CtConstructorAssert, CtConstructor<?>> implements CtConstructorAssertInterface<CtConstructorAssert, CtConstructor<?>> {
	CtConstructorAssert(CtConstructor<?> actual) {
		super(actual, CtConstructorAssert.class);
	}

	@Override
	public CtConstructorAssert self() {
		return this;
	}

	@Override
	public CtConstructor<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
