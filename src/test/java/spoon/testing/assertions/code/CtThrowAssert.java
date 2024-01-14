package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtElement;
public class CtThrowAssert extends AbstractAssert<CtThrowAssert, CtThrow> {
	public CtThrowAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtThrowAssert(CtThrow actual) {
		super(actual, CtThrowAssert.class);
	}
}
