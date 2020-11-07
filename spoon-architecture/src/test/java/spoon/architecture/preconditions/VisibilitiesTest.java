package spoon.architecture.preconditions;

import static com.google.common.truth.Truth.assertThat;
import org.junit.jupiter.api.Test;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.Precondition;
import spoon.architecture.constraints.Exists;
import spoon.architecture.helper.CountingErrorCollector;
import spoon.architecture.helper.Models;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.visitor.filter.TypeFilter;

public class VisibilitiesTest {

	@Test
	public void testPublicModifierFilter() {
		// contract: the 1 element with modifier public is found and matched by the precondition
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/visibility/Visibility.java");
		CountingErrorCollector<CtModifiable> collector = new CountingErrorCollector<>();
		ArchitectureTest.of(Precondition.of(
											new TypeFilter<>(CtModifiable.class),
											Visibilities.isPublic()),
											Constraint.of(collector, new Exists<>()))
											.runCheck(model);

		assertThat(collector.getCounter()).isEqualTo(1);
	}
	@Test
	public void testProtectedModifierFilter() {
		// contract: the 1 element with modifier protected is found and matched by the precondition
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/visibility/Visibility.java");
		CountingErrorCollector<CtModifiable> collector = new CountingErrorCollector<>();
		ArchitectureTest.of(Precondition.of(
											new TypeFilter<>(CtModifiable.class),
											Visibilities.isProtected()),
											Constraint.of(collector, new Exists<>()))
											.runCheck(model);

		assertThat(collector.getCounter()).isEqualTo(1);
	}
	@Test
	public void testPrivateModifierFilter() {
		// contract: the 1 element with modifier private is found and matched by the precondition
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/visibility/Visibility.java");
		CountingErrorCollector<CtModifiable> collector = new CountingErrorCollector<>();
		ArchitectureTest.of(Precondition.of(
											new TypeFilter<>(CtModifiable.class),
											Visibilities.isPrivate()),
											Constraint.of(collector, new Exists<>()))
											.runCheck(model);

		assertThat(collector.getCounter()).isEqualTo(1);
	}
	@Test
	public void testDefaultModifierFilter() {
		// contract: the 1 element with no modifier is found and matched by the precondition
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/visibility/Visibility.java");
		CountingErrorCollector<CtModifiable> collector = new CountingErrorCollector<>();
		ArchitectureTest.of(Precondition.of(
											new TypeFilter<>(CtModifiable.class),
											Visibilities.isDefault()),
											Constraint.of(collector, new Exists<>()))
											.runCheck(model);

		assertThat(collector.getCounter()).isEqualTo(1);
	}
}
