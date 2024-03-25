package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCatch;
public interface CtCatchAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtCatch> extends CtBodyHolderAssertInterface<A, W> , SpoonAssert<A, W> , CtCodeElementAssertInterface<A, W> {
	default CtBlockAssertInterface<?, ?> getBody() {
		return SpoonAssertions.assertThat(actual().getBody());
	}

	default CtCatchVariableAssertInterface<?, ?> getParameter() {
		return SpoonAssertions.assertThat(actual().getParameter());
	}
}
