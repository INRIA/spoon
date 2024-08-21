package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtAssert;
public interface CtAssertAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAssert<?>> extends SpoonAssert<A, W> , CtStatementAssertInterface<A, W> {
	default CtExpressionAssertInterface<?, ?> getAssertExpression() {
		return SpoonAssertions.assertThat(actual().getAssertExpression());
	}

	default CtExpressionAssertInterface<?, ?> getExpression() {
		return SpoonAssertions.assertThat(actual().getExpression());
	}
}
