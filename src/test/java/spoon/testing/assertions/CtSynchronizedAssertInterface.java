package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtSynchronized;
public interface CtSynchronizedAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtSynchronized> extends SpoonAssert<A, W> , CtStatementAssertInterface<A, W> {
	default CtBlockAssertInterface<?, ?> getBlock() {
		return SpoonAssertions.assertThat(actual().getBlock());
	}

	default CtExpressionAssertInterface<?, ?> getExpression() {
		return SpoonAssertions.assertThat(actual().getExpression());
	}
}
