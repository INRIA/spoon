package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypeMember;
public class CtTypeMemberAssert extends AbstractAssert<CtTypeMemberAssert, CtTypeMember> {
	public CtTypeMemberAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtTypeMemberAssert(CtTypeMember actual) {
		super(actual, CtTypeMemberAssert.class);
	}
}
