package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.declaration.CtElement;
public class CtYieldStatementAssert extends AbstractAssert<CtYieldStatementAssert, CtYieldStatement> {
	public CtYieldStatementAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtYieldStatementAssert(CtYieldStatement actual) {
		super(actual, CtYieldStatementAssert.class);
	}
}
