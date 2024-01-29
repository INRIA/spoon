package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtConstructorCall;
public class CtConstructorCallAssert extends AbstractObjectAssert<CtConstructorCallAssert, CtConstructorCall<?>> implements CtConstructorCallAssertInterface<CtConstructorCallAssert, CtConstructorCall<?>> {
	CtConstructorCallAssert(CtConstructorCall<?> actual) {
		super(actual, CtConstructorCallAssert.class);
	}

	@Override
	public CtConstructorCallAssert self() {
		return this;
	}

	@Override
	public CtConstructorCall<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
