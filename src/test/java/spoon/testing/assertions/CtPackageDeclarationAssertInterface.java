package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtPackageDeclaration;
public interface CtPackageDeclarationAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtPackageDeclaration> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
	default CtPackageReferenceAssertInterface<?, ?> getReference() {
		return SpoonAssertions.assertThat(actual().getReference());
	}
}
