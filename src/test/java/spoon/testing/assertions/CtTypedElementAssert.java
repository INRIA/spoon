package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtTypedElement;
public class CtTypedElementAssert extends AbstractObjectAssert<CtTypedElementAssert, CtTypedElement<?>> implements CtTypedElementAssertInterface<CtTypedElementAssert, CtTypedElement<?>> {
	CtTypedElementAssert(CtTypedElement<?> actual) {
		super(actual, CtTypedElementAssert.class);
	}

	@Override
	public CtTypedElementAssert self() {
		return this;
	}

	@Override
	public CtTypedElement<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
