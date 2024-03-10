package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtLoop;
public interface CtLoopAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtLoop> extends CtBodyHolderAssertInterface<A, W> , SpoonAssert<A, W> , CtStatementAssertInterface<A, W> {
	default CtStatementAssertInterface<?, ?> getBody() {
		return SpoonAssertions.assertThat(actual().getBody());
	}
}
