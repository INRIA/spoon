package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtYieldStatement;
public class CtYieldStatementAssert extends AbstractObjectAssert<CtYieldStatementAssert, CtYieldStatement> implements CtYieldStatementAssertInterface<CtYieldStatementAssert, CtYieldStatement> {
	CtYieldStatementAssert(CtYieldStatement actual) {
		super(actual, CtYieldStatementAssert.class);
	}

	@Override
	public CtYieldStatementAssert self() {
		return this;
	}

	@Override
	public CtYieldStatement actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
