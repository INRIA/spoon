package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtNamedElement;
public class CtNamedElementAssert extends AbstractObjectAssert<CtNamedElementAssert, CtNamedElement> implements CtNamedElementAssertInterface<CtNamedElementAssert, CtNamedElement> {
	CtNamedElementAssert(CtNamedElement actual) {
		super(actual, CtNamedElementAssert.class);
	}

	@Override
	public CtNamedElementAssert self() {
		return this;
	}

	@Override
	public CtNamedElement actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
