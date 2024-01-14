package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtModuleRequirement;
public class CtModuleRequirementAssert extends AbstractAssert<CtModuleRequirementAssert, CtModuleRequirement> {
	public CtModuleRequirementAssert(CtModuleRequirement actual) {
		super(actual, CtModuleRequirementAssert.class);
	}
}
