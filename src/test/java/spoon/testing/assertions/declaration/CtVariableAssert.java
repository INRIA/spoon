package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;
public class CtVariableAssert extends AbstractAssert<CtVariableAssert, CtVariable> {
	public CtVariableAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtVariableAssert(CtVariable actual) {
		super(actual, CtVariableAssert.class);
	}
}
