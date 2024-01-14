package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeParameterReference;
public class CtTypeParameterReferenceAssert extends AbstractAssert<CtTypeParameterReferenceAssert, CtTypeParameterReference> {
	public CtTypeParameterReferenceAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtTypeParameterReferenceAssert(CtTypeParameterReference actual) {
		super(actual, CtTypeParameterReferenceAssert.class);
	}
}
