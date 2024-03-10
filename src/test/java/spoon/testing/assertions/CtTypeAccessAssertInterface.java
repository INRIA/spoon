package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.code.CtTypeAccess;
public interface CtTypeAccessAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTypeAccess<?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> {
	default CtTypeReferenceAssertInterface<?, ?> getAccessedType() {
		return SpoonAssertions.assertThat(actual().getAccessedType());
	}

	default AbstractBooleanAssert<?> isImplicit() {
		return Assertions.assertThat(actual().isImplicit());
	}
}
