package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtForEach;
import spoon.reflect.declaration.CtElement;
public class CtForEachAssert extends AbstractAssert<CtForEachAssert, CtForEach> {
	public CtForEachAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtForEachAssert(CtForEach actual) {
		super(actual, CtForEachAssert.class);
	}
}
