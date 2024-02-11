package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtBlock;
public class CtBlockAssert extends AbstractObjectAssert<CtBlockAssert, CtBlock<?>> implements CtBlockAssertInterface<CtBlockAssert, CtBlock<?>> {
	CtBlockAssert(CtBlock<?> actual) {
		super(actual, CtBlockAssert.class);
	}

	@Override
	public CtBlockAssert self() {
		return this;
	}

	@Override
	public CtBlock<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
