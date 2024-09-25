package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtForEach;
public class CtForEachAssert extends AbstractObjectAssert<CtForEachAssert, CtForEach> implements CtForEachAssertInterface<CtForEachAssert, CtForEach> {
	CtForEachAssert(CtForEach actual) {
		super(actual, CtForEachAssert.class);
	}

	@Override
	public CtForEachAssert self() {
		return this;
	}

	@Override
	public CtForEach actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
