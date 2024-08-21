package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.CtShadowable;
public interface CtShadowableAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtShadowable> extends SpoonAssert<A, W> {
	default AbstractBooleanAssert<?> isShadow() {
		return Assertions.assertThat(actual().isShadow());
	}
}
