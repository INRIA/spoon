package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtStatementList;
public class CtStatementListAssert extends AbstractObjectAssert<CtStatementListAssert, CtStatementList> implements CtStatementListAssertInterface<CtStatementListAssert, CtStatementList> {
	CtStatementListAssert(CtStatementList actual) {
		super(actual, CtStatementListAssert.class);
	}

	@Override
	public CtStatementListAssert self() {
		return this;
	}

	@Override
	public CtStatementList actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
