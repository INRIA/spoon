package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.declaration.CtElement;
public class CtSuperAccessAssert extends AbstractAssert<CtSuperAccessAssert, CtSuperAccess> {
	public CtSuperAccessAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtSuperAccessAssert(CtSuperAccess actual) {
		super(actual, CtSuperAccessAssert.class);
	}
}
