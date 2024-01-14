package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtConditional;
import spoon.reflect.declaration.CtElement;
public class CtConditionalAssert extends AbstractAssert<CtConditionalAssert, CtConditional> {
	public CtConditionalAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtConditionalAssert(CtConditional actual) {
		super(actual, CtConditionalAssert.class);
	}
}
