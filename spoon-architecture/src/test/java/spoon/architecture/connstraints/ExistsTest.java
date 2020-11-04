package spoon.architecture.connstraints;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static spoon.architecture.helper.Models.createModelFromString;
import org.junit.jupiter.api.Test;
import spoon.SpoonException;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.constraints.Exists;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.reflect.CtModel;

public class ExistsTest {

	@Test
	public void testNoInterfacesExist() {
		// contract: with the existence quantifier it's possible that no interface must exist.
		CtModel model = createModelFromString(
				"src/test/resources/spoon/architecture/constraints/exists/HelloWorld.java");
		assertDoesNotThrow(() -> ArchitectureTest.of(Precondition.of(DefaultElementFilter.INTERFACES.getFilter()),
																									Constraint.of(new ExceptionError<>(), new Exists<>()))
																									.runCheck(model));
	}
	@Test
	public void testNoLambdasExist() {
	// contract: with the existence quantifier it's possible that no lambda must exist.
		CtModel model = createModelFromString(
				"src/test/resources/spoon/architecture/constraints/exists/HelloWorld.java");
		assertDoesNotThrow(() -> ArchitectureTest.of(Precondition.of(DefaultElementFilter.LAMBDAS.getFilter()),
																									Constraint.of(new ExceptionError<>(), new Exists<>()))
																									.runCheck(model));
	}
	@Test
	public void testClassExist() {
	// contract: with the existence quantifier it's possible to check any class must exist.
		CtModel model = createModelFromString(
				"src/test/resources/spoon/architecture/constraints/exists/HelloWorld.java");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(DefaultElementFilter.CLASSES.getFilter()),
																									Constraint.of(new ExceptionError<>(), new Exists<>()))
																									.runCheck(model));
	}
}
