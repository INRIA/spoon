package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtUsedService;
public interface CtUsedServiceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtUsedService> extends SpoonAssert<A, W> , CtModuleDirectiveAssertInterface<A, W> {
	default CtTypeReferenceAssertInterface<?, ?> getServiceType() {
		return SpoonAssertions.assertThat(actual().getServiceType());
	}
}
