package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtWhile;
public interface CtWhileAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtWhile> extends SpoonAssert<A, W> , CtLoopAssertInterface<A, W> {
	default CtExpressionAssertInterface<?, ?> getLoopingExpression() {
		return SpoonAssertions.assertThat(actual().getLoopingExpression());
	}
}
