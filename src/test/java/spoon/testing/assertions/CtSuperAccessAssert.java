package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtSuperAccess;
public class CtSuperAccessAssert extends AbstractObjectAssert<CtSuperAccessAssert, CtSuperAccess<?>> implements CtSuperAccessAssertInterface<CtSuperAccessAssert, CtSuperAccess<?>> {
	CtSuperAccessAssert(CtSuperAccess<?> actual) {
		super(actual, CtSuperAccessAssert.class);
	}

	@Override
	public CtSuperAccessAssert self() {
		return this;
	}

	@Override
	public CtSuperAccess<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
