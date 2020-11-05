package spoon.architecture.preconditions;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import spoon.SpoonException;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.constraints.Exists;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.helper.Models;
import spoon.reflect.CtModel;

public class AnnotationHelperTest {

	@Test
	public void testAnnotationHelperWithClass() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											DefaultElementFilter.METHODS.getFilter(),
											AnnotationHelper.hasAnnotationMatcher(Deprecated.class)),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}

	@Test
	public void testAnnotationHelperWithName() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											DefaultElementFilter.METHODS.getFilter(),
											AnnotationHelper.hasAnnotationMatcher("Deprecated", false)),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}

	@Test
	public void testAnnotationHelperWithFQName() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											DefaultElementFilter.METHODS.getFilter(),
											AnnotationHelper.hasAnnotationMatcher("java.lang.Deprecated", true)),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}
	@Test
	public void testAnnotationHelperWithPattern() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											DefaultElementFilter.METHODS.getFilter(),
											AnnotationHelper.hasAnnotationMatcher(Pattern.compile("(?i)deprecated"), false)),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}

	@Test
	public void testAnnotationHelperWithPatternAndFQ() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											DefaultElementFilter.METHODS.getFilter(),
											AnnotationHelper.hasAnnotationMatcher(Pattern.compile("(?i)java[.].*deprecated"), true)),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}
}
