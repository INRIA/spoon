package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtRecord;
public class CtRecordAssert extends AbstractObjectAssert<CtRecordAssert, CtRecord> implements CtRecordAssertInterface<CtRecordAssert, CtRecord> {
	CtRecordAssert(CtRecord actual) {
		super(actual, CtRecordAssert.class);
	}

	@Override
	public CtRecordAssert self() {
		return this;
	}

	@Override
	public CtRecord actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
