package spoon.architecture.constraints;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.helper.Models;
import spoon.reflect.CtModel;

public class InvocationMatcherTest {

	@Test
	public void testMethodInvocations() {
		// contract: the matcher finds every invocation: normal, local,constructor,lambda and executable reference
		CtModel model = Models.createModelFromString(
				"src/test/resources/spoon/architecture/constraints/invocationmatcher");
		InvocationMatcher matcher = new InvocationMatcher(model);
		assertDoesNotThrow(() -> ArchitectureTest.of(Precondition.of(
															DefaultElementFilter.METHODS.getFilter()),
															Constraint.of(new ExceptionError<>(), matcher))
															.runCheck(model));
	}
}
