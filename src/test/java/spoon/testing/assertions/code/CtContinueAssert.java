package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtContinue;
import spoon.reflect.declaration.CtElement;
public class CtContinueAssert extends AbstractAssert<CtContinueAssert, CtContinue> {
	public CtContinueAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtContinueAssert(CtContinue actual) {
		super(actual, CtContinueAssert.class);
	}
}
