package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtAssignment;
public class CtAssignmentAssert extends AbstractAssert<CtAssignmentAssert, CtAssignment> {
	public CtAssignmentAssert(CtAssignment actual) {
		super(actual, CtAssignmentAssert.class);
	}
}
