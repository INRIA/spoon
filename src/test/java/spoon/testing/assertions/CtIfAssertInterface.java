package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
public interface CtIfAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtIf> extends SpoonAssert<A, W> , CtStatementAssertInterface<A, W> {
	default CtExpressionAssertInterface<?, ?> getCondition() {
		return SpoonAssertions.assertThat(actual().getCondition());
	}

	default CtStatementAssertInterface<?, ?> getElseStatement() {
		return SpoonAssertions.assertThat(((CtStatement) (actual().getElseStatement())));
	}

	default CtStatementAssertInterface<?, ?> getThenStatement() {
		return SpoonAssertions.assertThat(((CtStatement) (actual().getThenStatement())));
	}
}
