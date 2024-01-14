package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtCase;
import spoon.reflect.declaration.CtElement;
public class CtCaseAssert extends AbstractAssert<CtCaseAssert, CtCase> {
	public CtCaseAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtCaseAssert(CtCase actual) {
		super(actual, CtCaseAssert.class);
	}
}
