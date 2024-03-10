package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.reference.CtTypeReference;
public interface CtNewClassAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtNewClass<?>> extends SpoonAssert<A, W> , CtConstructorCallAssertInterface<A, W> {
	default ListAssert<CtTypeReference<?>> getActualTypeArguments() {
		return Assertions.assertThat(actual().getActualTypeArguments());
	}

	default CtClassAssertInterface<?, ?> getAnonymousClass() {
		return SpoonAssertions.assertThat(actual().getAnonymousClass());
	}
}
