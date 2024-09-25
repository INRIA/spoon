package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtModuleRequirement;
public class CtModuleRequirementAssert extends AbstractObjectAssert<CtModuleRequirementAssert, CtModuleRequirement> implements CtModuleRequirementAssertInterface<CtModuleRequirementAssert, CtModuleRequirement> {
	CtModuleRequirementAssert(CtModuleRequirement actual) {
		super(actual, CtModuleRequirementAssert.class);
	}

	@Override
	public CtModuleRequirementAssert self() {
		return this;
	}

	@Override
	public CtModuleRequirement actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
