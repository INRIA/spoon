package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtField;
public class CtFieldAssert extends AbstractObjectAssert<CtFieldAssert, CtField<?>> implements CtFieldAssertInterface<CtFieldAssert, CtField<?>> {
	CtFieldAssert(CtField<?> actual) {
		super(actual, CtFieldAssert.class);
	}

	@Override
	public CtFieldAssert self() {
		return this;
	}

	@Override
	public CtField<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
