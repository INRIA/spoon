package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtImport;
public interface CtImportAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtImport> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
	default CtReferenceAssertInterface<?, ?> getReference() {
		return SpoonAssertions.assertThat(actual().getReference());
	}
}
