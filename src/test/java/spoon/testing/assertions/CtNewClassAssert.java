package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtNewClass;
public class CtNewClassAssert extends AbstractObjectAssert<CtNewClassAssert, CtNewClass<?>> implements CtNewClassAssertInterface<CtNewClassAssert, CtNewClass<?>> {
	CtNewClassAssert(CtNewClass<?> actual) {
		super(actual, CtNewClassAssert.class);
	}

	@Override
	public CtNewClassAssert self() {
		return this;
	}

	@Override
	public CtNewClass<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
