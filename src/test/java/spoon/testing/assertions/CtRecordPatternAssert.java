package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtRecordPattern;
public class CtRecordPatternAssert extends AbstractObjectAssert<CtRecordPatternAssert, CtRecordPattern> implements CtRecordPatternAssertInterface<CtRecordPatternAssert, CtRecordPattern> {
	CtRecordPatternAssert(CtRecordPattern actual) {
		super(actual, CtRecordPatternAssert.class);
	}

	@Override
	public CtRecordPatternAssert self() {
		return this;
	}

	@Override
	public CtRecordPattern actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
