package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtArrayRead;
public class CtArrayReadAssert extends AbstractObjectAssert<CtArrayReadAssert, CtArrayRead<?>> implements CtArrayReadAssertInterface<CtArrayReadAssert, CtArrayRead<?>> {
	CtArrayReadAssert(CtArrayRead<?> actual) {
		super(actual, CtArrayReadAssert.class);
	}

	@Override
	public CtArrayReadAssert self() {
		return this;
	}

	@Override
	public CtArrayRead<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
