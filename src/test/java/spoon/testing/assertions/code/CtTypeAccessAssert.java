package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtElement;
public class CtTypeAccessAssert extends AbstractAssert<CtTypeAccessAssert, CtTypeAccess> {
	public CtTypeAccessAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtTypeAccessAssert(CtTypeAccess actual) {
		super(actual, CtTypeAccessAssert.class);
	}
}
