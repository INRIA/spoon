package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.UnaryOperatorKind;
public interface CtUnaryOperatorAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtUnaryOperator<?>> extends SpoonAssert<A, W> , CtStatementAssertInterface<A, W> , CtExpressionAssertInterface<A, W> {
	default ObjectAssert<UnaryOperatorKind> getKind() {
		return Assertions.assertThatObject(actual().getKind());
	}

	default CtExpressionAssertInterface<?, ?> getOperand() {
		return SpoonAssertions.assertThat(actual().getOperand());
	}
}
