package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtWhile;
public class CtWhileAssert extends AbstractObjectAssert<CtWhileAssert, CtWhile> implements CtWhileAssertInterface<CtWhileAssert, CtWhile> {
	CtWhileAssert(CtWhile actual) {
		super(actual, CtWhileAssert.class);
	}

	@Override
	public CtWhileAssert self() {
		return this;
	}

	@Override
	public CtWhile actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
