package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtTextBlock;
import spoon.reflect.declaration.CtElement;
public class CtTextBlockAssert extends AbstractAssert<CtTextBlockAssert, CtTextBlock> {
	public CtTextBlockAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtTextBlockAssert(CtTextBlock actual) {
		super(actual, CtTextBlockAssert.class);
	}
}
