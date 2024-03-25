package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtYieldStatement;
public interface CtYieldStatementAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtYieldStatement> extends CtCFlowBreakAssertInterface<A, W> , SpoonAssert<A, W> {
	default CtExpressionAssertInterface<?, ?> getExpression() {
		return SpoonAssertions.assertThat(actual().getExpression());
	}
}
