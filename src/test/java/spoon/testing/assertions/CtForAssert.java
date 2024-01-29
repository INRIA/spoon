package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtFor;
public class CtForAssert extends AbstractObjectAssert<CtForAssert, CtFor> implements CtForAssertInterface<CtForAssert, CtFor> {
	CtForAssert(CtFor actual) {
		super(actual, CtForAssert.class);
	}

	@Override
	public CtForAssert self() {
		return this;
	}

	@Override
	public CtFor actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
