package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;
public class CtIfAssert extends AbstractAssert<CtIfAssert, CtIf> {
	public CtIfAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtIfAssert(CtIf actual) {
		super(actual, CtIfAssert.class);
	}
}
