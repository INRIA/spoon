package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypeParameter;
public class CtTypeParameterAssert extends AbstractAssert<CtTypeParameterAssert, CtTypeParameter> {
	public CtTypeParameterAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtTypeParameterAssert(CtTypeParameter actual) {
		super(actual, CtTypeParameterAssert.class);
	}
}
