package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtElement;
public class CtBlockAssert extends AbstractAssert<CtBlockAssert, CtBlock> {
	public CtBlockAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtBlockAssert(CtBlock actual) {
		super(actual, CtBlockAssert.class);
	}
}
