package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtRecordComponent;
public class CtRecordComponentAssert extends AbstractObjectAssert<CtRecordComponentAssert, CtRecordComponent> implements CtRecordComponentAssertInterface<CtRecordComponentAssert, CtRecordComponent> {
	CtRecordComponentAssert(CtRecordComponent actual) {
		super(actual, CtRecordComponentAssert.class);
	}

	@Override
	public CtRecordComponentAssert self() {
		return this;
	}

	@Override
	public CtRecordComponent actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
