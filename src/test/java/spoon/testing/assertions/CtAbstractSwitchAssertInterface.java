package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtAbstractSwitch;
import spoon.reflect.code.CtCase;
public interface CtAbstractSwitchAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAbstractSwitch<?>> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
	default ListAssert<CtCase<?>> getCases() {
		return Assertions.assertThat(actual().getCases());
	}

	default CtExpressionAssertInterface<?, ?> getSelector() {
		return SpoonAssertions.assertThat(actual().getSelector());
	}
}
