package spoon.architecture.preconditions;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import spoon.SpoonException;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.ElementFilters;
import spoon.architecture.Precondition;
import spoon.architecture.constraints.Exists;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.helper.Models;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.visitor.filter.TypeFilter;

public class NamesTest {

	@Test
	public void testEqualName() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											new TypeFilter<CtNamedElement>(CtNamedElement.class),
											Names.equals("Foobar")),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}

	@Test
	public void testContainsName() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											ElementFilters.ofClassObject(CtNamedElement.class,
											Names.contains("oob"))),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}

	@Test
	public void testStartsWithName() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											new TypeFilter<CtNamedElement>(CtNamedElement.class),
											Names.startsWith("F")),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}

	@Test
	public void testEndsWithName() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											new TypeFilter<CtNamedElement>(CtNamedElement.class),
											Names.endsWith("ar")),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}

	@Test
	public void testMatchesName() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											new TypeFilter<CtNamedElement>(CtNamedElement.class),
											Names.matches("^Fo+bar")),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}


	@Test
	public void testMatchesNotName() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertDoesNotThrow(() -> ArchitectureTest.of(Precondition.of(
											new TypeFilter<CtNamedElement>(CtNamedElement.class),
											Names.matchesNot(".*")),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}

	@Test
	public void testMatchesNameWithPattern() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											new TypeFilter<CtNamedElement>(CtNamedElement.class),
											Names.matches(Pattern.compile("(?i)^fo+bar"))),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}
	@Test
	public void testMatchesNotNameWithPattern() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertDoesNotThrow(() -> ArchitectureTest.of(Precondition.of(
											new TypeFilter<CtNamedElement>(CtNamedElement.class),
											Names.matchesNot(Pattern.compile(".*"))),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}
	@Test
	public void testEqualsFQName() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											DefaultElementFilter.TYPES.getFilter(),
											Names.equalsQualified("spoon.architecture.preconditions.naming.QualifiedNames")),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}
	@Test
	public void testStartsWithFQName() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											DefaultElementFilter.TYPES.getFilter(),
											Names.startsWithQualified("spoon.architecture")),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}
	@Test
	public void testContainsFQName() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											DefaultElementFilter.TYPES.getFilter(),
											Names.containsQualified("spoon")),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}
	@Test
	public void testEndsWithFQName() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											DefaultElementFilter.TYPES.getFilter(),
											Names.endsWithQualified("QualifiedNames")),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}

	@Test
	public void testMatchesFQName() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											DefaultElementFilter.TYPES.getFilter(),
											Names.matchesQualified(".*Names")),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}


	@Test
	public void testMatchesNotFQName() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertDoesNotThrow(() -> ArchitectureTest.of(Precondition.of(
											DefaultElementFilter.TYPES.getFilter(),
											Names.matchesNotQualified(".*")),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}

	@Test
	public void testMatchesFQNameWithPattern() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertThrows(SpoonException.class, () -> ArchitectureTest.of(Precondition.of(
											DefaultElementFilter.TYPES.getFilter(),
											Names.matchesQualified(Pattern.compile(".*Names"))),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}
	@Test
	public void testMatchesNotFQNameWithPattern() {
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/naming");
		assertDoesNotThrow(() -> ArchitectureTest.of(Precondition.of(
			DefaultElementFilter.TYPES.getFilter(),
			Names.matchesNotQualified(Pattern.compile(".*"))),
											Constraint.of(new ExceptionError<>(), new Exists<>()))
											.runCheck(model));
	}
}
