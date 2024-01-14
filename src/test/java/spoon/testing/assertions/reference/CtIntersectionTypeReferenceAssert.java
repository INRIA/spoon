package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtIntersectionTypeReference;
public class CtIntersectionTypeReferenceAssert extends AbstractAssert<CtIntersectionTypeReferenceAssert, CtIntersectionTypeReference> {
	public CtIntersectionTypeReferenceAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtIntersectionTypeReferenceAssert(CtIntersectionTypeReference actual) {
		super(actual, CtIntersectionTypeReferenceAssert.class);
	}
}
