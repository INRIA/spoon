package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.CtModuleRequirement;
public interface CtModuleRequirementAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtModuleRequirement> extends SpoonAssert<A, W> , CtModuleDirectiveAssertInterface<A, W> {
	default CtModuleReferenceAssertInterface<?, ?> getModuleReference() {
		return SpoonAssertions.assertThat(actual().getModuleReference());
	}

	default AbstractCollectionAssert<?, Collection<? extends CtModuleRequirement.RequiresModifier>, CtModuleRequirement.RequiresModifier, ?> getRequiresModifiers() {
		return Assertions.assertThat(actual().getRequiresModifiers());
	}
}
