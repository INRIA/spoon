package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtElement;
public class CtVariableReadAssert extends AbstractAssert<CtVariableReadAssert, CtVariableRead> {
	public CtVariableReadAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtVariableReadAssert(CtVariableRead actual) {
		super(actual, CtVariableReadAssert.class);
	}
}
