package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.reference.CtWildcardReference;
public interface CtWildcardReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtWildcardReference> extends SpoonAssert<A, W> , CtTypeParameterReferenceAssertInterface<A, W> {
	default CtTypeReferenceAssertInterface<?, ?> getBoundingType() {
		return SpoonAssertions.assertThat(actual().getBoundingType());
	}

	default AbstractBooleanAssert<?> isUpper() {
		return Assertions.assertThat(actual().isUpper());
	}
}
