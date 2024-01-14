package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtElement;
public class CtTryAssert extends AbstractAssert<CtTryAssert, CtTry> {
	public CtTryAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtTryAssert(CtTry actual) {
		super(actual, CtTryAssert.class);
	}
}
