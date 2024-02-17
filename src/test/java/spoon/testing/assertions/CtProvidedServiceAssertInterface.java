package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.reference.CtTypeReference;
public interface CtProvidedServiceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtProvidedService> extends SpoonAssert<A, W> , CtModuleDirectiveAssertInterface<A, W> {
	default ListAssert<CtTypeReference> getImplementationTypes() {
		return Assertions.assertThat(actual().getImplementationTypes());
	}

	default CtTypeReferenceAssertInterface<?, ?> getServiceType() {
		return SpoonAssertions.assertThat(actual().getServiceType());
	}
}
