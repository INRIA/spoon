package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
public class CtParameterAssert extends AbstractAssert<CtParameterAssert, CtParameter> {
	public CtParameterAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtParameterAssert(CtParameter actual) {
		super(actual, CtParameterAssert.class);
	}
}
