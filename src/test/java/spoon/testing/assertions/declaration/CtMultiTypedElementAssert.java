package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMultiTypedElement;
public class CtMultiTypedElementAssert extends AbstractAssert<CtMultiTypedElementAssert, CtMultiTypedElement> {
	public CtMultiTypedElementAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtMultiTypedElementAssert(CtMultiTypedElement actual) {
		super(actual, CtMultiTypedElementAssert.class);
	}
}
