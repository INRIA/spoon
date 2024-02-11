package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtFieldAccess;
public class CtFieldAccessAssert extends AbstractObjectAssert<CtFieldAccessAssert, CtFieldAccess<?>> implements CtFieldAccessAssertInterface<CtFieldAccessAssert, CtFieldAccess<?>> {
	CtFieldAccessAssert(CtFieldAccess<?> actual) {
		super(actual, CtFieldAccessAssert.class);
	}

	@Override
	public CtFieldAccessAssert self() {
		return this;
	}

	@Override
	public CtFieldAccess<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
