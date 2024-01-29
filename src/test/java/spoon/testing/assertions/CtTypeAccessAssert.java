package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtTypeAccess;
public class CtTypeAccessAssert extends AbstractObjectAssert<CtTypeAccessAssert, CtTypeAccess<?>> implements CtTypeAccessAssertInterface<CtTypeAccessAssert, CtTypeAccess<?>> {
	CtTypeAccessAssert(CtTypeAccess<?> actual) {
		super(actual, CtTypeAccessAssert.class);
	}

	@Override
	public CtTypeAccessAssert self() {
		return this;
	}

	@Override
	public CtTypeAccess<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
