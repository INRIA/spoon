package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtFor;
import spoon.reflect.declaration.CtElement;
public class CtForAssert extends AbstractAssert<CtForAssert, CtFor> {
	public CtForAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtForAssert(CtFor actual) {
		super(actual, CtForAssert.class);
	}
}
