package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypedElement;
public class CtTypedElementAssert extends AbstractAssert<CtTypedElementAssert, CtTypedElement> {
	public CtTypedElementAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtTypedElementAssert(CtTypedElement actual) {
		super(actual, CtTypedElementAssert.class);
	}
}
