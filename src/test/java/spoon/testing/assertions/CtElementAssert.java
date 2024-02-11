package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtElement;
public class CtElementAssert extends AbstractObjectAssert<CtElementAssert, CtElement> implements CtElementAssertInterface<CtElementAssert, CtElement> {
	CtElementAssert(CtElement actual) {
		super(actual, CtElementAssert.class);
	}

	@Override
	public CtElementAssert self() {
		return this;
	}

	@Override
	public CtElement actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
