package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtLiteral;
public class CtLiteralAssert extends AbstractAssert<CtLiteralAssert, CtLiteral> {
	public CtLiteralAssert(CtLiteral actual) {
		super(actual, CtLiteralAssert.class);
	}
}
