package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.CtInterface;
public interface CtInterfaceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtInterface<?>> extends CtSealableAssertInterface<A, W> , SpoonAssert<A, W> , CtStatementAssertInterface<A, W> , CtTypeAssertInterface<A, W> {
	default AbstractStringAssert<?> getLabel() {
		return Assertions.assertThat(actual().getLabel());
	}
}
