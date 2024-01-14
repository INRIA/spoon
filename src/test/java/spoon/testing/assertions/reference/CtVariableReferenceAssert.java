package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtVariableReference;
public class CtVariableReferenceAssert extends AbstractAssert<CtVariableReferenceAssert, CtVariableReference> {
	public CtVariableReferenceAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtVariableReferenceAssert(CtVariableReference actual) {
		super(actual, CtVariableReferenceAssert.class);
	}
}
