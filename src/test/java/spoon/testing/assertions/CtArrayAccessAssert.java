package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtArrayAccess;
public class CtArrayAccessAssert extends AbstractObjectAssert<CtArrayAccessAssert, CtArrayAccess<?, ?>> implements CtArrayAccessAssertInterface<CtArrayAccessAssert, CtArrayAccess<?, ?>> {
	CtArrayAccessAssert(CtArrayAccess<?, ?> actual) {
		super(actual, CtArrayAccessAssert.class);
	}

	@Override
	public CtArrayAccessAssert self() {
		return this;
	}

	@Override
	public CtArrayAccess<?, ?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
