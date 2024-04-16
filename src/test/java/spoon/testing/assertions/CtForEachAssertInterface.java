package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtForEach;
public interface CtForEachAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtForEach> extends SpoonAssert<A, W> , CtLoopAssertInterface<A, W> {
	default CtExpressionAssertInterface<?, ?> getExpression() {
		return SpoonAssertions.assertThat(actual().getExpression());
	}

	default CtLocalVariableAssertInterface<?, ?> getVariable() {
		return SpoonAssertions.assertThat(actual().getVariable());
	}
}
