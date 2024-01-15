package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import spoon.reflect.reference.CtPackageReference;
interface CtPackageReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtPackageReference> extends SpoonAssert<A, W> , CtReferenceAssertInterface<A, W> {
    default AbstractStringAssert<?> getSimpleName() {
        return org.assertj.core.api.Assertions.assertThat(actual().getSimpleName());
    }
}