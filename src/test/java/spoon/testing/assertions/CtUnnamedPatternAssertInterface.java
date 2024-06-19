package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtUnnamedPattern;
import spoon.reflect.reference.CtTypeReference;
public interface CtUnnamedPatternAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtUnnamedPattern> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> , CtPatternAssertInterface<A, W> {
	default ListAssert<CtTypeReference<?>> getTypeCasts() {
		return Assertions.assertThat(actual().getTypeCasts());
	}
}
