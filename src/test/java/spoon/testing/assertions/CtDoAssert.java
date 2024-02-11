package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtDo;
public class CtDoAssert extends AbstractObjectAssert<CtDoAssert, CtDo> implements CtDoAssertInterface<CtDoAssert, CtDo> {
	CtDoAssert(CtDo actual) {
		super(actual, CtDoAssert.class);
	}

	@Override
	public CtDoAssert self() {
		return this;
	}

	@Override
	public CtDo actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
