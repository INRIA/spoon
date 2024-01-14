package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtElement;
public class CtLocalVariableAssert extends AbstractAssert<CtLocalVariableAssert, CtLocalVariable> {
	public CtLocalVariableAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtLocalVariableAssert(CtLocalVariable actual) {
		super(actual, CtLocalVariableAssert.class);
	}
}
