package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtThrow;
public interface CtThrowAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtThrow> extends CtCFlowBreakAssertInterface<A, W> , SpoonAssert<A, W> {
	default CtExpressionAssertInterface<?, ?> getThrownExpression() {
		return SpoonAssertions.assertThat(actual().getThrownExpression());
	}
}
