package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.CtMethod;
public interface CtMethodAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtMethod<?>> extends SpoonAssert<A, W> , CtExecutableAssertInterface<A, W> , CtFormalTypeDeclarerAssertInterface<A, W> , CtShadowableAssertInterface<A, W> {
	default AbstractBooleanAssert<?> isDefaultMethod() {
		return Assertions.assertThat(actual().isDefaultMethod());
	}
}
