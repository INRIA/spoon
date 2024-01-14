package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtElement;
public class CtLiteralAssert extends AbstractAssert<CtLiteralAssert, CtLiteral> {
	public CtLiteralAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtLiteralAssert(CtLiteral actual) {
		super(actual, CtLiteralAssert.class);
	}
}
