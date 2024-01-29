package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtStatement;
public class CtStatementAssert extends AbstractObjectAssert<CtStatementAssert, CtStatement> implements CtStatementAssertInterface<CtStatementAssert, CtStatement> {
	CtStatementAssert(CtStatement actual) {
		super(actual, CtStatementAssert.class);
	}

	@Override
	public CtStatementAssert self() {
		return this;
	}

	@Override
	public CtStatement actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
