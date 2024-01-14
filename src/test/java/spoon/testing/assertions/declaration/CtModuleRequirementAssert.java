package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModuleRequirement;
public class CtModuleRequirementAssert extends AbstractAssert<CtModuleRequirementAssert, CtModuleRequirement> {
	public CtModuleRequirementAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtModuleRequirementAssert(CtModuleRequirement actual) {
		super(actual, CtModuleRequirementAssert.class);
	}
}
