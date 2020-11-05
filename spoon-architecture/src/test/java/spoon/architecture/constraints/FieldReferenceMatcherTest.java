package spoon.architecture.constraints;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import spoon.SpoonException;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.helper.Models;
import spoon.reflect.CtModel;

public class FieldReferenceMatcherTest {

	@Test
	public void testLocalReferences() {
		// contract: every local reference is correct found
		CtModel model = Models.createModelFromString(
				"src/test/resources/spoon/architecture/constraints/fieldreference/LocalReference.java");
		FieldReferenceMatcher matcher = new FieldReferenceMatcher(model);
		assertDoesNotThrow(() -> ArchitectureTest
											.of(Precondition.of(DefaultElementFilter.FIELDS.getFilter()),
											Constraint.of(new ExceptionError<>(), matcher))
											.runCheck(model));
	}

	@Test
	public void testUnusedField() {
		// contract: unused fields is correct detected and throws an error
		CtModel model = Models.createModelFromString(
				"src/test/resources/spoon/architecture/constraints/fieldreference/UnusedReference.java");
		FieldReferenceMatcher matcher = new FieldReferenceMatcher(model);
		assertThrows(SpoonException.class,
											() -> ArchitectureTest
											.of(Precondition.of(DefaultElementFilter.FIELDS.getFilter()),
											Constraint.of(new ExceptionError<>(), matcher))
											.runCheck(model));
	}
}
