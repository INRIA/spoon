package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.code.CtStatement;
public interface CtStatementAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtStatement> extends SpoonAssert<A, W> , CtCodeElementAssertInterface<A, W> {
	default AbstractStringAssert<?> getLabel() {
		return Assertions.assertThat(actual().getLabel());
	}
}
