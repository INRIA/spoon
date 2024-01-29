package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtSynchronized;
public class CtSynchronizedAssert extends AbstractObjectAssert<CtSynchronizedAssert, CtSynchronized> implements CtSynchronizedAssertInterface<CtSynchronizedAssert, CtSynchronized> {
	CtSynchronizedAssert(CtSynchronized actual) {
		super(actual, CtSynchronizedAssert.class);
	}

	@Override
	public CtSynchronizedAssert self() {
		return this;
	}

	@Override
	public CtSynchronized actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
