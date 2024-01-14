package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtParameterReference;
public class CtParameterReferenceAssert extends AbstractAssert<CtParameterReferenceAssert, CtParameterReference> {
	public CtParameterReferenceAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtParameterReferenceAssert(CtParameterReference actual) {
		super(actual, CtParameterReferenceAssert.class);
	}
}
