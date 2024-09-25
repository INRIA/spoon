package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtTextBlock;
public class CtTextBlockAssert extends AbstractObjectAssert<CtTextBlockAssert, CtTextBlock> implements CtTextBlockAssertInterface<CtTextBlockAssert, CtTextBlock> {
	CtTextBlockAssert(CtTextBlock actual) {
		super(actual, CtTextBlockAssert.class);
	}

	@Override
	public CtTextBlockAssert self() {
		return this;
	}

	@Override
	public CtTextBlock actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
