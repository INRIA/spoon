package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtAssignment;
public class CtAssignmentAssert extends AbstractObjectAssert<CtAssignmentAssert, CtAssignment<?, ?>> implements CtAssignmentAssertInterface<CtAssignmentAssert, CtAssignment<?, ?>> {
	CtAssignmentAssert(CtAssignment<?, ?> actual) {
		super(actual, CtAssignmentAssert.class);
	}

	@Override
	public CtAssignmentAssert self() {
		return this;
	}

	@Override
	public CtAssignment<?, ?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
