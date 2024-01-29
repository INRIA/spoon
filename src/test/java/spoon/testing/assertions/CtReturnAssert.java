package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtReturn;
public class CtReturnAssert extends AbstractObjectAssert<CtReturnAssert, CtReturn<?>> implements CtReturnAssertInterface<CtReturnAssert, CtReturn<?>> {
	CtReturnAssert(CtReturn<?> actual) {
		super(actual, CtReturnAssert.class);
	}

	@Override
	public CtReturnAssert self() {
		return this;
	}

	@Override
	public CtReturn<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
