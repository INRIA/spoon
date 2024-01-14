package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.declaration.CtElement;
public class CtTypePatternAssert extends AbstractAssert<CtTypePatternAssert, CtTypePattern> {
	public CtTypePatternAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtTypePatternAssert(CtTypePattern actual) {
		super(actual, CtTypePatternAssert.class);
	}
}
