package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtBodyHolder;
public interface CtBodyHolderAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtBodyHolder> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
	default CtStatementAssertInterface<?, ?> getBody() {
		return SpoonAssertions.assertThat(actual().getBody());
	}
}
