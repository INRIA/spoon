package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtArrayTypeReference;
public class CtArrayTypeReferenceAssert extends AbstractAssert<CtArrayTypeReferenceAssert, CtArrayTypeReference> {
	public CtArrayTypeReferenceAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtArrayTypeReferenceAssert(CtArrayTypeReference actual) {
		super(actual, CtArrayTypeReferenceAssert.class);
	}
}
