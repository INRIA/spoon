package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtFieldReference;
public class CtFieldReferenceAssert extends AbstractAssert<CtFieldReferenceAssert, CtFieldReference> {
	public CtFieldReferenceAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtFieldReferenceAssert(CtFieldReference actual) {
		super(actual, CtFieldReferenceAssert.class);
	}
}
