package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtThisAccess;
public class CtThisAccessAssert extends AbstractObjectAssert<CtThisAccessAssert, CtThisAccess<?>> implements CtThisAccessAssertInterface<CtThisAccessAssert, CtThisAccess<?>> {
	CtThisAccessAssert(CtThisAccess<?> actual) {
		super(actual, CtThisAccessAssert.class);
	}

	@Override
	public CtThisAccessAssert self() {
		return this;
	}

	@Override
	public CtThisAccess<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
