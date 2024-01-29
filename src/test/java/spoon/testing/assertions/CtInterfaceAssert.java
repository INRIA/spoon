package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtInterface;
public class CtInterfaceAssert extends AbstractObjectAssert<CtInterfaceAssert, CtInterface<?>> implements CtInterfaceAssertInterface<CtInterfaceAssert, CtInterface<?>> {
	CtInterfaceAssert(CtInterface<?> actual) {
		super(actual, CtInterfaceAssert.class);
	}

	@Override
	public CtInterfaceAssert self() {
		return this;
	}

	@Override
	public CtInterface<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
