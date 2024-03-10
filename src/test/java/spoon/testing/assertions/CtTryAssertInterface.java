package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtTry;
public interface CtTryAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTry> extends CtBodyHolderAssertInterface<A, W> , SpoonAssert<A, W> , CtStatementAssertInterface<A, W> {
	default CtBlockAssertInterface<?, ?> getBody() {
		return SpoonAssertions.assertThat(actual().getBody());
	}

	default ListAssert<CtCatch> getCatchers() {
		return Assertions.assertThat(actual().getCatchers());
	}

	default CtBlockAssertInterface<?, ?> getFinalizer() {
		return SpoonAssertions.assertThat(actual().getFinalizer());
	}
}
