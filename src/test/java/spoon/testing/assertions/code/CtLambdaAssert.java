package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtElement;
public class CtLambdaAssert extends AbstractAssert<CtLambdaAssert, CtLambda> {
	public CtLambdaAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtLambdaAssert(CtLambda actual) {
		super(actual, CtLambdaAssert.class);
	}
}
