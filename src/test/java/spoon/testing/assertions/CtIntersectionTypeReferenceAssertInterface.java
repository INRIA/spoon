package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeReference;
public interface CtIntersectionTypeReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtIntersectionTypeReference<?>> extends CtTypeReferenceAssertInterface<A, W> , SpoonAssert<A, W> {
	default ListAssert<CtTypeReference<?>> getBounds() {
		return Assertions.assertThat(actual().getBounds());
	}
}
