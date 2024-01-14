package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtStatement;
public class CtStatementAssert extends AbstractAssert<CtStatementAssert, CtStatement> {
	public CtStatementAssert(CtStatement actual) {
		super(actual, CtStatementAssert.class);
	}
}
