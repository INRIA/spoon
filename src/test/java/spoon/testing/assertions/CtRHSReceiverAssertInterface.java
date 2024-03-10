package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtRHSReceiver;
public interface CtRHSReceiverAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtRHSReceiver<?>> extends SpoonAssert<A, W> {
	default CtExpressionAssertInterface<?, ?> getAssignment() {
		return SpoonAssertions.assertThat(actual().getAssignment());
	}
}
