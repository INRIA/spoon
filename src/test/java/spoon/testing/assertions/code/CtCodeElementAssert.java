package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
public class CtCodeElementAssert extends AbstractAssert<CtCodeElementAssert, CtCodeElement> {
	public CtCodeElementAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtCodeElementAssert(CtCodeElement actual) {
		super(actual, CtCodeElementAssert.class);
	}
}
