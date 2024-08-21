package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtMethod;
public class CtMethodAssert extends AbstractObjectAssert<CtMethodAssert, CtMethod<?>> implements CtMethodAssertInterface<CtMethodAssert, CtMethod<?>> {
	CtMethodAssert(CtMethod<?> actual) {
		super(actual, CtMethodAssert.class);
	}

	@Override
	public CtMethodAssert self() {
		return this;
	}

	@Override
	public CtMethod<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
