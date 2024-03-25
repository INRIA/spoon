package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
public interface CtBinaryOperatorAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtBinaryOperator<?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> {
	default ObjectAssert<BinaryOperatorKind> getKind() {
		return Assertions.assertThatObject(actual().getKind());
	}

	default CtExpressionAssertInterface<?, ?> getLeftHandOperand() {
		return SpoonAssertions.assertThat(actual().getLeftHandOperand());
	}

	default CtExpressionAssertInterface<?, ?> getRightHandOperand() {
		return SpoonAssertions.assertThat(actual().getRightHandOperand());
	}
}
