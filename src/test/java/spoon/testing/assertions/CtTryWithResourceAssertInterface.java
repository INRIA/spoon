package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtResource;
import spoon.reflect.code.CtTryWithResource;
public interface CtTryWithResourceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTryWithResource> extends SpoonAssert<A, W> , CtTryAssertInterface<A, W> {
	default ListAssert<CtResource<?>> getResources() {
		return Assertions.assertThat(actual().getResources());
	}
}
