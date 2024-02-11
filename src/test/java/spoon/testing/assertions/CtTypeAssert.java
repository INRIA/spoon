package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtType;
public class CtTypeAssert extends AbstractObjectAssert<CtTypeAssert, CtType<?>> implements CtTypeAssertInterface<CtTypeAssert, CtType<?>> {
	CtTypeAssert(CtType<?> actual) {
		super(actual, CtTypeAssert.class);
	}

	@Override
	public CtTypeAssert self() {
		return this;
	}

	@Override
	public CtType<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
