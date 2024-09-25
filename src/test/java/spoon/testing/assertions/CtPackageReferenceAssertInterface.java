package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.reference.CtPackageReference;
public interface CtPackageReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtPackageReference> extends SpoonAssert<A, W> , CtReferenceAssertInterface<A, W> {
	default AbstractStringAssert<?> getSimpleName() {
		return Assertions.assertThat(actual().getSimpleName());
	}
}
