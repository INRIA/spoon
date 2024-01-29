package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtLoop;
public class CtLoopAssert extends AbstractObjectAssert<CtLoopAssert, CtLoop> implements CtLoopAssertInterface<CtLoopAssert, CtLoop> {
	CtLoopAssert(CtLoop actual) {
		super(actual, CtLoopAssert.class);
	}

	@Override
	public CtLoopAssert self() {
		return this;
	}

	@Override
	public CtLoop actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
