package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.UnaryOperatorKind;
public class UnaryOperatorKindAssert extends AbstractAssert<UnaryOperatorKindAssert, UnaryOperatorKind> {
	public UnaryOperatorKindAssert(UnaryOperatorKind actual) {
		super(actual, UnaryOperatorKindAssert.class);
	}
}
