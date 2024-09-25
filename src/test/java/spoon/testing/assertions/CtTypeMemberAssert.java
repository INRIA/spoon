package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtTypeMember;
public class CtTypeMemberAssert extends AbstractObjectAssert<CtTypeMemberAssert, CtTypeMember> implements CtTypeMemberAssertInterface<CtTypeMemberAssert, CtTypeMember> {
	CtTypeMemberAssert(CtTypeMember actual) {
		super(actual, CtTypeMemberAssert.class);
	}

	@Override
	public CtTypeMemberAssert self() {
		return this;
	}

	@Override
	public CtTypeMember actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
