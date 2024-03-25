package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCasePattern;
interface CtCasePatternAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtCasePattern> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> {
	default CtExpressionAssertInterface<?, ?> getGuard() {
		return SpoonAssertions.assertThat(actual().getGuard());
	}

	default CtPatternAssertInterface<?, ?> getPattern() {
		return SpoonAssertions.assertThat(actual().getPattern());
	}
}
