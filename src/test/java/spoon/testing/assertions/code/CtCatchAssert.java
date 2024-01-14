package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtCatch;
import spoon.reflect.declaration.CtElement;
public class CtCatchAssert extends AbstractAssert<CtCatchAssert, CtCatch> {
	public CtCatchAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtCatchAssert(CtCatch actual) {
		super(actual, CtCatchAssert.class);
	}
}
