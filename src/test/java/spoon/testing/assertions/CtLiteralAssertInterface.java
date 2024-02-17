package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.LiteralBase;
public interface CtLiteralAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtLiteral<?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> {
	default ObjectAssert<LiteralBase> getBase() {
		return Assertions.assertThatObject(actual().getBase());
	}

	default ObjectAssert<?> getValue() {
		return Assertions.assertThatObject(actual().getValue());
	}
}
