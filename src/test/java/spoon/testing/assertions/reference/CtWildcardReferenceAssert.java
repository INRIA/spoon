package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtWildcardReference;
public class CtWildcardReferenceAssert extends AbstractAssert<CtWildcardReferenceAssert, CtWildcardReference> {
	public CtWildcardReferenceAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtWildcardReferenceAssert(CtWildcardReference actual) {
		super(actual, CtWildcardReferenceAssert.class);
	}
}
