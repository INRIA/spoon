package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtStatement;
public interface CtForAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtFor> extends SpoonAssert<A, W> , CtLoopAssertInterface<A, W> {
	default CtExpressionAssertInterface<?, ?> getExpression() {
		return SpoonAssertions.assertThat(actual().getExpression());
	}

	default ListAssert<CtStatement> getForInit() {
		return Assertions.assertThat(actual().getForInit());
	}

	default ListAssert<CtStatement> getForUpdate() {
		return Assertions.assertThat(actual().getForUpdate());
	}
}
