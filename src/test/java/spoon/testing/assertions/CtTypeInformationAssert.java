package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtTypeInformation;
public class CtTypeInformationAssert extends AbstractObjectAssert<CtTypeInformationAssert, CtTypeInformation> implements CtTypeInformationAssertInterface<CtTypeInformationAssert, CtTypeInformation> {
	CtTypeInformationAssert(CtTypeInformation actual) {
		super(actual, CtTypeInformationAssert.class);
	}

	@Override
	public CtTypeInformationAssert self() {
		return this;
	}

	@Override
	public CtTypeInformation actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
