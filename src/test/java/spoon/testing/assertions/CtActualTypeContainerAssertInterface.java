package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtTypeReference;
public interface CtActualTypeContainerAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtActualTypeContainer> extends SpoonAssert<A, W> {
	default ListAssert<CtTypeReference<?>> getActualTypeArguments() {
		return Assertions.assertThat(actual().getActualTypeArguments());
	}
}
