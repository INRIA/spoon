package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.declaration.CtElement;
public class CtTryWithResourceAssert extends AbstractAssert<CtTryWithResourceAssert, CtTryWithResource> {
	public CtTryWithResourceAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtTryWithResourceAssert(CtTryWithResource actual) {
		super(actual, CtTryWithResourceAssert.class);
	}
}
