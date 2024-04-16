package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtOperatorAssignment;
public class CtOperatorAssignmentAssert extends AbstractObjectAssert<CtOperatorAssignmentAssert, CtOperatorAssignment<?, ?>> implements CtOperatorAssignmentAssertInterface<CtOperatorAssignmentAssert, CtOperatorAssignment<?, ?>> {
	CtOperatorAssignmentAssert(CtOperatorAssignment<?, ?> actual) {
		super(actual, CtOperatorAssignmentAssert.class);
	}

	@Override
	public CtOperatorAssignmentAssert self() {
		return this;
	}

	@Override
	public CtOperatorAssignment<?, ?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
