package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
public class CtExpressionAssert extends AbstractAssert<CtExpressionAssert, CtExpression> {
	public CtExpressionAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtExpressionAssert(CtExpression actual) {
		super(actual, CtExpressionAssert.class);
	}
}
