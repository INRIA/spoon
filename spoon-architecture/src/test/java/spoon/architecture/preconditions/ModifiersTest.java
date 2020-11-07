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

public class ModifiersTest {

	@Test
	public void testAbstractFilter() {
		// contract: the 1 element with modifier abstract is found and matched by the precondition
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/modifier/Modifiers.java");
		CountingErrorCollector<CtModifiable> collector = new CountingErrorCollector<>();
		ArchitectureTest.of(Precondition.of(
											new TypeFilter<>(CtModifiable.class),
											ModifierFilter.isAbstract()),
											Constraint.of(collector, new Exists<>()))
											.runCheck(model);
		assertThat(collector.getCounter()).isEqualTo(1);
	}

	@Test
	public void testTransientFilter() {
		// contract: the 1 element with modifier transient is found and matched by the precondition
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/modifier/Modifiers.java");
		CountingErrorCollector<CtModifiable> collector = new CountingErrorCollector<>();
		ArchitectureTest.of(Precondition.of(
											new TypeFilter<>(CtModifiable.class),
											ModifierFilter.isTransient()),
											Constraint.of(collector, new Exists<>()))
											.runCheck(model);
		assertThat(collector.getCounter()).isEqualTo(1);
	}

	@Test
	public void testSynchronizedFilter() {
		// contract: the 1 element with modifier synchronized is found and matched by the precondition
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/modifier/Modifiers.java");
		CountingErrorCollector<CtModifiable> collector = new CountingErrorCollector<>();
		ArchitectureTest.of(Precondition.of(
											new TypeFilter<>(CtModifiable.class),
											ModifierFilter.isSynchronized()),
											Constraint.of(collector, new Exists<>()))
											.runCheck(model);
		assertThat(collector.getCounter()).isEqualTo(1);
	}

	@Test
	public void testFinalFilter() {
		// contract: the 1 element with modifier final is found and matched by the precondition
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/modifier/Modifiers.java");
		CountingErrorCollector<CtModifiable> collector = new CountingErrorCollector<>();
		ArchitectureTest.of(Precondition.of(
											new TypeFilter<>(CtModifiable.class),
											ModifierFilter.isFinal()),
											Constraint.of(collector, new Exists<>()))
											.runCheck(model);
		assertThat(collector.getCounter()).isEqualTo(1);
	}

	@Test
	public void testStaticFilter() {
		// contract: the 1 element with modifier static is found and matched by the precondition
		CtModel model = Models.createModelFromString("src/test/resources/spoon/architecture/preconditions/modifier/Modifiers.java");
		CountingErrorCollector<CtModifiable> collector = new CountingErrorCollector<>();
		ArchitectureTest.of(Precondition.of(
											new TypeFilter<>(CtModifiable.class),
											ModifierFilter.isStatic()),
											Constraint.of(collector, new Exists<>()))
											.runCheck(model);
		assertThat(collector.getCounter()).isEqualTo(1);
	}
}
