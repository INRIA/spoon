package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.CtConstructor;
public interface CtConstructorAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtConstructor<?>> extends SpoonAssert<A, W> , CtExecutableAssertInterface<A, W> , CtFormalTypeDeclarerAssertInterface<A, W> , CtShadowableAssertInterface<A, W> {
	default AbstractStringAssert<?> getSimpleName() {
		return Assertions.assertThat(actual().getSimpleName());
	}

	default AbstractBooleanAssert<?> isCompactConstructor() {
		return Assertions.assertThat(actual().isCompactConstructor());
	}
}
