package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtLambda;
public class CtLambdaAssert extends AbstractAssert<CtLambdaAssert, CtLambda> {
	public CtLambdaAssert(CtLambda actual) {
		super(actual, CtLambdaAssert.class);
	}
}
