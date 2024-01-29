package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCodeElement;
public class CtCodeElementAssert extends AbstractObjectAssert<CtCodeElementAssert, CtCodeElement> implements CtCodeElementAssertInterface<CtCodeElementAssert, CtCodeElement> {
	CtCodeElementAssert(CtCodeElement actual) {
		super(actual, CtCodeElementAssert.class);
	}

	@Override
	public CtCodeElementAssert self() {
		return this;
	}

	@Override
	public CtCodeElement actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
