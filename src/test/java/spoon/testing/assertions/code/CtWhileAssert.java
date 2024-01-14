package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtElement;
public class CtWhileAssert extends AbstractAssert<CtWhileAssert, CtWhile> {
	public CtWhileAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtWhileAssert(CtWhile actual) {
		super(actual, CtWhileAssert.class);
	}
}
