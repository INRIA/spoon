package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtResource;
import spoon.reflect.declaration.CtElement;
public class CtResourceAssert extends AbstractAssert<CtResourceAssert, CtResource> {
	public CtResourceAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtResourceAssert(CtResource actual) {
		super(actual, CtResourceAssert.class);
	}
}
