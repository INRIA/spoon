package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
public class CtEnumAssert extends AbstractAssert<CtEnumAssert, CtEnum> {
	public CtEnumAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtEnumAssert(CtEnum actual) {
		super(actual, CtEnumAssert.class);
	}
}
