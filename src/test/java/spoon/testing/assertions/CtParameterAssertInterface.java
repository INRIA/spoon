package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.CtParameter;
public interface CtParameterAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtParameter<?>> extends CtVariableAssertInterface<A, W> , SpoonAssert<A, W> , CtShadowableAssertInterface<A, W> {
	default AbstractBooleanAssert<?> isInferred() {
		return Assertions.assertThat(actual().isInferred());
	}

	default AbstractBooleanAssert<?> isVarArgs() {
		return Assertions.assertThat(actual().isVarArgs());
	}
}
