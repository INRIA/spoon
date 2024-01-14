package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtExpression;
public class CtExpressionAssert extends AbstractAssert<CtExpressionAssert, CtExpression> {
	public CtExpressionAssert(CtExpression actual) {
		super(actual, CtExpressionAssert.class);
	}
}
