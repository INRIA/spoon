package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtExpression;
import spoon.reflect.reference.CtTypeReference;
public interface CtExpressionAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtExpression<?>> extends SpoonAssert<A, W> , CtTypedElementAssertInterface<A, W> , CtCodeElementAssertInterface<A, W> {
	default ListAssert<CtTypeReference<?>> getTypeCasts() {
		return Assertions.assertThat(actual().getTypeCasts());
	}
}
