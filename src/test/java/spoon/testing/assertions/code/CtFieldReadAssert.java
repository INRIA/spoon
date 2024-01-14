package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.declaration.CtElement;
public class CtFieldReadAssert extends AbstractAssert<CtFieldReadAssert, CtFieldRead> {
	public CtFieldReadAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtFieldReadAssert(CtFieldRead actual) {
		super(actual, CtFieldReadAssert.class);
	}
}
