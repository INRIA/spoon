package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtModifiable;
public class CtModifiableAssert extends AbstractObjectAssert<CtModifiableAssert, CtModifiable> implements CtModifiableAssertInterface<CtModifiableAssert, CtModifiable> {
	CtModifiableAssert(CtModifiable actual) {
		super(actual, CtModifiableAssert.class);
	}

	@Override
	public CtModifiableAssert self() {
		return this;
	}

	@Override
	public CtModifiable actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
