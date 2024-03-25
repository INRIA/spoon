package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtReturn;
public interface CtReturnAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtReturn<?>> extends CtCFlowBreakAssertInterface<A, W> , SpoonAssert<A, W> {
	default CtExpressionAssertInterface<?, ?> getReturnedExpression() {
		return SpoonAssertions.assertThat(actual().getReturnedExpression());
	}
}
