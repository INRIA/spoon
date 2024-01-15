package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ListAssert;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.reference.CtTypeReference;
interface CtProvidedServiceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtProvidedService> extends SpoonAssert<A, W> , CtModuleDirectiveAssertInterface<A, W> {
    default ListAssert<CtTypeReference> getImplementationTypes() {
        return org.assertj.core.api.Assertions.assertThat(actual().getImplementationTypes());
    }

    default CtTypeReferenceAssertInterface<?, ?> getServiceType() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getServiceType());
    }
}