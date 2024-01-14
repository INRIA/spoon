package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtBreak;
import spoon.reflect.declaration.CtElement;
public class CtBreakAssert extends AbstractAssert<CtBreakAssert, CtBreak> {
	public CtBreakAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtBreakAssert(CtBreak actual) {
		super(actual, CtBreakAssert.class);
	}
}
