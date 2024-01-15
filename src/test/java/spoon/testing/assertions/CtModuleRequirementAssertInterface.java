package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtModuleRequirement;
interface CtModuleRequirementAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtModuleRequirement> extends SpoonAssert<A, W> , CtModuleDirectiveAssertInterface<A, W> {
    default CtModuleReferenceAssertInterface<?, ?> getModuleReference() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getModuleReference());
    }

    default AbstractCollectionAssert<?, Collection<? extends CtModuleRequirement.RequiresModifier>, CtModuleRequirement.RequiresModifier, ?> getRequiresModifiers() {
        return org.assertj.core.api.Assertions.assertThat(actual().getRequiresModifiers());
    }
}