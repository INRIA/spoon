package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtModuleReference;
public class CtModuleReferenceAssert extends AbstractAssert<CtModuleReferenceAssert, CtModuleReference> {
	public CtModuleReferenceAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtModuleReferenceAssert(CtModuleReference actual) {
		super(actual, CtModuleReferenceAssert.class);
	}
}
