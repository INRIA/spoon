package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtStatementList;
public class CtStatementListAssert extends AbstractAssert<CtStatementListAssert, CtStatementList> {
	public CtStatementListAssert(CtStatementList actual) {
		super(actual, CtStatementListAssert.class);
	}
}
