package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtArrayAccess;
public interface CtArrayAccessAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtArrayAccess<?, ?>> extends SpoonAssert<A, W> , CtTargetedExpressionAssertInterface<A, W> {
	default CtExpressionAssertInterface<?, ?> getIndexExpression() {
		return SpoonAssertions.assertThat(actual().getIndexExpression());
	}
}
