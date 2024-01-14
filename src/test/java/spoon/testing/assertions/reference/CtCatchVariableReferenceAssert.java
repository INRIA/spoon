package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtCatchVariableReference;
public class CtCatchVariableReferenceAssert extends AbstractAssert<CtCatchVariableReferenceAssert, CtCatchVariableReference> {
	public CtCatchVariableReferenceAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtCatchVariableReferenceAssert(CtCatchVariableReference actual) {
		super(actual, CtCatchVariableReferenceAssert.class);
	}
}
