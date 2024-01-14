package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtElement;
public class CtStatementListAssert extends AbstractAssert<CtStatementListAssert, CtStatementList> {
	public CtStatementListAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtStatementListAssert(CtStatementList actual) {
		super(actual, CtStatementListAssert.class);
	}
}
