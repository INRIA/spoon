package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtConditional;
public interface CtConditionalAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtConditional<?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> {
	default CtExpressionAssertInterface<?, ?> getCondition() {
		return SpoonAssertions.assertThat(actual().getCondition());
	}

	default CtExpressionAssertInterface<?, ?> getElseExpression() {
		return SpoonAssertions.assertThat(actual().getElseExpression());
	}

	default CtExpressionAssertInterface<?, ?> getThenExpression() {
		return SpoonAssertions.assertThat(actual().getThenExpression());
	}
}
