package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.declaration.CtElement;
public class CtArrayAccessAssert extends AbstractAssert<CtArrayAccessAssert, CtArrayAccess> {
	public CtArrayAccessAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtArrayAccessAssert(CtArrayAccess actual) {
		super(actual, CtArrayAccessAssert.class);
	}
}
