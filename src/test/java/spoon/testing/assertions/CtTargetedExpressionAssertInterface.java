package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtTargetedExpression;
public interface CtTargetedExpressionAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTargetedExpression<?, ?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> {
	default CtExpressionAssertInterface<?, ?> getTarget() {
		return SpoonAssertions.assertThat(((CtExpression<?>) (actual().getTarget())));
	}
}
