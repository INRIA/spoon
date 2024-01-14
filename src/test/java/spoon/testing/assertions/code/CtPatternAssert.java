package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtPattern;
import spoon.reflect.declaration.CtElement;
public class CtPatternAssert extends AbstractAssert<CtPatternAssert, CtPattern> {
	public CtPatternAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtPatternAssert(CtPattern actual) {
		super(actual, CtPatternAssert.class);
	}
}
