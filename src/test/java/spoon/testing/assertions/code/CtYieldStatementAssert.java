package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtYieldStatement;
public class CtYieldStatementAssert extends AbstractAssert<CtYieldStatementAssert, CtYieldStatement> {
	public CtYieldStatementAssert(CtYieldStatement actual) {
		super(actual, CtYieldStatementAssert.class);
	}
}
