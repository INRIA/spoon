package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
public class CtMethodAssert extends AbstractAssert<CtMethodAssert, CtMethod> {
	public CtMethodAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtMethodAssert(CtMethod actual) {
		super(actual, CtMethodAssert.class);
	}
}
