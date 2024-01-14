package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtElement;
public class CtUnaryOperatorAssert extends AbstractAssert<CtUnaryOperatorAssert, CtUnaryOperator> {
	public CtUnaryOperatorAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtUnaryOperatorAssert(CtUnaryOperator actual) {
		super(actual, CtUnaryOperatorAssert.class);
	}
}
