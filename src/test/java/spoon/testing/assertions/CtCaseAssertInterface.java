package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import spoon.reflect.code.CaseKind;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;
public interface CtCaseAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtCase<?>> extends SpoonAssert<A, W> , CtStatementAssertInterface<A, W> , CtStatementListAssertInterface<A, W> {
	default ListAssert<CtExpression<?>> getCaseExpressions() {
		return Assertions.assertThat(actual().getCaseExpressions());
	}

	default ObjectAssert<CaseKind> getCaseKind() {
		return Assertions.assertThatObject(actual().getCaseKind());
	}

	default CtExpressionAssertInterface<?, ?> getGuard() {
		return SpoonAssertions.assertThat(actual().getGuard());
	}

	default AbstractBooleanAssert<?> getIncludesDefault() {
		return Assertions.assertThat(actual().getIncludesDefault());
	}
}
