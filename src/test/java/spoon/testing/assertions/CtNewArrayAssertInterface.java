package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewArray;
public interface CtNewArrayAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtNewArray<?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> {
	default ListAssert<CtExpression<Integer>> getDimensionExpressions() {
		return Assertions.assertThat(actual().getDimensionExpressions());
	}

	default ListAssert<CtExpression<?>> getElements() {
		return Assertions.assertThat(actual().getElements());
	}
}
