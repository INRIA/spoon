package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.BinaryOperatorKind;
public class BinaryOperatorKindAssert extends AbstractAssert<BinaryOperatorKindAssert, BinaryOperatorKind> {
	public BinaryOperatorKindAssert(BinaryOperatorKind actual) {
		super(actual, BinaryOperatorKindAssert.class);
	}
}
