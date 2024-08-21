package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.code.CtLocalVariable;
public interface CtLocalVariableAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtLocalVariable<?>> extends CtVariableAssertInterface<A, W> , SpoonAssert<A, W> , CtStatementAssertInterface<A, W> , CtRHSReceiverAssertInterface<A, W> , CtResourceAssertInterface<A, W> {
	default AbstractBooleanAssert<?> isInferred() {
		return Assertions.assertThat(actual().isInferred());
	}
}
