package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtArrayWrite;
public class CtArrayWriteAssert extends AbstractObjectAssert<CtArrayWriteAssert, CtArrayWrite<?>> implements CtArrayWriteAssertInterface<CtArrayWriteAssert, CtArrayWrite<?>> {
	CtArrayWriteAssert(CtArrayWrite<?> actual) {
		super(actual, CtArrayWriteAssert.class);
	}

	@Override
	public CtArrayWriteAssert self() {
		return this;
	}

	@Override
	public CtArrayWrite<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
