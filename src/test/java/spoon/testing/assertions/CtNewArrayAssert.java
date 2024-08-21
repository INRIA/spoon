package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtNewArray;
public class CtNewArrayAssert extends AbstractObjectAssert<CtNewArrayAssert, CtNewArray<?>> implements CtNewArrayAssertInterface<CtNewArrayAssert, CtNewArray<?>> {
	CtNewArrayAssert(CtNewArray<?> actual) {
		super(actual, CtNewArrayAssert.class);
	}

	@Override
	public CtNewArrayAssert self() {
		return this;
	}

	@Override
	public CtNewArray<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
