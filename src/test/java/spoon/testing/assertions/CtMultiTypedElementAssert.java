package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtMultiTypedElement;
public class CtMultiTypedElementAssert extends AbstractObjectAssert<CtMultiTypedElementAssert, CtMultiTypedElement> implements CtMultiTypedElementAssertInterface<CtMultiTypedElementAssert, CtMultiTypedElement> {
	CtMultiTypedElementAssert(CtMultiTypedElement actual) {
		super(actual, CtMultiTypedElementAssert.class);
	}

	@Override
	public CtMultiTypedElementAssert self() {
		return this;
	}

	@Override
	public CtMultiTypedElement actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
